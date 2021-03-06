package com.xst.bigwhite.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ RestRuntimeException.class })
    protected ResponseEntity<Object> handleInvalidRequest(RuntimeException e, WebRequest request) {
    	RestRuntimeException ire = (RestRuntimeException) e;
        List<FieldErrorResource> fieldErrorResources = new ArrayList<>();

        if(ire.getErrors()!=null){
        List<FieldError> fieldErrors = ire.getErrors().getFieldErrors();
	        for (FieldError fieldError : fieldErrors) {
	            FieldErrorResource fieldErrorResource = new FieldErrorResource();
	            fieldErrorResource.setResource(fieldError.getObjectName());
	            fieldErrorResource.setField(fieldError.getField());
	            fieldErrorResource.setCode(fieldError.getCode());
	            fieldErrorResource.setMessage(fieldError.getDefaultMessage());
	            fieldErrorResources.add(fieldErrorResource);
	        }
        }

        ErrorResource error = new ErrorResource(ire.getCode(), ire.getMessage());
        error.setFieldErrors(fieldErrorResources);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }


}
