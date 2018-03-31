package com.acme.model.dto.mapper;

import com.acme.annotation.SimpleMapper;
import com.acme.config.CentralConfig;
import com.acme.constant.Constants;
import com.acme.model.Category;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.Sale;
import com.acme.model.dto.CatalogDetailDto;
import com.acme.model.dto.CatalogDto;
import com.acme.model.dto.ItemContentDto;
import com.acme.model.dto.ItemDto;
import com.acme.model.dto.ItemMapDto;
import com.google.common.base.Strings;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.util.Sets;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", config = CentralConfig.class)
public abstract class ItemMapper extends BaseMapper {

	/**
	 * Mapping Item to simple item list dto. This dto admin see in list of items
	 * @param entity
	 * @return
	 */
	@Mappings({
		@Mapping(target = "companyId", expression = "java(bindCompanyId(entity))"),
		@Mapping(target = "companyName", expression = "java(bindCompanyName(entity))")
	})
	public abstract ItemDto toDto(Item entity);

	@Mappings({
		@Mapping(target = "company", ignore = true),
	})
	public abstract Item toEntity(ItemDto dto);

	public abstract void toExistingEntity(ItemDto updateRequest, @MappingTarget Item entity);

	@Mappings({
		@Mapping(target = "status", ignore = true),
		@Mapping(target = "categories", ignore = true),
		@Mapping(target = "companyId", ignore = true),
		@Mapping(target = "description", ignore = true),
		@Mapping(target = "age", ignore = true),
		@Mapping(target = "material", ignore = true),
		@Mapping(target = "companyName", expression = "java(bindCompanyName(entity))")
	})
	@SimpleMapper
	public abstract ItemDto toSimpleDto(Item entity);

	/**
	 * mapping Item to catalog detail dto. This dto client see in item card.
	 * @param entity
	 * @return
	 */
	@Mappings({
		@Mapping(target = "url", expression = "java(buildUrl(entity))"),
		@Mapping(target = "salePrice", expression = "java(buildSalePrice(entity))"),
		@Mapping(target = "company", expression = "java(bindCompanyName(entity))")
	})
	public abstract CatalogDetailDto toDetailDto(Item entity);

	@SimpleMapper
	@Mappings({
		@Mapping(target = "url", expression = "java(buildViewUrl(entity))"),
		@Mapping(target = "salePrice", expression = "java(buildSalePrice(entity))"),
		@Mapping(target = "company", ignore = true),
		@Mapping(target = "categories", ignore = true),
		@Mapping(target = "itemContents", ignore = true),
		@Mapping(target = "contentIds", expression = "java(collectContentIds(entity))"),
	})
	public abstract CatalogDetailDto toSimpleCatalogDetailDto(Item entity);

	/**
	 * Mapping item to catalog list dto. This dto client see in catalog list.
	 * @param entity
	 * @return
	 */
	@Mappings({
		@Mapping(target = "url", expression = "java(buildUrl(entity))"),
		@Mapping(target = "salePrice", expression = "java(buildSalePrice(entity))"),
		@Mapping(target = "companyName", expression = "java(bindCompanyName(entity))"),
	})
	public abstract CatalogDto toCatalogDto(Item entity);

	/**
	 * Custom mapping to catalog dto.
	 * @param entities
	 * @return List of catalog dto
	 */
	public Set<CatalogDto> toCatalogDto(Collection<Item> entities){
		Set<CatalogDto> result = Sets.newHashSet();
		for (Item entity : entities) {
			result.add(toCatalogDto(entity));
		}
		return result;
	}

	@Mappings({
		@Mapping(target = "image", expression = "java(getMainImageId(entity))"),
		@Mapping(target = "salePrice", expression = "java(buildSalePrice(entity))"),
	})
	public abstract ItemMapDto toMapDto(Item entity);

	@Mappings({
		@Mapping(target = "contentId", source = "content.id"),
	})
	public abstract ItemContentDto toContentDto(ItemContent entity);

	@IterableMapping(elementTargetType = ItemContentDto.class)
	public abstract Set<ItemContentDto> toContentDto(Collection<ItemContent> entities);

	@IterableMapping(elementTargetType = ItemMapDto.class)
	public abstract Set<ItemMapDto> toMapDto(Collection<Item> entities);

	/**
	 * Custom mapping to item dto
	 * @param entities
	 * @return List of item dto
	 */
	@IterableMapping(elementTargetType = ItemDto.class)
	public abstract List<ItemDto> toDto(List<Item> entities);

	/**
	 * Custom mapping to item dto
	 * @param entities
	 * @return List of item dto
	 */
	@SimpleMapper
	public Set<ItemDto> toSimpleDto(Collection<Item> entities){
		Set<ItemDto> result = Sets.newHashSet();
		for (Item entity : entities) {
			result.add(toSimpleDto(entity));
		}
		return result;
	}

	/**
	 * Подменяем компанию
	 * @param item
	 * @return
	 */
	protected String bindCompanyName(Item item){
		return item.getCompany().getName();
	}


	protected String bindCompanyId(Item item){
		return item.getCompany().getId();
	}

	protected List<String> collectCategoryIds(Item item){
		return item.getCategories().stream().map(Category::getId).collect(Collectors.toList());
	}

	protected Map<String, String> collectContentIds(Item entity){
		return entity.getItemContents()
			.stream()
			.collect(Collectors.toMap(o -> o.getContent().getId(), o-> Strings.nullToEmpty(o.getContent().getMetaInfo())));
	}

	protected String buildViewUrl(Item entity){
		final String[] result = new String[1];
		Optional<ItemContent> contentOptional = entity.getItemContents().stream().filter(ItemContent::isMain).findAny();
		contentOptional.ifPresent(itemContent -> result[0] = Constants.VIEW_URL + itemContent.getContent().getId());
		return result[0];
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	protected String buildUrl(Item entity){
		final String[] result = new String[1];
		Optional<ItemContent> contentOptional = entity.getItemContents().stream().filter(ItemContent::isMain).findAny();
		contentOptional.ifPresent(itemContent -> result[0] = Constants.PREVIEW_URL + itemContent.getContent().getId());
		return result[0];
	}

	protected String getMainImageId(Item entity){
		final String[] result = new String[1];
		Optional<ItemContent> contentOptional = entity.getItemContents().stream().filter(ItemContent::isMain).findAny();
		contentOptional.ifPresent(itemContent -> result[0] = itemContent.getContent().getId());
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
