package com.acme.service;

import com.acme.model.Subject;

import java.util.List;

/**
 * Created by kobzev on 20.12.16.
 */
public interface SubjectService {

	Subject getSubject(String subjectId);

	Subject getSubjectByEmail(String email);

	Subject enableSubject(Subject subject);

	List<Subject> getAdmins();

	Subject getRoot();

}
