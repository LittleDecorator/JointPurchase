package com.acme.model.filter;

/**
 * Created by kobzev on 23.03.17.
 */
public class SubjectFilter {

	private String fio;
	private String phone;
	private String email;
	private Integer limit;
	private Integer offset;

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "SubjectFilter{" +
			   "fio='" + fio + '\'' +
			   ", phone='" + phone + '\'' +
			   ", email='" + email + '\'' +
			   ", limit=" + limit +
			   ", offset=" + offset +
			   '}';
	}
}
