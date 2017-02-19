package com.acme.model.specification;

import com.acme.enums.OrderStatus;
import com.acme.model.OrderView;
import com.acme.model.OrderView_;
import com.acme.model.filter.OrderFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kobzev on 16.02.17.
 *
 */
public class OrderViewSpecifications {

	public static Specification<OrderView> filter(OrderFilter filter) {
		return (root, criteriaQuery, builder) -> {
			Path<Timestamp> createDate = root.get(OrderView_.createDate);
			Path<OrderStatus> status = root.get(OrderView_.status);
			Path<String> recipient = root.get(OrderView_.recipientId);

			final List<Predicate> predicates = new ArrayList<>();
			if (filter.getDateFrom() != null) {
				predicates.add(builder.greaterThanOrEqualTo(createDate, Timestamp.from(filter.getDateFrom().atZone(ZoneId.of("UTC")).toInstant())));
			}
			if (filter.getDateTo() != null) {
				predicates.add(builder.lessThanOrEqualTo(createDate, Timestamp.from(filter.getDateTo().atZone(ZoneId.of("UTC")).toInstant())));
			}
			if (filter.getStatus() != null) {
				predicates.add(builder.equal(status, filter.getStatus()));
			}
			if (filter.getSubjectId() != null) {
				predicates.add(builder.equal(recipient, filter.getSubjectId()));
			}
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	public static Specification<OrderView> customer(String subjectId) {
		return (root, criteriaQuery, builder) -> builder.equal(root.get(OrderView_.recipientId), subjectId);
	}
}
