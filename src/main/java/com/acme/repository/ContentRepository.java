package com.acme.repository;

import com.acme.model.Content;
import org.springframework.data.repository.CrudRepository;


/**
 * Created by nikolay on 19.02.17.
 *
 */
public interface ContentRepository extends CrudRepository<Content, String> {

    Content findOneByIsDefault(boolean isDefault);

}
