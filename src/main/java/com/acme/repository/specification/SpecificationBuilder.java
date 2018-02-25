package com.acme.repository.specification;

import com.acme.model.Item;
import com.acme.model.filter.CatalogFilter;
import com.acme.model.filter.ItemFilter;
import javax.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specifications;

import static org.springframework.data.jpa.domain.Specifications.where;
import static com.acme.repository.specification.ItemSpecifications.*;

public class SpecificationBuilder {

    /**
     * Spec for items by catalog filter
     * @param filter
     * @return
     */
    public static Specifications<Item> applyCatalogFilter(CatalogFilter filter) {
        Specifications<Item> spec = where(isAvailable());

        fetchCompany(JoinType.INNER);

        if (filter.getCompany()!=null) {
            spec = spec.and(hasCompany(filter.getCompany()));
        }
        if(filter.getCategory()!=null || filter.getSubcategory()!=null){
            spec = spec.and(hasCategories(filter.getCategory(), filter.getSubcategory()));
        }

        return spec;
    }

    /**
     * Spec for items by item filter
     * @param filter
     * @return
     */
    public static Specifications<Item> applyItemFilter(ItemFilter filter){
        Specifications<Item> spec = where(null);
        fetchCompany(JoinType.INNER);

        if (filter.getArticle() != null) {
            spec = spec.and(likeArticle(filter.getArticle()));
        }
        if (filter.getCompany() != null) {
            spec = spec.and(hasCompany(filter.getCompany().getId()));
        }
        if (filter.getName() != null) {
            spec = spec.and(likeName(filter.getName()));
        }
        return spec;
    }
}
