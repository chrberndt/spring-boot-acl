package com.chberndt.springbootacl.advice;

import com.chberndt.springbootacl.exception.AlbumNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Christian Berndt
 */
@RestControllerAdvice
class AlbumNotFoundAdvice {

	@ExceptionHandler(AlbumNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String albumNotFoundHandler(AlbumNotFoundException ex) {
		return ex.getMessage();
	}

}
