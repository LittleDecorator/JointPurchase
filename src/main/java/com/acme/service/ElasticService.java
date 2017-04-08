package com.acme.service;

import com.acme.model.Item;

import java.util.List;

/**
 * Created by kobzev on 08.04.17.
 */
public interface ElasticService {

	/**
	 * Индексация товара в хранилище поиска
	 * @param items
	 */
	void indexItems(List<Item> items);

	/**
	 * Индексация товара в хранилище поиска
	 * @param item
	 */
	void indexItem(Item item);

}
