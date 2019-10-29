package no.nav.familie.ks.oppslag.config;

import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({OIDCUnauthorizedException.class, HttpClientErrorException.Unauthorized.class})
    public Map<String, String> handleUnauthorizedException(RuntimeException e) {
        logger.warn("Bruker kan ikke logge inn", e);
        return Map.of("error", "Du er ikke logget inn");
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RestClientResponseException.class})
    public Map<String, String> handleRestClientResponseException(RestClientResponseException e) {
        secureLogger.error("RestClientResponseException : {} {} {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
        logger.error("RestClientResponseException : {}", e.getResponseBodyAsString(), e);
        return Map.of("error", String.format("Feil ved oppslag. %s %s %s", e.getRawStatusCode(), e.getResponseBodyAsString(), e.getMessage()));
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public Map<String, String> handleException(Exception e) {
        secureLogger.error("Exception : ", e);
        logger.error("Exception : ", e);
        return Map.of("error", "Det oppstod en feil " + e.getMessage());
    }

}
