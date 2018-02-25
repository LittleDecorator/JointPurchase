package com.acme.constant;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;

public interface Headers {

		String ACCEPT = "accept";

		String AUTH_TOKEN = "x-auth-token";
		String REQUEST_UID = "x-request-uid";
		String INTERNAL_KEY = "x-internal-key";

		/**
		 * User REQUEST_UID instead of
		 */
		@Deprecated
		String REQUEST_ID = "x-request-id";

		interface Error {
				String NAME = "x-error";
				String CODE = "x-error-code";
				String TEXT = "x-error-text";

				/**
				 * Use TEXT instead of
				 */
				@Deprecated
				String MSG = "x-error-msg";

				String DETAILED_INFORMATION = "x-error-detailed-information";
		}

		interface Paging {
				String TOTAL_ELEMENTS = "x-total-count";
				String TOTAL_PAGES = "x-total-pages";
				String PAGE_NUMBER = "x-page-number";
				String PAGE_SIZE = "x-page-size";

				Set<String> ALL_HEADERS = ImmutableSet.of(TOTAL_ELEMENTS, TOTAL_PAGES, PAGE_NUMBER, PAGE_SIZE);
		}

		static boolean isJsonAcceptable(HttpServletRequest servletRequest) {
				String acceptHeader = servletRequest.getHeader(Headers.ACCEPT);
				return acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)
						|| acceptHeader.contains(MediaType.ALL_VALUE);
		}


}
