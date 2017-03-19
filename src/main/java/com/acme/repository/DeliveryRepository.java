package com.acme.repository;

import com.acme.model.Delivery;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by nikolay on 19.03.17.
 *
 * Репозиторий таблицы "Способ доставки"
 */
public interface DeliveryRepository extends CrudRepository<Delivery, String> {
}
