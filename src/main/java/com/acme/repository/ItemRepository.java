package com.acme.repository;

import com.acme.model.Item;
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
    List<Item> findAllByOrderByDateAddAsc();

    List<Item> findByCompanyId(String companyId);

    List<Item> findByIdIn(List<String> ids);

//    List<Item> findByOrderItems(List<OrderItem> orderItems);

    @Query(value = "select * from get_limited_category_items(:categoryId, :offset, :limit)", nativeQuery = true)
    List<Item> findAllByCategoryId(@Param("categoryId") String categoryId, @Param("offset") int offset, @Param("limit") int limit);

}
