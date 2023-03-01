package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.customizedexception;

import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.exception.OrderNotFound;
import com.dxlab.dxlabbackendapi.domain.exception.OrderResultNotUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
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

    private static final Logger log = LoggerFactory.getLogger(CustomizedExceptionAdapter.class);
    public static final String EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE = "An exception occured, which will cause a {} response";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn(EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, status, ex);
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error al validar los campos");
        pd.setTitle("Error al validar los campos");
        pd.setType(URI.create("https://api.com/errors/not-found"));
        pd.setProperty("error", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler({LaboratoryResultException.class, OrderResultNotUpdate.class})
    public final ResponseEntity<Object> handleLaboratoryResultException(RuntimeException ex) {
        log.warn(EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, HttpStatus.BAD_REQUEST, ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = "";

        if(ex instanceof LaboratoryResultException) {
            title = "Error en el servicio de resultados";
        }
        if(ex instanceof OrderResultNotUpdate) {
            title = "Error al actualizar la orden con el resultado de laboratorio";
        }

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        pd.setTitle(title);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(OrderNotFound.class)
    public final ResponseEntity<Object> handleOrderNotFound(RuntimeException  ex) {
        log.warn(EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, HttpStatus.NOT_FOUND, ex);
        HttpStatus status = HttpStatus.NOT_FOUND;
        String title = "Error al buscar la orden";
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        pd.setTitle(title);
        return ResponseEntity.notFound().build();
    }
}
