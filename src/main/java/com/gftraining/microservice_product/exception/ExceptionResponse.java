package com.gftraining.microservice_product.exception;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class ExceptionResponse {

	private LocalDate localDate;
	private String message;
	private List<String> details;

	public ExceptionResponse(LocalDate localDate, String message, List<String> details) {
		super();
		this.localDate = localDate;
		this.message = message;
		this.details = details;
	}

	public ExceptionResponse(String message, LocalDate localDate) {
		super();
		this.localDate = localDate;
		this.message = message;
	}

}
