package com.acme.repository.specification;

import com.acme.model.Company_;
import com.acme.model.Item;
import com.acme.model.Item_;
import com.acme.model.filter.CatalogFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kobzev on 11.05.17.
 */
public class CatalogSpecifications {

	public static Specification<Item> filter(CatalogFilter filter) {

		return (root, criteriaQuery, builder) -> {
//            Path<List<String>> categories = root.join(CategoryItem_.itemId).get(Item_.categories);
			Path<String> company = root.join(Item_.company).get(Company_.id);

			final List<Predicate> predicates = new ArrayList<>();

			if (filter.getCompany() != null) {
				predicates.add(builder.equal(company, filter.getCompany().getId()));
			}
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

}
