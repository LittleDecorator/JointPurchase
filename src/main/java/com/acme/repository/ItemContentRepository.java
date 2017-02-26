package com.acme.repository;

import com.acme.model.ItemContent;
import com.acme.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 20.02.17.
 */
public interface ItemContentRepository extends CrudRepository<ItemContent, String> {

    void deleteByItemId(String itemId);

    int countByItemId(String itemId);

    List<ItemContent> findAllByItemId(String itemId);

    ItemContent findByItemIdAndContentId(String itemId, String contentId);

}
