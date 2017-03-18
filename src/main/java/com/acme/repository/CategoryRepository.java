package com.acme.repository;

import com.acme.model.Category;
import com.acme.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 22.02.17.
 *
 */
public interface CategoryRepository extends CrudRepository<Category, String> {

    List<Category> findByParentId(String categoryId);

    List<Category> findByIdIn(List<String> ids);

}
