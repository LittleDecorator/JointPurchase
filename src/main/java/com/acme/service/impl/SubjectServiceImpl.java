package com.acme.service.impl;

import com.acme.model.Credential;
import com.acme.model.Subject;
import com.acme.repository.CredentialRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.SubjectService;
import com.google.api.client.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kobzev on 20.12.16.
 */
@Service
public class SubjectServiceImpl implements SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private CredentialRepository credentialRepository;

	@Override
	public Subject getSubject(String subjectId) {
		return subjectRepository.findOne(subjectId);
	}

	@Override
	public Subject getSubjectByEmail(String email) {
		return subjectRepository.findByEmail(email);
	}

	public List<Subject> getAdmins(){
		List<Subject> result = Lists.newArrayList();
		List<Credential> admins = credentialRepository.findAllByRoleId("admin");
		if(!admins.isEmpty()){
			result = subjectRepository.findAllByIdIn(admins.stream().map(Credential::getSubjectId).collect(Collectors.toList()));
		}
		return result;
	}

	@Override
	public Subject enableSubject(Subject subject) {
		subject.setEnabled(true);
		subject.setMiddleName("BLSADKSFD");
		return subjectRepository.save(subject);
	}
}
