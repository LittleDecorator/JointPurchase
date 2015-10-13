package com.acme.model.mapper;

import com.acme.model.domain.CategoryItemLink;
import com.acme.model.domain.ItemCategoryLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomMapper {

    List<String> getContentHashes();

    boolean isHashExists(@Param("hash") String hash);

    List<String> getSubCategoryLeafs(@Param("baseId") String id);

    List<CategoryItemLink> getCategoryTreeItems();

    List<ItemCategoryLink> getItemCategories();

}
