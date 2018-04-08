package com.acme.service.impl;

import com.acme.model.Credential;
import com.acme.model.Subject;
import com.acme.repository.CredentialRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.SubjectService;
import com.google.api.client.util.Lists;
import com.google.common.base.Joiner;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kobzev on 20.12.16.
 */
@Service
@Slf4j
public class SubjectServiceImpl implements SubjectService, UserDetailsService {

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

	@Override
	public List<Subject> getAdmins(){
		List<Subject> result = Lists.newArrayList();
		List<Credential> admins = credentialRepository.findAllByRoleId("admin");
		if(!admins.isEmpty()){
			result = subjectRepository.findAllByIdIn(admins.stream().map(Credential::getSubjectId).collect(Collectors.toList()));
		}
		return result;
	}

	@Override
	public Subject getRoot() {
		Subject result = null;
		Credential root = credentialRepository.findByRoleId("root");
		if(root!=null){
			result = subjectRepository.findOne(root.getSubjectId());
		}
		return result;
	}

	@Override
	public Subject enableSubject(Subject subject) {
		subject.setEnabled(true);
		return subjectRepository.save(subject);
	}

	/**
	 * Return security User by name
	 * @param userId
	 * @return
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		//log.debug("Authenticating '{}'", username);
		Subject subject = getSubject(userId);

		if (subject == null) {
			log.error("User '{}' was not found in the database", userId);
			throw new UsernameNotFoundException("User '" + userId + "' was not found in the database");
		}
		//if(!subject.isActive()) {
		//	log.error("User {} is not active", username);
		//	throw new UsernameNotFoundException("User " + username + " is not active");
		//}
		//log.debug("User [" + username + "] found in local database with ROLES: {}", Joiner.on(",").join(subject.getAuthorities()));

		Credential credential = credentialRepository.findOne(subject.getId());

		//Get roles from user
		Set<GrantedAuthority> grantedAuthorities = Stream.of(credential.getRoleId()).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

		log.debug("User [" + userId + "] has ROLES inherited: {}", Joiner.on(",").join(grantedAuthorities));

		//return new User(subject, grantedAuthorities);
		return new User(subject.getEmail(), Objects.toString(credential.getPassword(), ""), grantedAuthorities);
	}
}
