package com.acme.sms;

import java.util.HashMap;
import java.util.Map;

public class SMSAccount {

	private String id = null;
	private String serviceType = null;
	private String description = null;
	private Map<String, String> credentials = new HashMap<>();

	public String getId() {
		return id;
	}

	public SMSAccount setId(String id) {
		this.id = id;
		return this;
	}

	public String getServiceType() {
		return serviceType;
	}

	public SMSAccount setServiceType(String serviceType) {
		this.serviceType = serviceType;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public SMSAccount setDescription(String description) {
		this.description = description;
		return this;
	}

	public Map<String, String> getCredentials() {
		return credentials;
	}

	public SMSAccount setCredentials(Map<String, String> credentials) {
		this.credentials = credentials;
		return this;
	}

	public String put(String key, String value) {
		return this.credentials.put(key, value);
	}

	public SMSAccount putBuild(String key, String value) {
		this.put(key, value);
		return this;
	}

	public String get(Object key) {
		return this.credentials.get(key);
	}

	public static SMSAccount instance() {
		return new SMSAccount();
	}

}
