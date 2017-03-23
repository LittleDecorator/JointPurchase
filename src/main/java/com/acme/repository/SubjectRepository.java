package com.acme.repository;

import com.acme.model.OrderView;
import com.acme.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


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

}
