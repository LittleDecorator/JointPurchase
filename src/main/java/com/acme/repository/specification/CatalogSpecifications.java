package com.acme.repository.specification;

import com.acme.model.Catalog;
import com.acme.model.Catalog_;
import com.acme.model.Category;
import com.acme.model.Category_;
import com.acme.model.Company;
import com.acme.model.Company_;
import com.acme.model.Item;
import com.acme.model.Item_;
import com.acme.model.filter.CatalogFilter;
import org.assertj.core.util.Strings;
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
			Path<String> company = root.join(Item_.company).get(Company_.id);

			final List<Predicate> predicates = new ArrayList<>();

			if (filter.getCompany() != null) {
				predicates.add(builder.equal(company, filter.getCompany()));
			}
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/**
	 * Спецификация для выборки из view
	 * @param filter
	 * @return
	 */
	public static Specification<Catalog> filterCatalog(CatalogFilter filter) {
		return (root, criteriaQuery, builder) -> {

			// путь до производителя
			Path<Company> company = root.get(Catalog_.company);
			// путь до подкатегории
			Path<Category> category = root.get(Catalog_.category);
			// путь до категории
			Path<Category> parentCategory = root.get(Catalog_.parentCategory);

			final List<Predicate> predicates = new ArrayList<>();

			// фильтр по компании
			if (filter.getCompany() != null) {
				predicates.add(builder.equal(company.get(Company_.id), filter.getCompany()));
			}

			//  фильтр по категории и подкатегории
			if(filter.getCategory() !=null) {
				Predicate parent = builder.equal(parentCategory.get(Category_.id), filter.getCategory());
				if(filter.getSubcategory()==null){
					predicates.add(builder.or(parent, builder.equal(category.get(Category_.id), filter.getCategory())));
				} else {
					predicates.add(parent);
				}
			}

			// если выбрана подкатегория
			if(filter.getSubcategory()!=null){
					predicates.add(builder.equal(category.get(Category_.id), filter.getSubcategory()));
			}

			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	public static Specification<Item> filterPopular() {

		return (root, criteriaQuery, builder) -> {
			Path<Boolean> bestseller = root.get(Item_.bestseller);

			final List<Predicate> predicates = new ArrayList<>();

			predicates.add(builder.isTrue(bestseller));
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

}
