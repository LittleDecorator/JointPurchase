package com.acme.service.impl;

import com.acme.model.CategoryItem;
import com.acme.model.Company;
import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.OrderItem;
import com.acme.model.dto.ItemMediaTransfer;
import com.acme.model.dto.ItemUrlTransfer;
import com.acme.model.filter.CatalogFilter;
import com.acme.repository.*;
import com.acme.repository.specification.CatalogSpecifications;
import com.acme.service.ItemService;
import com.acme.constant.Constants;
import com.google.common.collect.Lists;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemContentRepository itemContentRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    private CategoryItemRepository categoryItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Override
    public List<Item> getAll(CatalogFilter filter) {
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.ASC, "status", "name");
        return Lists.newArrayList(itemRepository.findAll(CatalogSpecifications.filter(filter), pageable));
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public List<Item> getAllByCategory(CatalogFilter filter) {
        //TODO: УБРАТЬ ЭТОТ УЖАСНЕЙШИЙ КОСТЫЛЬ
        if(Strings.isNullOrEmpty(filter.getCompany())){
            return itemRepository.findAllByCategoryId(filter.getCategory(), filter.getOffset(), filter.getLimit());
        }
        return itemRepository.findAllByCategoryId(filter.getCategory(), filter.getCompany(), filter.getOffset(), filter.getLimit());
    }

    @Override
    public List<Item> getAllByCompanyId(String companyId) {
        return itemRepository.findAllByCompanyId(companyId);
    }

    @Override
    public Item getItem(String itemId) {
        return itemRepository.findOne(itemId);
    }

    @Override
    public Item getItemByLatinName(String name) {
        return itemRepository.findOneByTransliteName(name);
    }

    @Override
    public List<ItemUrlTransfer> getItemUrlTransfers(List<Item> items) {
        List<ItemUrlTransfer> result = Lists.newArrayList();
        if (items != null && !items.isEmpty()) {
//            Content defContent = contentRepository.getDefault();
//            String noImage = Constants.PREVIEW_URL+defContent.getId();
//            HashMap<String,String> itemContents = Maps.newHashMap(itemContentRepository.getMain().stream().collect(Collectors.toMap(ItemContent::getItemId,ItemContent::getContentId)));
//            for(Item item : items){
//                ItemUrlTransfer transfer = new ItemUrlTransfer();
//                transfer.setId(item.getId());
//                transfer.setName(item.getName());
//                transfer.setPrice(item.getPrice());
//                transfer.setDescription(item.getDescription());
//                if(itemContents.containsKey(item.getId())){
//                    transfer.setUrl(Constants.ORIG_URL+ (itemContents.remove(item.getId())));
//                } else {
//                    transfer.setUrl(noImage);
//                }
//                result.add(transfer);
//            }
        }
        return result;
    }

    @Override
    public ItemMediaTransfer getItemMediaTransfers(Item item) {
        ItemMediaTransfer transfer = null;
        if (item != null) {
            transfer = new ItemMediaTransfer();
//            Content defContent = contentRepository.getDefault();
//            List<ItemContent> itemContents = itemContentRepository.getShowedByItemId(item.getId());
//            if(!itemContents.isEmpty()){
//                transfer.getMedia().addAll(itemContents.stream().map(ItemContent::getContentId).collect(Collectors.toList()));
//            } else {
//                transfer.getMedia().add(defContent.getId());
//            }
            transfer.setId(item.getId());
            transfer.setName(item.getName());
            transfer.setPrice(item.getPrice());
            transfer.setDescription(item.getDescription());
        }
        return transfer;
    }

//    @Override
//    public void decreaseCountByOrder(String orderId) {
//        // обновим кол-во товара в наличие
//        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
//        for(OrderItem orderItem : orderItems){
//            Item item = itemRepository.findOne(orderItem.getItemId());
//            item.setInStock(item.getInStock() - orderItem.getCount());
//            item.setInOrder(item.getInOrder() + orderItem.getCount());
//            itemRepository.save(item);
//        }
//    }

    @Override
    public void increaseCountByOrder(String orderId) {
        // обновим кол-во товара в наличие
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Item item = itemRepository.findOne(orderItem.getItemId());
            item.setInStock(item.getInStock() + orderItem.getCount());
            item.setInOrder(item.getInOrder() - orderItem.getCount());
            itemRepository.save(item);
        }
    }

    public void fillItem(Item item, Content defContent) {
        List<ItemContent> itemContents = itemContentRepository.findAllByItemId(item.getId());
        if (itemContents.isEmpty()) {
            item.setUrl(Constants.PREVIEW_URL + defContent.getId());
        } else {
            item.setItemContents(itemContents);
            Optional<ItemContent> contentOptional = itemContents.stream().filter(ItemContent::isMain).findAny();
            if(contentOptional.isPresent()){
                item.setUrl(Constants.PREVIEW_URL + contentOptional.get().getContentId());
            }
        }
        item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
    }

    @Override
    public void updateItem(Item item) {
        if (item != null) {
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                itemRepository.save(item);
                transactionManager.commit(status);
            } catch (Exception ex) {
                ex.printStackTrace();
                transactionManager.rollback(status);
            }
        }
    }

    @Override
    public List<Item> getAllByIdList(List<String> ids) {
        return itemRepository.findByIdIn(ids);
    }
}
