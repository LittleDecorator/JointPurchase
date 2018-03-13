package com.acme.model.dto.mapper;

import com.acme.annotation.SimpleMapper;
import com.acme.config.CentralConfig;
import com.acme.model.Sale;
import com.acme.model.dto.ItemDto;
import com.acme.model.dto.ItemMapDto;
import com.acme.model.dto.SaleDto;
import com.acme.model.dto.SaleRequestDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = CentralConfig.class)
public abstract class SaleMapper extends BaseMapper {

    @Autowired
    ItemMapper itemMapper;

    @Mapping(target = "items", expression = "java(convertItems(entity))")
    public abstract SaleDto toDto(Sale entity);

    @SimpleMapper
    @Mappings({
        @Mapping(target = "bannerId", ignore = true),
        @Mapping(target = "items", ignore = true),
        //@Mapping(target = "transliteName", ignore = true),
    })
    public abstract SaleDto toSimpleDto(Sale entity);

    @Mapping(target = "items", ignore = true)
    public abstract Sale toEntity(SaleDto dto);

    @Mapping(target = "items", ignore = true)
    public abstract Sale requestToEntity(SaleRequestDto dto);

    /**
     *
     * @param entities
     * @return
     */
    public List<SaleDto> toDto(Collection<Sale> entities){
        List<SaleDto> result = new ArrayList<>();
        for (Sale entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }

    @SimpleMapper
    public List<SaleDto> toSimpleDto(Collection<Sale> entities){
        List<SaleDto> result = new ArrayList<>();
        for (Sale entity : entities) {
            result.add(toSimpleDto(entity));
        }
        return result;
    }

    /**
     *
     * @param entity
     * @return
     */
    //protected List<CatalogDetailDto> convertItems(Sale entity) {
    //    List<CatalogDetailDto> result = new ArrayList<>();
    //    for (Item item : entity.getItems()) {
    //        result.add(itemMapper.toDetailDto(item));
    //    }
    //    return result;
    //}

    protected Set<ItemMapDto> convertItems(Sale entity) {
        return itemMapper.toMapDto(entity.getItems());
    }
}
