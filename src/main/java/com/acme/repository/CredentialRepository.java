package com.acme.repository;

import com.acme.model.Credential;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by nikolay on 22.02.17.
 */
public interface CredentialRepository extends CrudRepository<Credential, String> {
}
