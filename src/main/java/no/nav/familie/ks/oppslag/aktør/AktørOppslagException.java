package no.nav.familie.ks.oppslag.aktør;

import org.springframework.http.HttpStatus;

public class AktørOppslagException extends RuntimeException {
    private HttpStatus httpStatus;

    public AktørOppslagException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
