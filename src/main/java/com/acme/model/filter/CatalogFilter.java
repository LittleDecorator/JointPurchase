package com.acme.model.filter;

/**
 * Created by kobzev on 11.05.17.
 */
public class CatalogFilter {

	private String company;
	private String category;
	private String subcategory;
	// необходим для проверки вхождения результирующих товаров в список желаемого
	private String clientEmail;
	private int limit;
	private int offset;
	// ЗАЧЕМ????
	private String sale;

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

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}

	@Override
	public String toString() {
		return "CatalogFilter{" +
				"company='" + company + '\'' +
				", category='" + category + '\'' +
				", clientEmail='" + clientEmail + '\'' +
				", limit=" + limit +
				", offset=" + offset +
				'}';
	}
}
