package com.acme.repository.specification;

import com.acme.model.Sale;
import com.acme.model.Sale_;
import com.google.common.collect.Lists;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class SaleSpecifications {

    public static Specification<Sale> active() {
        return (root, criteriaQuery, builder) -> {
            final List<Predicate> predicates = Lists.newArrayList();
            predicates.add(builder.isTrue(root.get(Sale_.active)));
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

    }

}
