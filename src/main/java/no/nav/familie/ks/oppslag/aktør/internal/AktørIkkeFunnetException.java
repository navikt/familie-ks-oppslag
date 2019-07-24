package no.nav.familie.ks.oppslag.aktør.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Identen finnes ikke i Aktørregisteret")
public class AktørIkkeFunnetException extends RuntimeException {

    public AktørIkkeFunnetException(String ident) {
        super(String.format("Ident %s finnes ikke i Aktørregisteret", ident));
    }
}
