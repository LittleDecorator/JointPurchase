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

    Category findOneByTransliteName(String name);

    @Query(value = "select * from get_child_categories(:id)", nativeQuery = true)
    List<Category> getChildCategories(@Param("id") String id);

    @Query(value = "select * from get_root_categories_for_company(:companyId)", nativeQuery = true)
    List<Category> getRootCategories(@Param("companyId") String companyId);

}
