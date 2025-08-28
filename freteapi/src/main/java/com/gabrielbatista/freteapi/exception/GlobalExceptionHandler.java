package com.gabrielbatista.freteapi.exception;

import com.gabrielbatista.freteapi.error.ErrorCode;
import com.gabrielbatista.freteapi.error.ErrorUtils;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400 - validação @Valid (body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid"))
                .collect(Collectors.toList());
        ProblemDetail pd = ErrorUtils.problem(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
                "Payload inválido.", req);
        pd.setProperty("errors", errors);
        log.warn("Validation error: {}", errors);
        return pd;
    }

    // 400 - validação @Validated (path/query)
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, WebRequest req) {
        var errors = ex.getConstraintViolations().stream()
                .map(cv -> Map.of(
                        "field", cv.getPropertyPath().toString(),
                        "message", cv.getMessage() != null ? cv.getMessage() : "invalid"))
                .collect(Collectors.toList());
        ProblemDetail pd = ErrorUtils.problem(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
                "Parâmetros inválidos.", req);
        pd.setProperty("errors", errors);
        log.warn("Constraint violation: {}", errors);
        return pd;
    }

    // 400 - JSON malformado / tipo errado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex, WebRequest req) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        log.warn("Bad request body: {}", msg);
        return ErrorUtils.problem(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Corpo da requisição inválido.", req);
    }

    // 400 - parâmetro obrigatório ausente
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParam(MissingServletRequestParameterException ex, WebRequest req) {
        log.warn("Missing param: {}", ex.getParameterName());
        return ErrorUtils.problem(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Parâmetro obrigatório ausente: " + ex.getParameterName(), req);
    }

    // 400 - tipo de argumento inválido
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest req) {
        log.warn("Type mismatch: {} -> {}", ex.getName(), ex.getValue());
        return ErrorUtils.problem(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Tipo inválido para '" + ex.getName() + "'.", req);
    }

    // 404 - domínio
    @ExceptionHandler(UfNaoEncontradaException.class)
    public ProblemDetail handleUfNotFound(UfNaoEncontradaException ex, WebRequest req) {
        log.warn("UF not found: {}", ex.getMessage());
        return ErrorUtils.problem(HttpStatus.NOT_FOUND, ErrorCode.UF_NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(FreteNaoEncontradoException.class)
    public ProblemDetail handleFreteNotFound(FreteNaoEncontradoException ex, WebRequest req) {
        log.warn("Frete not found: {}", ex.getMessage());
        return ErrorUtils.problem(HttpStatus.NOT_FOUND, ErrorCode.FRETE_NOT_FOUND, ex.getMessage(), req);
    }

    // 409 / 400 - integridade (UNIQUE/FK)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, WebRequest req) {
        // Percorre cadeia de causas para identificar melhor
        Throwable t = ex;
        while (t != null) {
            // Via Hibernate (nome da constraint disponível)
            if (t instanceof org.hibernate.exception.ConstraintViolationException h) {
                String c = h.getConstraintName();
                if (c != null && c.equalsIgnoreCase("uk_frete_uf")) {
                    log.warn("Duplicate UF freight (constraint {}): {}", c, ErrorUtils.rootMessage(ex));
                    return ErrorUtils.problem(HttpStatus.CONFLICT, ErrorCode.UF_DUPLICATE,
                            "Frete já cadastrado para esta UF.", req);
                }
                if (c != null && c.equalsIgnoreCase("fk_frete_uf")) {
                    log.warn("Invalid UF FK (constraint {}): {}", c, ErrorUtils.rootMessage(ex));
                    return ErrorUtils.problem(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                            "UF inválida (violação de chave estrangeira).", req);
                }
            }
            // Via JDBC (MySQL): códigos 1062=duplicate, 1452=fk
            if (t instanceof SQLIntegrityConstraintViolationException sql) {
                int code = sql.getErrorCode(); // MySQL vendor code
                String msg = sql.getMessage();
                if (code == 1062 || (msg != null && msg.contains("Duplicate entry") && msg.contains("uk_frete_uf"))) {
                    log.warn("Duplicate UF freight (sql code {}): {}", code, msg);
                    return ErrorUtils.problem(HttpStatus.CONFLICT, ErrorCode.UF_DUPLICATE,
                            "Frete já cadastrado para esta UF.", req);
                }
                if (code == 1452 || (msg != null && msg.contains("a foreign key constraint fails") && msg.contains("fk_frete_uf"))) {
                    log.warn("Invalid UF FK (sql code {}): {}", code, msg);
                    return ErrorUtils.problem(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                            "UF inválida (violação de chave estrangeira).", req);
                }
            }
            t = t.getCause();
        }
        log.warn("Integrity violation: {}", ErrorUtils.rootMessage(ex));
        return ErrorUtils.problem(HttpStatus.CONFLICT, ErrorCode.CONFLICT, ErrorUtils.rootMessage(ex), req);
    }

    // Quando você lançar ErrorResponseException com status custom
    @ExceptionHandler(ErrorResponseException.class)
    public ProblemDetail handleErrorResponse(ErrorResponseException ex, WebRequest req) {
        log.warn("ErrorResponseException: {}", ErrorUtils.rootMessage(ex));
        ProblemDetail pd = ex.getBody();
        if (pd.getProperties().get("code") == null) {
            pd.setProperty("code", ErrorCode.BAD_REQUEST.name());
        }
        return pd;
    }

    // 500 - inesperado
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOthers(Exception ex, WebRequest req) {
        log.error("Unexpected error", ex);
        return ErrorUtils.problem(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "Erro interno inesperado.", req);
    }
}
