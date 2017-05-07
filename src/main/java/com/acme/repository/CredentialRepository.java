package com.acme.repository;

import com.acme.model.Credential;
import com.acme.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 22.02.17.
 */
public interface CredentialRepository extends JpaRepository<Credential, String> {

    /**
     * Получение админов
     * @param roleId
     * @return
     */
    List<Credential> findAllByRoleId(String roleId);

}
