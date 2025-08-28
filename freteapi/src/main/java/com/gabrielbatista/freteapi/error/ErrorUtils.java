package com.gabrielbatista.freteapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.net.URI;
import java.time.OffsetDateTime;

public final class ErrorUtils {

    private ErrorUtils() {}

    public static ProblemDetail problem(HttpStatus status, ErrorCode code, String detail, WebRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(status.getReasonPhrase());
        pd.setType(URI.create("about:blank")); // pode apontar pra tua doc
        pd.setProperty("code", code.name());
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        if (req instanceof ServletWebRequest sw) {
            pd.setInstance(URI.create(sw.getRequest().getRequestURI()));
            pd.setProperty("method", sw.getRequest().getMethod());
        }
        return pd;
    }

    public static String rootMessage(Throwable ex) {
        Throwable t = ex;
        while (t.getCause() != null && t.getCause() != t) t = t.getCause();
        return t.getMessage() != null ? t.getMessage() : ex.toString();
    }
}
