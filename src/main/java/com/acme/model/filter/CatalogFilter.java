package com.acme.model.filter;

/**
 * Created by kobzev on 11.05.17.
 */
public class CatalogFilter {

	private String company;
	private String category;
	private int limit;
	private int offset;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
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
