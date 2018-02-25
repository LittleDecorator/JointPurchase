package com.acme.model.dto.mapper;

import com.acme.config.CentralConfig;
import com.acme.constant.Constants;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.Sale;
import com.acme.model.dto.CatalogDetailDto;
import com.acme.model.dto.CatalogDto;
import com.acme.model.dto.ItemDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", config = CentralConfig.class)
public abstract class ItemMapper extends BaseMapper {

	/**
	 * Mapping Item to simple item list dto. This dto admin see in list of items
	 * @param entity
	 * @return
	 */
	@Mapping(target = "company", expression = "java(bindCompany(entity))")
	public abstract ItemDto toDto(Item entity);

	/**
	 * mapping Item to catalog detail dto. This dto client see in item card.
	 * @param entity
	 * @return
	 */
	@Mappings({
		@Mapping(target = "url", expression = "java(buildUrl(entity))"),
		@Mapping(target = "salePrice", expression = "java(buildSalePrice(entity))"),
		@Mapping(target = "company", expression = "java(bindCompany(entity))")
	})
	public abstract CatalogDetailDto toDetailDto(Item entity);

	/**
	 * Mapping item to catalog list dto. This dto client see in catalog list.
	 * @param entity
	 * @return
	 */
	@Mappings({
		@Mapping(target = "url", expression = "java(buildUrl(entity))"),
		@Mapping(target = "salePrice", expression = "java(buildSalePrice(entity))")
	})
	public abstract CatalogDto toCatalogDto(Item entity);

	/**
	 * Custom mapping to catalog dto.
	 * @param entities
	 * @return List of catalog dto
	 */
	public List<CatalogDto> toCatalogDto(Collection<Item> entities){
		List<CatalogDto> result = new ArrayList<>();
		for (Item entity : entities) {
			result.add(toCatalogDto(entity));
		}
		return result;
	}

	/**
	 * Custom mapping to item dto
	 * @param entities
	 * @return List of item dto
	 */
	@IterableMapping(elementTargetType = ItemDto.class)
	public abstract List<ItemDto> toDto(List<Item> entities);

	/**
	 * Подменяем компанию
	 * @param item
	 * @return
	 */
	protected String bindCompany(Item item){
		return item.getCompany().getName();
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	protected String buildUrl(Item entity){
		final String[] result = new String[1];
		Optional<ItemContent> contentOptional = entity.getItemContents().stream().filter(ItemContent::isMain).findAny();
		contentOptional.ifPresent(itemContent -> result[0] = Constants.PREVIEW_URL + itemContent.getContentId().getId());
		return result[0];
	}

	// FIXME: если время акции не настало, то мы не должны её вообще получать для товара
	protected Integer buildSalePrice(Item entity){
		Integer result = null;
		Sale sale = entity.getSale();
		Date now = new Date();
		if(sale !=null && sale.isActive() && sale.getStartDate().before(now) && sale.getEndDate().after(now)){
			result = (((Float)(entity.getPrice() - (sale.getDiscount() / 100f * entity.getPrice()))).intValue());
		}
		return result;
	}
}
