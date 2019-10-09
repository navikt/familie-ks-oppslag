package no.nav.familie.ks.oppslag.infotrygd;

import no.nav.familie.ks.oppslag.infotrygd.domene.AktivKontantstøtteInfo;
import no.nav.security.oidc.api.ProtectedWithClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;
import javax.validation.constraints.NotNull;

@RestController
@ProtectedWithClaims(issuer = "intern")
@RequestMapping("/api/infotrygd")
public class InfotrygdController {
    private static final Logger LOG = LoggerFactory.getLogger(InfotrygdController.class);

    private InfotrygdService infotrygdService;

    public InfotrygdController(InfotrygdService infotrygdService) {
        this.infotrygdService = infotrygdService;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpServerErrorException.class)
    public Map<String, String> handleExceptions(HttpServerErrorException ex) {
        LOG.warn("Infotrygd-kontantstøtte 5xx-feil: " + ex.getStatusText());
        return Map.of("error", ex.getStatusText());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpClientErrorException.class)
    public Map<String, String> handleExceptions(HttpClientErrorException ex) {
        LOG.warn("Infotrygd-kontantstøtte 4xx-feil: " + ex.getStatusText());
        return Map.of("error", ex.getStatusText());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "harBarnAktivKontantstotte")
    public AktivKontantstøtteInfo aktivKontantstøtte(@NotNull @RequestHeader(name = "Nav-Personident") String fnr) {
        return infotrygdService.hentAktivKontantstøtteFor(fnr);
    }
}
