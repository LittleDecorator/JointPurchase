package com.acme.controller;

import com.acme.enums.OrderStatus;
import com.acme.exception.TemplateException;
import com.acme.model.Item;
import com.acme.model.Order;
import com.acme.model.OrderItem;
import com.acme.model.OrderView;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.model.filter.OrderFilter;
import com.acme.repository.specification.OrderViewSpecifications;
import com.acme.repository.*;
import com.acme.service.AuthService;
import com.acme.service.EmailService;
import com.google.common.collect.Lists;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/order")
public class OrderController {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderViewRepository orderViewRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	AuthService authService;

	@Autowired
	EmailService emailService;

	@Autowired
	OrderItemRepository  orderItemRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;


	/**
	 * Получение всех заказов по фильтру
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public List<OrderView> getOrders(OrderFilter filter) {
		/* Сортировка видимо будет идти по модели, и в запросе не участвует */
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "createDate");
		return Lists.newArrayList(orderViewRepository.findAll(OrderViewSpecifications.filter(filter), pageable).iterator());
	}

	/**
	 * Получение заказа по ID
	 * @param id - order ID
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public Order getOrder(@PathVariable("id") String id) {
		return orderRepository.findOne(id);
	}

	/**
	 * Отмена заказа по ID
	 * @param id - order ID
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/cancel")
	public Order cancelOrder(@PathVariable("id") String id) {
		Order order = orderRepository.findOne(id);
		// получим заблокированные статусы
		List<OrderStatus> lockedStatuses = Lists.newArrayList(OrderStatus.CANCELED, OrderStatus.DONE);
		if(!lockedStatuses.contains(order.getStatus())){
			// изменим статус заказа если возможно
			TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
			try{
				order.setStatus(OrderStatus.CANCELED);
				orderRepository.save(order);
				transactionManager.commit(status);
				//отправляем письмо об изменение статуса заказа
				emailService.sendOrderStatus(order);
			} catch (Exception ex){
				ex.printStackTrace(System.out);
				transactionManager.rollback(status);
			}
		} else {
			order = null;
		}
		return order;
	}

	/**
	 * Получение заказов по ID клиента
	 * @param id - customer ID
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/customer/{id}")
	public List<OrderView> getCustomerOrders(@PathVariable("id") String id) {
		return orderViewRepository.findAll(OrderViewSpecifications.customer(id));
	}

	/**
	 * Создание заказа авторизованного клиента.
	 * Клиенту также будет отправлено уведомление (sms|email) о создание заказа
	 *
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws MessagingException
	 * @throws TemplateException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/personal")
	public Order privateOrderProcess(@RequestBody OrderRequest request) throws ParseException, IOException, MessagingException, TemplateException {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
		request.getOrder().setSubjectId(authService.getClaims(servletRequest).getId());
		return persistOrder(request);
	}

	/**
	 * Получение истории заказов авторизованного клиента
	 * @return
	 */
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public List<OrderView> getOrderHistory(OrderFilter filter) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
		filter.setSubjectId(authService.getClaims(servletRequest).getId());
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "createDate");
		return Lists.newArrayList(orderViewRepository.findAll(OrderViewSpecifications.filter(filter), pageable).iterator());
	}

	/**
	 * Создание заказа
	 *
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Order persistOrder(@RequestBody OrderRequest request) throws ParseException, IOException {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			/* сохраним заказ из запроса. */
			request.getOrder().setStatus(OrderStatus.NEW);
			Order order = orderRepository.save(request.getOrder());

			/* добавим записи в таблицу связи заказ-товар */
			if(request.getItems() == null || request.getItems().isEmpty()){
				throw new IllegalArgumentException("NO items in request");
			}
			for (OrderItemsList itemsList : request.getItems()) {
				// собираем товар для дальнейшей обработки
				OrderItem orderItem = new OrderItem();
				orderItem.setItemId(itemsList.getItem().getId());
				orderItem.setOrderId(order.getId());
				orderItem.setCount(itemsList.getCount());
				orderItemRepository.save(orderItem);
				// изменить кол-во товара в наличие
				Item item = itemRepository.findOne(itemsList.getItem().getId());
				item.setInStock(item.getInStock() - itemsList.getCount());
				itemRepository.save(item);
			}

			//удаляем записи, где заказ совпадает, а товар нет.
			List<String> itemIdList = request.getItems().stream().map(OrderItemsList::getItem).map(Item::getId).collect(Collectors.toList());
			orderItemRepository.deleteByOrderIdAndItemIdNotIn(order.getId(), itemIdList);
			transactionManager.commit(status);
			/* отправляем на почту писмо с подтверждением заказа */
			emailService.sendOrderStatus(order);
			return order;
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			transactionManager.rollback(status);
			return null;
		}
	}

	/**
	 * Удаление заказа по ID
	 * @param id - order ID
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public void deleteOrder(@PathVariable("id") String id) {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try{
			//delete order bind items
			orderItemRepository.deleteByOrderId(id);
			//delete order itself
			orderRepository.delete(id);
			transactionManager.commit(status);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			transactionManager.rollback(status);
		}
	}



	/**
	 * Получение товара из заказа
	 * @param id - order ID
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/items")
	public List<OrderItemsList> getOrderItems(@PathVariable("id") String id) {
		List<OrderItemsList> result = Lists.newArrayList();
		// получим заказ
		List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(id);
		List<Item> items = itemRepository.findByIdIn(orderItems.stream().map(OrderItem::getItemId).collect(Collectors.toList()));
        Map<String, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
		result.addAll(orderItems.stream().map(orderItem -> new OrderItemsList(itemMap.get(orderItem.getItemId()), orderItem.getCount())).collect(Collectors.toList()));
		return result;
	}
}
