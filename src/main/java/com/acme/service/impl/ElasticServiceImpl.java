package com.acme.service.impl;

import com.acme.model.Item;
import com.acme.service.ElasticService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kobzev on 08.04.17.
 */
@Service
public class ElasticServiceImpl implements ElasticService {

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public void indexItems(List<Item> items) {
		List<IndexQuery> indexQueries = Lists.newArrayList();
        /* найдем все документы из "Товар" для включения в индекс */
		for(Item item : items){
			IndexQuery indexQuery = new IndexQueryBuilder().withIndexName("item-index").withId(item.getId()).withObject(item).build();
			indexQueries.add(indexQuery);
		}
        /* Добавление документов в индекс */
		elasticsearchTemplate.bulkIndex(indexQueries);
	}

	@Override
	public void indexItem(Item item) {
		IndexQuery indexQuery = new IndexQueryBuilder().withId(item.getId()).withObject(item).build();
		elasticsearchTemplate.index(indexQuery);
	}
}
