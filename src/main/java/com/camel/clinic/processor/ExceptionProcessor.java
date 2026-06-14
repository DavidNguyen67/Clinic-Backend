package com.camel.clinic.processor;

import com.camel.clinic.dto.RestErrorDto;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptionProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        Exception cause = exchange.getProperty(
                Exchange.EXCEPTION_CAUGHT, Exception.class
        );

        int status;
        String code;
        String message;

        if (cause instanceof BadRequestException
                || cause instanceof org.apache.coyote.BadRequestException
                || cause instanceof IllegalArgumentException) {

            status = 400;
            code = "BAD_REQUEST";

        } else if (cause instanceof UnauthorizedException) {

            status = 401;
            code = "UNAUTHORIZED";

        } else if (cause instanceof AccessDeniedException) {

            status = 403;
            code = "FORBIDDEN";

        } else if (cause instanceof EntityNotFoundException
                || cause instanceof NotFoundException) {

            status = 404;
            code = "NOT_FOUND";

        } else {

            status = 500;
            code = "INTERNAL_SERVER_ERROR";
        }

        message = resolveMessage(cause, status);
        logException(exchange, cause, status, code, message);

        RestErrorDto errorResponse = new RestErrorDto();
        errorResponse.setStatusCode(code);
        errorResponse.setStatusCodeValue(status);
        errorResponse.setBody(message);

        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, status);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(errorResponse);
    }

    private String resolveMessage(Exception ex, int status) {
        if (ex == null) return "Unknown error";

        if (status == 500) {
            return ex.getMessage() != null
                    ? ex.getMessage()
                    : "An unexpected error occurred";
        }

        return ex.getMessage() != null
                ? ex.getMessage()
                : "Request failed";
    }

    private void logException(Exchange exchange, Exception cause, int status, String code, String message) {
        String routeId = exchange.getFromRouteId();
        String exchangeId = exchange.getExchangeId();
        String method = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);
        String path = exchange.getIn().getHeader(Exchange.HTTP_PATH, String.class);

        if (status >= 500) {
            log.error("Request failed exchangeId={} routeId={} method={} path={} status={} code={} message={}",
                    exchangeId, routeId, method, path, status, code, message, cause);
        } else {
            log.warn("Request rejected exchangeId={} routeId={} method={} path={} status={} code={} message={}",
                    exchangeId, routeId, method, path, status, code, message);
        }
    }
}
