package com.acme.model.dto.mapper;

import com.acme.annotation.SimpleMapper;
import com.acme.config.CentralConfig;
import com.acme.model.Subject;
import com.acme.model.dto.SubjectDto;
import com.acme.model.dto.SubjectMapDto;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.util.Sets;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import static java.util.stream.Collectors.joining;

@Mapper(componentModel = "spring", config = CentralConfig.class)
public interface SubjectMapper {

    /**
     * Mapping Item to simple item list dto. This dto admin see in list of items
     * @param entity
     * @return
     */
    @Mappings({
        @Mapping(target = "fullName", expression = "java(buildFullName(entity))")
    })
    SubjectDto toDto(Subject entity);

    Subject toEntity(SubjectDto dto);

    @Mappings({
        @Mapping(target = "name", expression = "java(buildFullName(entity))")
    })
    SubjectMapDto toMapDto(Subject entity);

    @Mappings({
        @Mapping(target = "fullName", expression = "java(buildFullName(entity))"),
        @Mapping(target = "firstName", ignore = true),
        @Mapping(target = "lastName", ignore = true)
    })
    @SimpleMapper
    SubjectDto toSimpleDto(Subject entity);

    @SimpleMapper
    default List<SubjectDto> toSimpleDto(List<Subject> entities){
        List<SubjectDto> result = new ArrayList<>();
        for (Subject entity : entities) {
            result.add(toSimpleDto(entity));
        }
        return result;
    }

    default Set<SubjectMapDto> toMapDto(List<Subject> entities){
        Set<SubjectMapDto> result = Sets.newHashSet();
        for (Subject entity : entities) {
            result.add(toMapDto(entity));
        }
        return result;
    }

    /**
     * Формирование полного имени клинта
     * @param entity
     * @return
     */
    default String buildFullName(Subject entity) {
        return Stream.of(entity.getLastName(), entity.getFirstName(), entity.getMiddleName())
            .filter(s -> !Strings.isNullOrEmpty(s))
            .collect(joining(" "));
    }

}
