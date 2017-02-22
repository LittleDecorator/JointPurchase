package com.acme.repository;

import com.acme.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 22.02.17.
 *
 */
public interface CategoryRepository extends CrudRepository<Category, String> {

    List<Category> findByParentId(String categoryId);

}
