package com.acme.repository;

import com.acme.model.InstagramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by nikolay on 03.07.17.
 */
public interface InstagramUserRepository extends JpaRepository<InstagramUser, String>, JpaSpecificationExecutor<InstagramUser> {

    InstagramUser findOneByoriginId(String originId);

}
