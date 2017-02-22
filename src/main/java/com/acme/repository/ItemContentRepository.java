package com.acme.repository;

import com.acme.model.ItemContent;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by nikolay on 20.02.17.
 */
public interface ItemContentRepository extends CrudRepository<ItemContent, String> {
}
