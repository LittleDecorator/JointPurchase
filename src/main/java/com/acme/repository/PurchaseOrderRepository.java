package com.acme.repository;

import com.acme.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by kobzev on 16.02.17.
 *
 * Репозиторий доступа к заказам
 */
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String>, JpaSpecificationExecutor {}
