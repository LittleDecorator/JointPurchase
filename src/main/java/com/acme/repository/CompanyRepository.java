package com.acme.repository;

import com.acme.model.Company;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by nikolay on 22.02.17.
 *
 */
public interface CompanyRepository extends CrudRepository<Company, String> {

    Company findOneByTransliteName(String name);

    Company findOneById(String id);

}
