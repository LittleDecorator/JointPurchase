package com.acme.repository;

import com.acme.model.OrderView;
import com.acme.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by kobzev on 16.02.17.
 *
 * CRUD для работы с "Клиент"
 */
public interface SubjectRepository extends JpaRepository<Subject, String>, JpaSpecificationExecutor<Subject> {

	/**
	 * Получение клиента по email
	 * @param email
	 * @return
	 */
	Subject findByEmail(String email);

//	@Query("update Subject s set s.enabled = true where s.id = :id")
//	@Modifying(clearAutomatically = true)
//	@Transactional
//	void enableById(@Param("id") String id);

}
