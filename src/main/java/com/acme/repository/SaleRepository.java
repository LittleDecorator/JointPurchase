package com.acme.repository;

import com.acme.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by nikolay on 11.12.17.
 */
public interface SaleRepository extends JpaRepository<Sale, String>, JpaSpecificationExecutor<Sale> {

    Sale findOneByTransliteName(String transliteName);

}
