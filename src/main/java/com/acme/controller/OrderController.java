package com.acme.controller;

import com.acme.exception.TemplateException;
import com.acme.model.Item;
import com.acme.model.OrderItem;
import com.acme.model.PurchaseOrder;
import com.acme.model.OrderView;
import com.acme.model.filter.OrderFilter;
import com.acme.repository.specification.OrderViewSpecifications;
import com.acme.repository.*;
import com.acme.service.AuthService;
import com.acme.service.EmailService;
import com.google.common.collect.Lists;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/order")
public class OrderController {

	@Autowired
	PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	PurchaseOrderViewRepository purchaseOrderViewRepository;

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
		/* выставляем offset, limit и order by */
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "create_order_date");
		return Lists.newArrayList(purchaseOrderViewRepository.findAll(OrderViewSpecifications.filter(filter), pageable).iterator());
	}

	/**
	 * Получение заказа по ID
	 * @param id - order ID
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public PurchaseOrder getOrder(@PathVariable("id") String id) {
		return purchaseOrderRepository.findOne(id);
	}

	/**
	 * Получение заказов по ID клиента
	 * @param id - customer ID
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/customer/{id}")
	public List<OrderView> getCustomerOrders(@PathVariable("id") String id) {
		return purchaseOrderViewRepository.findAll(OrderViewSpecifications.customer(id));
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
	public PurchaseOrder privateOrderProcess(@RequestBody OrderRequest request) throws ParseException, IOException, MessagingException, TemplateException {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
		request.getOrder().setSubjectId(authService.getClaims(servletRequest).getId());
		PurchaseOrder purchaseOrder = persistOrder(request);
		emailService.sendOrderStatus(purchaseOrder);
		return purchaseOrder;
	}

	/**
	 * Получение истории заказов авторизованного клиента
	 * @return
	 */
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public Page<OrderView> getOrderHistory(OrderFilter filter) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
		filter.setSubjectId(authService.getClaims(servletRequest).getId());
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "create_order_date");
		return purchaseOrderViewRepository.findAll(OrderViewSpecifications.filter(filter), pageable);
	}

	/**
	 * Создание заказа
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public PurchaseOrder persistOrder(@RequestBody OrderRequest request) throws ParseException, IOException {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {

			/* сохраним заказ из запроса. */
			PurchaseOrder order = purchaseOrderRepository.save(request.getOrder());

			/* добавим записи в таблицу связи заказ-товар */
			for (OrderItemsList itemsList : request.getItems()) {
				// собираем товар для дальнейшей обработки
				OrderItem orderItem = new OrderItem();
				orderItem.setItem(itemsList.getItem());
				orderItem.setOrder(order);
				orderItem.setCount(orderItem.getCount());
				orderItemRepository.save(orderItem);
			}

			//удаляем записи, где заказ совпадает, а товар нет.
			List<Item> items = request.getItems().stream().map(OrderItemsList::getItem).collect(Collectors.toList());
			orderItemRepository.deleteByOrderAndItemNotIn(order, items);

			transactionManager.commit(status);
			return order;
		} catch (Exception e) {
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
		PurchaseOrder order = purchaseOrderRepository.findOne(id);
		//delete order bind items
		orderItemRepository.deleteByOrder(order);
		//delete order itself
		purchaseOrderRepository.delete(id);
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
		PurchaseOrder order = purchaseOrderRepository.findOne(id);
		result.addAll(order.getOrderItems().stream().map(orderItem -> new OrderItemsList(orderItem.getItem(), orderItem.getCount())).collect(Collectors.toList()));
		return result;
	}

    /* ------ NESTED CLASSES ------ */

	/**
	 * Класс представляет собой содержимое заказа.
	 */
	private class OrderRequest {

		private PurchaseOrder order;
		private List<OrderItemsList> items;

		public PurchaseOrder getOrder() {
			return order;
		}

		public void setOrder(PurchaseOrder order) {
			this.order = order;
		}

		public List<OrderItemsList> getItems() {
			return items;
		}

		public void setItems(List<OrderItemsList> items) {
			this.items = items;
		}
	}

	/**
	 * Класс представляет собой содержимое заказа.
	 * Детали заказа убраны.
	 */
	private class OrderItemsList {

		Item item;
		Integer count;

		OrderItemsList(Item item, Integer count) {
			this.item = item;
			this.count = count;
		}

		public Item getItem() {
			return item;
		}

		public void setItem(Item item) {
			this.item = item;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}
}
