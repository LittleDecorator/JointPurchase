package com.acme.model.filter;

/**
 * Created by kobzev on 11.05.17.
 */
public class CatalogFilter {

	private String company;
	private String category;
	// необходим для проверки вхождения результирующих товаров в список желаемого
	private String clientEmail;
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

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
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
