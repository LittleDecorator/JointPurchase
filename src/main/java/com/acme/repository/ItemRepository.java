package com.acme.repository;

import com.acme.model.Item;
import com.acme.model.OrderItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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

}
