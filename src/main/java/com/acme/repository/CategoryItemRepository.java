package com.acme.repository;

import com.acme.model.CategoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 20.02.17.
 */
public interface CategoryItemRepository extends CrudRepository<CategoryItem, String> {

    List<CategoryItem> findAllByItemId(String itemId);

    List<CategoryItem> findAllByCategoryId(String categoryId);

    List<CategoryItem> findAllByCategoryIdIn(List<String> ids);

    List<CategoryItem> findAllByItemIdIn(List<String> ids);

    void deleteByItemId(String itemId);

    void deleteByCategoryId(String categoryId);

    void deleteByItemIdAndCategoryIdNotIn(String itemId, List<String> categoryIdList);

    void deleteByCategoryIdAndItemIdNotIn(String categoryId, List<String> itemIdList);

    Page<CategoryItem> findAll(Pageable pageable);

}
