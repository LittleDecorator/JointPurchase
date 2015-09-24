package com.acme.model.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomMapper {

    List<String> getContentHashes();

    boolean isHashExists(@Param("hash") String hash);

    List<String> getSubCategoryLeafs(@Param("baseId") String id);



}
