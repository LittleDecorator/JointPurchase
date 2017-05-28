package com.acme.repository;

import com.acme.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by nikolay on 22.02.17.
 *
 */
public interface CategoryRepository extends CrudRepository<Category, String> {

    List<Category> findByParentId(String categoryId);

    List<Category> findByIdIn(List<String> ids);

    @Query(value = "select id, name, parent_id, date_add from get_child_categories(:id)", nativeQuery = true)
    List<Category> getChildCategories(@Param("id") String id);

}
