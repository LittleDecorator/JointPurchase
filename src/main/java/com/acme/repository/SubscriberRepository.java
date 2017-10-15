package com.acme.repository;

import com.acme.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by nikolay on 15.10.17.
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, String>, JpaSpecificationExecutor<Subscriber> {

    Subscriber findOneByEmail(String mail);

}
