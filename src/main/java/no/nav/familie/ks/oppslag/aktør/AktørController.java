package no.nav.familie.ks.oppslag.aktør;

import no.nav.familie.ks.oppslag.felles.MDCOperations;
import no.nav.security.oidc.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@ProtectedWithClaims(issuer = "intern")
@RequestMapping("/api/aktoer")
public class AktørController {

    private AktørService aktørService;

    AktørController(AktørService aktørService) {
        this.aktørService = aktørService;
    }

    @GetMapping
    @Deprecated
    public String getAktoerIdForPersonIdent(@NotNull @RequestParam(name = "ident") String personIdent) {
        MDCOperations.putCallId(); // FIXME: Midlertidig, bør erstattes med en interceptor
        return aktørService.getAktørId(personIdent);
    }

    @GetMapping
    public String getAktørIdForPersonIdent(@NotNull @RequestHeader(name = "Nav-Personident") String personIdent) {
        MDCOperations.putCallId();
        return aktørService.getAktørId(personIdent);
    }
}
