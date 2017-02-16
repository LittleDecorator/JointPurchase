package com.acme.repository;

import com.acme.model.Subject;
import org.springframework.data.repository.CrudRepository;


/**
 * Created by kobzev on 16.02.17.
 *
 * CRUD для работы с "Клиент"
 */
public interface SubjectRepository extends CrudRepository<Subject, String> {

	/**
	 * Получение клиента по email
	 * @param email
	 * @return
	 */
	Subject findByEmail(String email);

}
