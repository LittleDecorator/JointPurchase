package com.acme.repository;

import com.acme.enums.OrderStatus;
import com.acme.model.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kobzev on 16.02.17.
 *
 * Репозиторий доступа к заказам
 */
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    List<Order> findAllByStatusIn(List<OrderStatus> statuses);

    List<Order> findAllBySubjectId(String subjectId);

    @Transactional(noRollbackFor = {Exception.class, EmptyResultDataAccessException.class})
    String deleteBySubjectId(String subjectId);

}
