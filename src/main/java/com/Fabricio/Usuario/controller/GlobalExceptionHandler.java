package com.Fabricio.Usuario.controller;

import com.Fabricio.Usuario.infrastructure.exception.ConflictException;
import com.Fabricio.Usuario.infrastructure.exception.IllegalArgumentException;
import com.Fabricio.Usuario.infrastructure.exception.ResourceNotFoundException;
import com.Fabricio.Usuario.infrastructure.exception.UnauthorizedException;
import com.Fabricio.Usuario.infrastructure.exception.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                            HttpServletRequest request){
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                "Not Found"));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException(ConflictException ex,
                                                          HttpServletRequest request){
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI(),
                "Conflict"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }

    private ErrorResponseDTO buildError(int status, String mensagem, String path, String error){
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message(mensagem)
                .error(error)
                .status(status)
                .path(path)
                .build();
    }
}
