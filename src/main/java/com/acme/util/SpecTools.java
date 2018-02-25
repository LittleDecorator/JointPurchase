package com.acme.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

/**
 * Util methods for building JPA specifications
 */
public abstract class SpecTools {

	/**
	 * Build specification instance to compare value with appropriate field
	 * 
	 * @param singularAttribute metamodel singular attribute
	 * @param value value for comparison
	 * @param <Entity> root entity type
	 * @param <FieldType> entity field type
	 * @return specification instance with comparison logic
	 */
	public static <Entity, FieldType> Specification<Entity> fieldEqual
			(final SingularAttribute<Entity, FieldType> singularAttribute, final FieldType value) {

		return new Specification<Entity>() {
			@Override
			public Predicate toPredicate(Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(singularAttribute), value);
			}
		};
	}

	/**
	 * Get existing join for attribute or create one if not exists
	 *
	 * @param root root element
	 * @param attribute joining attribute
	 * @param <ROOT> root entity type
	 * @param <JOIN> joining entity type
	 * @return join for attribute
	 */
	@SuppressWarnings("unchecked")
	public static <ROOT, JOIN> Join<ROOT, JOIN> getJoin(Root<ROOT> root, SetAttribute<ROOT, JOIN> attribute) {
		for (Join<ROOT, ?> join : root.getJoins()) {
			if (join.getAttribute().equals(attribute)) {
				return (Join<ROOT, JOIN>) join;
			}
		}
		return root.join(attribute);
	}

	/**
	 * Get existing map join for attribute or create one if not exists
	 *
	 * @param root root element
	 * @param attribute joining attribute
	 * @param <ROOT> root entity type
	 * @param <ID> joining entity id type
	 * @param <JOIN> joining entity type
	 * @return join for attribute
	 */
	@SuppressWarnings("unchecked")
	public static <ROOT, ID, JOIN> MapJoin<ROOT, ID, JOIN> getMapJoin(Root<ROOT> root, MapAttribute<ROOT, ID, JOIN> attribute) {
		for (Join<ROOT, ?> join : root.getJoins()) {
			if (join.getAttribute().equals(attribute)) {
				return (MapJoin<ROOT, ID, JOIN>) join;
			}
		}
		for (Fetch<ROOT, ?> fetch : root.getFetches()) {
			if (fetch.getAttribute().equals(attribute)) {
				return (MapJoin<ROOT, ID, JOIN>) fetch;
			}
		}
		return root.join(attribute);
	}

	/**
	 * Add fetch join for collection if not exists
	 *
	 * @param root root expression for entity
	 * @param attribute fetching attribute
	 * @param <T> root entity type
	 */
	public static <T> void fetchOnce(Root<T> root, SetAttribute<? super T, ?> attribute) {
		for (Fetch<T, ?> fetch : root.getFetches()) {
			if (fetch.getAttribute().equals(attribute)) {
				return;
			}
		}
		root.fetch(attribute);
	}

	/**
	 * Add fetch join for map if not exists
	 *
	 * @param root root expression for entity
	 * @param attribute fetching attribute
	 * @param <T> root entity type
	 */
	public static <T> void fetchOnce(Root<T> root, MapAttribute<? super T, ?, ?> attribute) {
		for (Fetch<T, ?> fetch : root.getFetches()) {
			if (fetch.getAttribute().equals(attribute)) {
				return;
			}
		}
		root.fetch(attribute);
	}

	/**
	 * Check if query is for count
	 *
	 * @param query criteria query to check
	 * @return true if for count
	 */
	public static boolean isCountQuery(CriteriaQuery<?> query) {
		return query.getResultType() == Long.class || query.getResultType() == long.class;
	}
}