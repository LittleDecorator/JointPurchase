package com.acme.util;

import java.util.HashMap;

/**
 * Регистро-нечувствительная по ключам Map. Необходима для унификации работы с
 * базами данных. Например выборка таблицы в H2 - дает upper case ключи, а
 * Postgresql - lower case.
 * 
 * @author val
 *
 * @param <V>
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {

	@Override
	public V put(String key, V value) {
		return super.put(key.toLowerCase(), value);
	}

	@Override
	public V get(Object key) {
		return super.get(String.class.cast(key).toLowerCase());
	}

}