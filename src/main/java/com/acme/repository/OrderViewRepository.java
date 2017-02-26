package com.acme.repository;

import com.acme.model.OrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by kobzev on 16.02.17.
 *
 * Репозиторий доступа к представлению заказов
 */
public interface OrderViewRepository extends JpaRepository<OrderView, String>, JpaSpecificationExecutor<OrderView> {

	List<OrderView> findAllByOrderByCreateDateDesc();

}
