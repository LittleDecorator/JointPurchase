package com.acme.service.impl;

import com.acme.service.ConfirmService;
import com.google.api.client.repackaged.com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by kobzev on 08.06.17.
 */

@Service
public class ConfirmServiceImpl implements ConfirmService{

	@Autowired
	CacheManager cacheManager;

	@Override
	public int generateSmsCode() {
		int min = 0,max = 9,size=5;
		StringBuilder builder = new StringBuilder();
		while(size>0){
			builder.append(ThreadLocalRandom.current().nextInt(min, max + 1));
			size--;
		}
		return Integer.parseInt(builder.toString());
	}

	@Override
	public int generateSmsCode(String subjectId) {
		int code = generateSmsCode();
		storeSmsCode(subjectId, code);
		return code;
	}

	@Override
	public boolean compareSmsCodes(String subjectId, int code) {
		return Objects.equal(code, findSmsCode(subjectId));
	}

	@Override
	public void storeSmsCode(String subjectId, int code) {
		cacheManager.getCache("codes").put(subjectId, code);
	}

	@Override
	public int findSmsCode(String subjectId) {
		return (int)cacheManager.getCache("codes").get(subjectId).get();
	}
}
