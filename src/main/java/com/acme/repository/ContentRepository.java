package com.acme.repository;

import com.acme.model.Content;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


/**
 * Created by nikolay on 19.02.17.
 *
 */
public interface ContentRepository extends CrudRepository<Content, String> {

    Content findOneByIsDefault(boolean isDefault);

    List<Content> findAllByIsInstagramTrue();

    List<Content> findAllByIdIn(List<String> ids);

    void deleteAllByIdIn(List<String> ids);
}
