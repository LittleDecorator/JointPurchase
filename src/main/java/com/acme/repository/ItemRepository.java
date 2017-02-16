package com.acme.repository;

import com.acme.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 11.02.17.
 *
 */
public interface ItemRepository extends CrudRepository<Item, String> {

    /**
     * Получаем все записи с сортировкой по дате
     * @return
     */
    List<Item> findAllByOrderByDateAddAsc();

    List<Item> findByCompanyId(String companyId);

    List<Item> findByIdIn(List<String> ids);

}
