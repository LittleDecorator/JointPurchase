package com.acme.model.dto.mapper;

import com.acme.config.CentralConfig;
import com.acme.model.Item;
import com.acme.model.Sale;
import com.acme.model.dto.CatalogDetailDto;
import com.acme.model.dto.SaleDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = CentralConfig.class)
public abstract class SaleMapper extends BaseMapper {

    @Autowired
    ItemMapper itemMapper;

    @Mapping(target = "items", expression = "java(convertItems(entity))")
    public abstract SaleDto toSaleDto(Sale entity);

    /**
     *
     * @param entities
     * @return
     */
    public List<SaleDto> toSaleDto(Collection<Sale> entities){
        List<SaleDto> result = new ArrayList<>();
        for (Sale entity : entities) {
            result.add(toSaleDto(entity));
        }
        return result;
    }

    /**
     *
     * @param entity
     * @return
     */
    protected List<CatalogDetailDto> convertItems(Sale entity) {
        List<CatalogDetailDto> result = new ArrayList<>();
        for (Item item : entity.getItems()) {
            result.add(itemMapper.toDetailDto(item));
        }
        return result;
    }
}
