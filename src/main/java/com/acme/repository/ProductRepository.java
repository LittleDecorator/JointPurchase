package com.acme.repository;

import com.acme.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Catalog, String>, JpaSpecificationExecutor<Catalog> {
}
