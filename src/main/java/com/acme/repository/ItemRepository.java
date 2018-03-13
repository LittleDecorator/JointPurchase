package com.acme.repository;

import com.acme.model.Item;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by nikolay on 11.02.17.
 *
 */
public interface ItemRepository extends JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {

    /**
     * Получаем все записи с сортировкой по дате
     * @return
     */
    Set<Item> findAllByOrderByDateAddAsc();

    Set<Item> findAllByCompanyId(String companyId);

    Set<Item> findAllByIdIn(List<String> ids);

    Item findOneByTransliteName(String name);

    @Query(value = "select distinct t.* from get_limited_category_items(:categoryId, :offset, :limit) t", nativeQuery = true)
    Set<Item> findAllByCategoryId(@Param("categoryId") String categoryId, @Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "select distinct t.* from get_limited_category_items(:categoryId, :companyId, :offset, :limit) t", nativeQuery = true)
    Set<Item> findAllByCategoryId(@Param("categoryId") String categoryId, @Param("companyId") String companyId, @Param("offset") int offset, @Param("limit") int limit);

}
