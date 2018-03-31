package com.acme.util;

import com.acme.constant.Headers;
import com.google.common.base.Function;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
											 String.valueOf(page.getNumber()),
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

		/**
		 * Simplifies calls to methods with optional paging. Depending on page info existence calls paged or collection
		 * method version and sets page headers for response.
		 *
		 * @param response http response
		 * @param pageable optional page request (may be <code>null</code>)
		 * @param collectionCall call to method without paging support
		 * @param pageCall call to method with paging support
		 * @param <T> collection/page item type
		 * @return collection
		 */
		//public static <T> List<T> optionalPageRequest(HttpServletResponse response, @Nullable Pageable pageable,
		//		Supplier<List<T>> collectionCall,
		//		Supplier<Page<T>> pageCall) {
    //
		//		if (pageable == null) {
		//				List<T> list = collectionCall.get();
		//				setPageHeaders(list, response);
		//				return list;
		//		}
		//		Page<T> page = pageCall.get();
		//		setPageHeaders(page, response);
		//		return page.getContent();
		//}

		/**
		 * Simplifies calls to methods with optional paging. Depending on page info existence calls paged or collection
		 * method version and sets page headers for response.
		 *
		 * @param response http response
		 * @param pageable optional page request (may be <code>null</code>)
		 * @param collectionCall call to method without paging support
		 * @param pageCall call to method with paging support
		 * @param <T> collection/page item type
		 * @return collection
		 */
		//public static <T> Set<T> optionalPageSetRequest(HttpServletResponse response, @Nullable Pageable pageable,
		//		Supplier<Set<T>> collectionCall,
		//		Supplier<Page<T>> pageCall) {
    //
		//		if (pageable == null) {
		//				Set<T> list = collectionCall.get();
		//				setPageHeaders(list, response);
		//				return list;
		//		}
		//		Page<T> page = pageCall.get();
		//		setPageHeaders(page, response);
		//		return new HashSet<>(page.getContent());
		//}

}
