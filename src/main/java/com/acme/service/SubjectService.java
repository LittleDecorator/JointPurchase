package com.acme.service;

import com.acme.model.Subject;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by kobzev on 20.12.16.
 */
public interface SubjectService {

	Subject getSubject(String subjectId);

	Subject getSubjectByEmail(String email);

	Subject enableSubject(Subject subject);

	List<Subject> getAdmins();

	Subject getRoot();

	UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException;

}
