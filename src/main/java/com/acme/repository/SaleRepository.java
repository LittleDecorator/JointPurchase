package com.acme.repository;

import com.acme.model.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nikolay on 11.12.17.
 */
public interface SaleRepository extends JpaRepository<Sale, String>, JpaSpecificationExecutor<Sale> {

    Sale findOneByTransliteName(String transliteName);

    @Modifying
    @Transactional
    @Query("update Sale s set s.active = false where s.active=true and s.endDate < current_timestamp")
    void deactiveteSales();

}
