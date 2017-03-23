package com.acme.repository.specification;

import com.acme.model.Subject;
import com.acme.model.Subject_;
import com.acme.model.filter.SubjectFilter;
import com.google.api.client.util.Lists;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Created by kobzev on 23.03.17.
 */
public class SubjectSpecifications {

	public static Specification<Subject> filter(SubjectFilter filter) {
		return (root, criteriaQuery, builder) -> {
			Path<String> firstName = root.get(Subject_.firstName);
			Path<String> middleName = root.get(Subject_.middleName);
			Path<String> lastName = root.get(Subject_.lastName);
			Path<String> phone = root.get(Subject_.phoneNumber);
			Path<String> email = root.get(Subject_.email);

			final List<Predicate> predicates = Lists.newArrayList();
			final List<Predicate> fioPredicates = Lists.newArrayList();
			if (filter.getFio() != null) {
				fioPredicates.add(builder.like(firstName, "%" + filter.getFio()+ "%"));
				fioPredicates.add(builder.like(middleName, "%" + filter.getFio()+ "%"));
				fioPredicates.add(builder.like(lastName, "%" + filter.getFio()+ "%"));
				predicates.add(builder.or(fioPredicates.toArray(new Predicate[fioPredicates.size()])));
			}
			if (filter.getPhone() != null) {
				predicates.add(builder.like(phone, "%" + filter.getPhone() + "%"));
			}
			if (filter.getEmail() != null) {
				predicates.add(builder.like(email, "%" + filter.getEmail() + "%"));
			}

			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};

	}

}
