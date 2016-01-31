package com.acme.model.mapper;

//import com.acme.model.domain.Category;
import com.acme.gen.domain.Item;
import com.acme.model.domain.ItemCategoryLink;
import com.acme.model.domain.Product;
import com.acme.model.filters.ProductFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomMapper {

    List<String> getContentHashes();

    boolean isHashExists(@Param("hash") String hash);

    List<String> getSubCategoryLeafs(@Param("baseId") String id);

//    List<Category> getCategoryTreeItems();

    List<ItemCategoryLink> getItemCategories();

    Integer getOrderedItemCou(@Param("itemId") String id);

    List<ItemCategoryLink> getFilteredItems(@Param("name") String name, @Param("article") String article, @Param("company") String company);

    List<Item> searchItems(@Param("criteria") String criteria);

//    List<Map<String,Object>> getItemsByFilter();
    List<Product> getItemsByFilter(ProductFilter filter);

}
