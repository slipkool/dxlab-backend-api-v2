package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.customizedexception;

import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.exception.OrderNotFound;
import com.dxlab.dxlabbackendapi.domain.exception.OrderResultNotUpdate;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomizedExceptionAdapter extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error al validar los campos");
        pd.setTitle("Error al validar los campos");
        pd.setType(URI.create("https://api.com/errors/not-found"));
        pd.setProperty("error", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler({LaboratoryResultException.class, OrderResultNotUpdate.class, OrderNotFound.class})
    public final ResponseEntity<Object> handleLaboratoryResultException(RuntimeException  ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = "";

        if(ex instanceof LaboratoryResultException) {
            title = "Error al cargar los resultados";
        }
        if(ex instanceof OrderResultNotUpdate) {
            title = "Error al actualizar la orden con el resultado de laboratorio";
        }
        if(ex instanceof OrderNotFound) {
            title = "Error al buscar la orden";
            status = HttpStatus.NOT_FOUND;
        }

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        pd.setTitle(title);
        return ResponseEntity.badRequest().body(pd);
    }
}
