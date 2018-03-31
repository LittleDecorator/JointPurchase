package com.acme.repository.specification;

import com.acme.model.*;
import com.acme.model.Company_;
import com.acme.model.Item_;
import com.acme.model.filter.ItemFilter;
import com.acme.util.SpecTools;
import com.google.common.base.Strings;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikolay on 19.02.17.
 */
public class ItemSpecifications {

    public static Specification<Item> likeArticle(String article) {
        return (root, query, cb) -> cb.like(root.get(Item_.article), "%" + article + "%");
    }

    public static Specification<Item> likeName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Item_.name)), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Item> isAvailable() {
        return (root, query, cb) -> cb.notEqual(root.get(Item_.notForSale), true);
    }

    /**
     * Specification on item company
     * @param companyId
     * @return
     */
    public static Specification<Item> hasCompany(String companyId) {
        return (root, query, cb) -> cb.equal(root.join(Item_.company).get(Company_.id), companyId);
    }

    /**
     * specification by category
     * @param categoryId
     * @param subcategoryId
     * @return
     */
    public static Specification<Item> hasCategories(String categoryId, String subcategoryId) {
        return (root, query, cb) -> {
            query.distinct(true);

            final List<Predicate> predicates = new ArrayList<>();
            ListJoin<Item, Category> categoryJoin = root.join(Item_.categories);

            // если выбрана подкатегория
            if (!Strings.isNullOrEmpty(subcategoryId)) {
                predicates.add(cb.equal(categoryJoin.get(Category_.id), subcategoryId));
            } else {
                //  фильтр по категории и подкатегории
                if (!Strings.isNullOrEmpty(categoryId)) {
                    predicates.add(cb.or(cb.equal(categoryJoin.get(Category_.id), categoryId), cb.equal(categoryJoin.get(Category_.parentId), categoryId)));
                }
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     *
     * @return
     */
    public static Specification<Item> isPopular() {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.isTrue(root.get(Item_.bestseller));
        };
    }

    /**
     * Join периодических задач
     *
     * @param joinType
     * @return
     */
    @SuppressWarnings("all")
    public static Specification<Item> fetchCompany(final JoinType joinType) {
        return (root, query, cb) -> {
            if (!SpecTools.isCountQuery(query)) {
                query.distinct(true);
                root.fetch(Item_.company, joinType);
            }
            return null;
        };
    }
}
