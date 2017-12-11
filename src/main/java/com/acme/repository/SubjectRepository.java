package com.acme.repository;

import com.acme.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


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

	List<Subject> findAllByIdIn(List<String> ids);

	Subject findByIdAndEnabledFalse(String subjectId);

//	@Query("update Subject s set s.enabled = true where s.id = :id")
//	@Modifying(clearAutomatically = true)
//	@Transactional
//	void enableById(@Param("id") String id);

}
