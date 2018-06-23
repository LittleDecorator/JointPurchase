package com.acme.repository;

import com.acme.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nikolay on 22.02.17.
 *
 */
public interface CompanyRepository extends JpaRepository<Company, String> {

    Company findOneByTransliteName(String name);

    Company findOneById(String id);

}
