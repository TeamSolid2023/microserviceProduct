package com.gftraining.microservice_product.exception;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;


@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handlerException(EntityNotFoundException exception){
		ExceptionResponse res = new ExceptionResponse(LocalDate.now(),exception.getMessage(), null);

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {

		List<String> errors = new ArrayList<>();

		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
		}

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(),"Constraint violation", errors);
		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

		List<String> errors = fieldErrors.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(),"Method argument not valid", errors);
		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {

		ExceptionResponse res = new ExceptionResponse("There is no user with that id", LocalDate.now());

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}
}
