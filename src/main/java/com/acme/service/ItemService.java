package com.acme.service;


import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.dto.ItemMediaTransfer;
import com.acme.model.dto.ItemUrlTransfer;

import java.util.List;

public interface ItemService {

    List<ItemUrlTransfer> getItemUrlTransfers(List<Item> items);

    ItemMediaTransfer getItemMediaTransfers(Item item);

    /**
     * Пересчет кол-ва товара из-за создания заказа
     * @param orderId
     */
//    void decreaseCountByOrder(String orderId);

    /**
     * Пересчет кол-ва товара из-за отмены или удаления заказа
     * @param orderId
     */
    void increaseCountByOrder(String orderId);

    void fillItem(Item item, Content defContent);
}
