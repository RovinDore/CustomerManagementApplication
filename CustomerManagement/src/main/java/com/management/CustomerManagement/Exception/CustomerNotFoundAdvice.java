package com.management.CustomerManagement.Exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

@ControllerAdvice
public class CustomerNotFoundAdvice {

    protected Logger logger;

    public CustomerNotFoundAdvice() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorMessage UserNotFoundHandler(UserNotFoundException ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ResponseBody
    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorMessage CustomerNotFoundHandler(CustomerNotFoundException ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ResponseBody
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage BadRequestHandler(BadRequestException ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseBody
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage JwtExpiredHandler(ExpiredJwtException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage JwtSignatureHandler(SignatureException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {
        logger.error("Request raised a DataIntegrityViolationException");
        // Nothing to do
    }

    @ExceptionHandler({ SQLException.class, DataAccessException.class })
    public String databaseError(Exception exception) {
        // Nothing to do. Return value 'databaseError' used as logical view name
        // of an error page, passed to view-resolver(s) in usual way.
        logger.error("Request raised " + exception.getClass().getSimpleName());
        logger.info(exception.getMessage());
        return "databaseError";
    }

    @ResponseBody
    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage handleStorageFileNotFound(StorageException ex) {
        return new ErrorMessage(ex.getMessage());
    }
}
