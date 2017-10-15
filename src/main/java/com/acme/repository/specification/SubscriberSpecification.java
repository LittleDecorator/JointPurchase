package com.acme.repository.specification;

import com.acme.model.Subscriber;
import com.acme.model.Subscriber_;
import com.acme.model.filter.SubscriberFilter;
import com.google.common.collect.Lists;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Created by nikolay on 15.10.17.
 */
public class SubscriberSpecification {

    public static Specification<Subscriber> filter(SubscriberFilter filter) {
        return (root, criteriaQuery, builder) -> {
            Path<String> email = root.get(Subscriber_.email);

            final List<Predicate> predicates = Lists.newArrayList();
            if (filter.getMail() != null) {
                predicates.add(builder.like(builder.lower(email), "%" + filter.getMail().toLowerCase() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

    }

}
