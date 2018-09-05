package com.acme.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.NullValueCheckStrategy;

/**
 * MapStruct CentralConfig all mappers inherit from this
 */
@MapperConfig(mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CentralConfig {
//	@Mapping(target = "class", ignore = true)
//	Object mapObjectWithoutMetaClass(Object o);
//
//	@Mapping(target = "class", ignore = true)
//	Object mapObjectWithoutMetaClassFromTwoObjects(Object o1, Object o2);
//
//	@Mapping(target = "class", ignore = true)
//	Object mapObjectWithoutMetaClassFromThreeObjects(Object o1, Object o2, Object o3);
}
