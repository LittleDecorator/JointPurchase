package com.acme.repository;

import com.acme.model.CategoryItem;
import com.acme.model.embedded.CategoryItemId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 20.02.17.
 */
public interface CategoryItemRepository extends CrudRepository<CategoryItem, CategoryItemId> {

    List<CategoryItem> findAllByIdItemId(String itemId);

    List<CategoryItem> findAllByIdCategoryId(String categoryId);

    List<CategoryItem> findAllByIdCategoryIdIn(List<String> ids);

    List<CategoryItem> findAllByIdItemIdIn(List<String> ids);

    void deleteByIdItemId(String itemId);

    void deleteByIdCategoryId(String categoryId);

    void deleteByIdItemIdAndIdCategoryIdNotIn(String itemId, List<String> categoryIdList);

    void deleteByIdCategoryIdAndIdItemIdNotIn(String categoryId, List<String> itemIdList);

    Page<CategoryItem> findAll(Pageable pageable);

}
