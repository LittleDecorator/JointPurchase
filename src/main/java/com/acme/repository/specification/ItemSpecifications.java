package com.acme.repository.specification;

import com.acme.model.*;
import com.acme.model.Company_;
import com.acme.model.Item_;
import com.acme.model.filter.ItemFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikolay on 19.02.17.
 */
public class ItemSpecifications {

    public static Specification<Item> filter(ItemFilter filter) {

        return (root, criteriaQuery, builder) -> {
            System.out.println(filter);
            Path<String> name = root.get(Item_.name);
            Path<String> article = root.get(Item_.article);
//            Path<List<Category>> categories = root.get(Item_.categories);
            Path<String> company = root.join(Item_.company).get(Company_.id);

            final List<Predicate> predicates = new ArrayList<>();

            if (filter.getArticle() != null) {
                predicates.add(builder.like(article, "%" + filter.getArticle() + "%"));
            }
            if (filter.getCompany() != null) {
                predicates.add(builder.equal(company, filter.getCompany().getId()));
            }
            if (filter.getName() != null) {
                predicates.add(builder.like(builder.lower(name), "%" + filter.getName().toLowerCase()+ "%"));
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}
