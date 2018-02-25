package com.acme.service;


import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.Product;
import com.acme.model.dto.ItemMediaTransfer;
import com.acme.model.dto.ItemUrlTransfer;
import com.acme.model.filter.CatalogFilter;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface ItemService {

    List<Item> getAll(CatalogFilter filter);

    List<Item> getAll();

    List<Item> getPortion(int offset, int limit);

    List<Item> getAllBySpec(Specification<Item> specification);

    List<Item> getAllByCategory(CatalogFilter filter);

    List<Item> getAllByCompanyId(String companyId);

    List<Item> getAllByIdList(List<String> ids);

    Item getItem(String itemId);

    Item getItemByLatinName(String name);

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

    void fillItem(Product item, Content defContent);

    void updateItem(Item item);
}
