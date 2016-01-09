package com.acme.model.mapper;

//import com.acme.model.domain.Category;
import com.acme.gen.domain.Item;
import com.acme.model.domain.ItemCategoryLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomMapper {

    List<String> getContentHashes();

    boolean isHashExists(@Param("hash") String hash);

    List<String> getSubCategoryLeafs(@Param("baseId") String id);

//    List<Category> getCategoryTreeItems();

    List<ItemCategoryLink> getItemCategories();

    Integer getOrderedItemCou(@Param("itemId") String id);

    List<ItemCategoryLink> getFilteredItems(@Param("name") String name, @Param("article") String article, @Param("company") String company);

    List<Item> searchItems(@Param("criteria") String criteria);

}
