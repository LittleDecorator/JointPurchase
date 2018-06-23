package com.acme.util;

import com.acme.constant.Headers;
import com.google.common.base.Function;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class PageTools {

		public static <T, U> Page<U> mapPageList(Page<T> entityPage, Function<Collection<T>, List<U>> listMappingFunction) {
				Objects.requireNonNull(listMappingFunction, "listMappingFunction");

				List<U> mappedContent = listMappingFunction.apply(entityPage.getContent());

				Pageable pageable = null;
				int size = (entityPage.getSize() == 0 && entityPage.getNumber() !=0) ? entityPage.getNumber() : entityPage.getSize();
				if(size != 0){
						pageable = new PageRequest(entityPage.getNumber(), size, entityPage.getSort());
				}

				return new PageImpl<>(mappedContent, pageable, entityPage.getTotalElements());
		}

		public static <T, U> Page<U> mapPageEntity(Page<T> entityPage, Function<T, U> entityMappingFunction) {
				Objects.requireNonNull(entityMappingFunction, "entityMappingFunction");

				List<U> mappedContent = new ArrayList<>();
				for (T sourceEntity : entityPage.getContent()) {
						mappedContent.add(entityMappingFunction.apply(sourceEntity));
				}

				Pageable pageable = null;
				int size = (entityPage.getSize() == 0 && entityPage.getNumber() !=0) ? entityPage.getNumber() : entityPage.getSize();
				if(size != 0){
						pageable = new PageRequest(entityPage.getNumber(), size, entityPage.getSort());
				}

				return new PageImpl<>(mappedContent, pageable, entityPage.getTotalElements()
				);
		}

		public static void setPageHeaders(Page<?> page){
			RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
			HttpServletResponse servletResponse = ((ServletRequestAttributes) attributes).getResponse();
			setPageHeaders(page, servletResponse);
		}

		/**
		 * Creates http headers for page info
		 *
		 * @param page page info
		 * @param response http response
		 */
		public static void setPageHeaders(Page<?> page, HttpServletResponse response) {
				setPageHeaders(response,
											 String.valueOf(page.getTotalElements()),
											 String.valueOf(page.getTotalPages()),
											 String.valueOf(page.getNumber()+1),
											 String.valueOf(page.getSize()));
		}

	public static void setPageHeaders(Collection<?> collection){
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		HttpServletResponse servletResponse = ((ServletRequestAttributes) attributes).getResponse();
		setPageHeaders(collection, servletResponse);
	}

		/**
		 * Creates http headers for collection
		 *
		 * @param collection collection
		 * @param response http response
		 */
		public static void setPageHeaders(Collection<?> collection, HttpServletResponse response) {
				setPageHeaders(response,
											 String.valueOf(collection.size()),
											 String.valueOf(1),
											 String.valueOf(0),
											 String.valueOf(collection.size()));
		}

		private static void setPageHeaders(
				HttpServletResponse response, String totalElements, String totalPages, String pageNumber, String pageSize) {

				response.setHeader(Headers.Paging.TOTAL_ELEMENTS, totalElements);
				response.setHeader(Headers.Paging.TOTAL_PAGES, totalPages);
				response.setHeader(Headers.Paging.PAGE_NUMBER, pageNumber);
				response.setHeader(Headers.Paging.PAGE_SIZE, pageSize);
		}

  public static Pageable getPageable(Pageable pageable) {
		  return getPageable(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
  }

  public static Pageable getPageable(Integer page, Integer size, Sort sort) {
    if (page != null && size != null) {
      if (page < 1) {
        throw new RuntimeException("Page index must not be less than one");
      }
      //запросим порцию данных
      int newPage = (page - 1);

      Sort idSort = new Sort(Sort.Direction.ASC,"id");
      if(sort != null){
        sort.and(idSort);
      } else{
        sort = idSort;
      }

      return new PageRequest(newPage, size, sort);
    }
    return null;
  }

  //public static Pageable getPageable(PageFilter filter) {
  //  return getPageable(filter.getPage(), filter.getSize(), filter.getSort());
  //}

}
