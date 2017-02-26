package com.acme.repository;

import com.acme.model.CategoryItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 20.02.17.
 */
public interface CategoryItemRepository extends CrudRepository<CategoryItem, String> {

    List<CategoryItem> findAllByItemId(String itemId);

    void deleteByItemId(String itemId);

    void deleteByItemIdAndCategoryIdNotIn(String itemId, List<String> categoryIdList);

}
