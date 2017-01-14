package com.acme.service.impl;

import com.acme.model.Subject;
import com.acme.repository.SubjectRepository;
import com.acme.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kobzev on 20.12.16.
 */
@Service
public class SubjectServiceImpl implements SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;

	@Override
	public Subject getSubject(String subjectId) {
		return subjectRepository.getById(subjectId);
	}

	@Override
	public Subject getSubjectByEmail(String email) {
		return subjectRepository.getByEmail(email);
	}
}
