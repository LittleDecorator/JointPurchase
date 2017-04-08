package com.acme.service.impl;

import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.OrderItem;
import com.acme.model.dto.ItemMediaTransfer;
import com.acme.model.dto.ItemUrlTransfer;
import com.acme.repository.ContentRepository;
import com.acme.repository.ItemContentRepository;
import com.acme.repository.ItemRepository;
import com.acme.repository.OrderItemRepository;
import com.acme.repository.OrderRepository;
import com.acme.service.ItemService;
import com.acme.constant.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService{

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

    @Override
    public List<ItemUrlTransfer> getItemUrlTransfers(List<Item> items) {
        List<ItemUrlTransfer> result = Lists.newArrayList();
        if(items !=null && !items.isEmpty()){
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
        if(item!=null){
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
        for(OrderItem orderItem : orderItems){
            Item item = itemRepository.findOne(orderItem.getItemId());
            item.setInStock(item.getInStock() + orderItem.getCount());
            item.setInOrder(item.getInOrder() - orderItem.getCount());
            itemRepository.save(item);
        }
    }
}
