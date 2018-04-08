package com.acme.util;

import com.acme.exception.InvalidRequestException;
import io.jsonwebtoken.MalformedJwtException;
import javax.naming.AuthenticationException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//import ru.mdimension.wrs.taskmanual.exceptions.AuthorizationException;
//import ru.mdimension.wrs.taskmanual.exceptions.NotFoundException;
//import ru.mdimension.wrs.taskmanual.exceptions.TaskResponseAlreadyClosedException;
//import ru.mdimension.wrs.taskmanual.exceptions.VersionConflictException;
//import ru.mdimension.wrs.taskmanual.exceptions.invalid_request.InvalidRequestException;
//import ru.mdimension.wrs.taskmanual.model.dto.Versioned;
//import ru.mdimension.wrs.taskmanual.model.dto.task.response.TaskResponseDto;
//import ru.mdimension.wrs.taskmanual.web.rest.utils.HttpUtils;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler({
		AccessDeniedException.class,
		AuthenticationException.class,
		BadCredentialsException.class,
		UsernameNotFoundException.class,
		MalformedJwtException.class
	})
	public ResponseEntity<?> authenticationExceptions(Exception e){
		return errorResponse(e.getClass().getSimpleName(), e.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(InvalidRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleBadRequestException(InvalidRequestException ex, HttpServletResponse response) {
		log.warn("Bad request: {}", ex.getMessage());
		setExceptionHeaders(response, ex);
	}

	@ExceptionHandler(value = IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) {
		setExceptionHeaders(response, ex);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleEntityNotFoundException(EntityNotFoundException ex, HttpServletResponse response) {
		setExceptionHeaders(response, ex);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleOtherExceptions(Exception ex, HttpServletResponse response) {
		log.error("Unhandled exception", ex);
		setExceptionHeaders(response, ex);
	}

	/**
	 * Handle HttpMessageNotWritableException.
	 *
	 * @param ex      HttpMessageNotWritableException
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ApiError object
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "Error writing JSON output";
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
	}

	public static void setExceptionHeaders(HttpServletResponse servletResponse, Exception ex) {
		servletResponse.setHeader("x-error", ex.getClass().getSimpleName());
		servletResponse.setHeader("x-error-text", ex.getMessage());
	}

	protected ResponseEntity<?> errorResponse(String error, String message, HttpStatus status) {
		return ResponseEntity
			.status(status)
			.header("x-error", error)
			.header("x-error-message", message)
			.build();
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

}
