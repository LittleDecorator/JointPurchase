package com.acme.service;

/**
 * Created by kobzev on 08.06.17.
 */
public interface ConfirmService {

	int generateSmsCode();

	int generateSmsCode(String subjectId);

	boolean compareSmsCodes(String subjectId, int code);

	void storeSmsCode(String subjectId, int code);

	int findSmsCode(String subjectId);
}
