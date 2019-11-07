package no.nav.familie.ks.oppslag.config;

import no.nav.familie.http.azure.AzureAccessTokenException;
import no.nav.familie.ks.kontrakter.sak.Ressurs;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    public ApiExceptionHandler() {
    }

    @ExceptionHandler({JwtTokenUnauthorizedException.class})
    public ResponseEntity<Ressurs> handleUnauthorizedException(JwtTokenUnauthorizedException e) {
        logger.warn("Kan ikke logget inn.", e);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Ressurs.Companion.failure("Du er ikke logget inn.", null));
    }

    @ExceptionHandler({RestClientResponseException.class})
    public ResponseEntity<Ressurs> handleRestClientResponseException(RestClientResponseException e) {
        secureLogger.error("RestClientResponseException : {} {}", e.getResponseBodyAsString(), e);
        logger.error("RestClientResponseException : {} {} {}", e.getRawStatusCode(), e.getStatusText(), ExceptionUtils.getStackTrace(e));
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(Ressurs.Companion.failure("Feil mot ekstern tjeneste. " + e.getRawStatusCode() + " " + e.getResponseBodyAsString() + " Message=" + e.getMessage(), null));
    }

    @ExceptionHandler({AzureAccessTokenException.class})
    public ResponseEntity<Ressurs> handleRestClientResponseException(AzureAccessTokenException e) {
        logger.error("AzureAccessTokenException : {} ", ExceptionUtils.getStackTrace(e));
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(Ressurs.Companion.failure("Feil mot azure. Message=" + e.getMessage(), null));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Ressurs> handleException(Exception e) {
        secureLogger.error("Exception : ", e);
        logger.error("Exception : {}", ExceptionUtils.getStackTrace(e));
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(Ressurs.Companion.failure("Det oppstod en feil. " + e.getMessage(), null));
    }

}
