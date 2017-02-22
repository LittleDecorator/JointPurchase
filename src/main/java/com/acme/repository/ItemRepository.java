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

    List<Item> findByOrderId(@Param("ID") String ID);

}
