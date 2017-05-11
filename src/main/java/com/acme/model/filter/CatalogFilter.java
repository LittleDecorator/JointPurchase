package com.acme.model.filter;

import com.acme.model.Category;
import com.acme.model.Company;

/**
 * Created by kobzev on 11.05.17.
 */
public class CatalogFilter {

	private Company company;
	private Category category;
	private int limit;
	private int offset;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "CatalogFilter{" +
			   "company=" + company +
			   ", category=" + category +
			   ", limit=" + limit +
			   ", offset=" + offset +
			   '}';
	}
}
