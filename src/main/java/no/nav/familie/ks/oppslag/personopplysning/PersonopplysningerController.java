package no.nav.familie.ks.oppslag.personopplysning;

import no.nav.familie.ks.kontrakter.sak.Ressurs;
import no.nav.familie.ks.oppslag.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.oppslag.personopplysning.domene.Personinfo;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
@ProtectedWithClaims(issuer = "azuread")
@RequestMapping("/api/personopplysning")
public class PersonopplysningerController {

    private PersonopplysningerService personopplysningerService;

    public PersonopplysningerController(PersonopplysningerService personopplysningerService) {
        this.personopplysningerService = personopplysningerService;
    }

    @ExceptionHandler({HttpClientErrorException.NotFound.class, HttpClientErrorException.Forbidden.class})
    public ResponseEntity<Ressurs> handleRestClientResponseException(RestClientResponseException e) {
        return ResponseEntity
                .status(e.getRawStatusCode())
                .body(Ressurs.Companion.failure("Feil mot ekstern tjeneste. " + e.getRawStatusCode() + " " + e.getResponseBodyAsString() + " Message=" + e.getMessage(), null));
    }

    @Deprecated
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "historikk")
    public ResponseEntity<PersonhistorikkInfo> historikkGammel(@NotNull @RequestHeader(name = "Nav-Personident") String personIdent,
                                                         @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fomDato,
                                                         @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tomDato) {
        return personopplysningerService.hentHistorikkForGammel(personIdent, fomDato, tomDato);
    }

    @Deprecated
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "info")
    public ResponseEntity<Personinfo> personInfoGammel(@NotNull @RequestHeader(name = "Nav-Personident") String personIdent) {
        return personopplysningerService.hentPersoninfoForGammel(personIdent);
    }


    @Deprecated
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "v1/historikk")
    public ResponseEntity<Ressurs> historikk(@NotNull @RequestHeader(name = "Nav-Personident") String personIdent,
                                                         @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fomDato,
                                                         @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tomDato) {
        return ResponseEntity.ok().body(Ressurs.Companion.success(personopplysningerService.hentHistorikkFor(personIdent, fomDato, tomDato),"Hent personhistorikk OK"));
    }

    @Deprecated
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "v1/info")
    public ResponseEntity<Ressurs> personInfo(@NotNull @RequestHeader(name = "Nav-Personident") String personIdent) {
        return ResponseEntity.ok().body(Ressurs.Companion.success(personopplysningerService.hentPersoninfoFor(personIdent), "Hent personinfo OK"));
    }
}
