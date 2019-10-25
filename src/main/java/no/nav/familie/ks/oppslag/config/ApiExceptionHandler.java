package no.nav.familie.ks.oppslag.config;

import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({OIDCUnauthorizedException.class, HttpClientErrorException.Unauthorized.class})
    public ResponseEntity<String> handleUnauthorizedException() {
        logger.warn("Kan ikke behandle pga. bruker ikke logget inn.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Du er ikke logget inn");
    }

    @ExceptionHandler({RestClientResponseException.class})
    public ResponseEntity<String> handleRestClientResponseException(RestClientResponseException e) {
        HttpStatus status = HttpStatus.resolve(e.getRawStatusCode());
        return status != null ? handleRestException(e, status) : handleException(e);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }


    private ResponseEntity<String> handleRestException(RestClientResponseException e, HttpStatus status) {
        secureLogger.error("RestClientResponseException : ", e);
        logger.error("RestClientResponseException : {} {} \n{}", status.value(), status.getReasonPhrase(), ExceptionUtils.getStackTrace(e));
        return ResponseEntity.status(status).header("message", e.getMessage()).build();
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        secureLogger.error("Exception : ", e);
        logger.error("Exception : {}", ExceptionUtils.getStackTrace(e));
        return ResponseEntity.status(status).body("Det oppstod en Feil. Feilen er logget og vi ser på problemet!");
    }

}
