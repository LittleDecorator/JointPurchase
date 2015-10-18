package com.acme.model.mapper;

//import com.acme.model.domain.Category;
import com.acme.model.domain.ItemCategoryLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomMapper {

    List<String> getContentHashes();

    boolean isHashExists(@Param("hash") String hash);

    List<String> getSubCategoryLeafs(@Param("baseId") String id);

//    List<Category> getCategoryTreeItems();

    List<ItemCategoryLink> getItemCategories();

}
