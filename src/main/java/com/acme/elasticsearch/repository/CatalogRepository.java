package com.acme.elasticsearch.repository;

import com.acme.model.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by nikolay on 25.02.17.
 *
 */

@Repository
public interface CatalogRepository extends ElasticsearchRepository<Item, String> {
}
