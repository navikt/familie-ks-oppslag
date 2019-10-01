package no.nav.familie.ks.oppslag.aktør;

import no.nav.familie.http.azure.AccessTokenClient;
import no.nav.familie.http.azure.AccessTokenDto;
import no.nav.familie.ks.oppslag.personopplysning.domene.AktørId;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@ProtectedWithClaims(issuer = "intern")
@RequestMapping("/api/aktoer")
public class AktørController {

    private AktørService aktørService;
    private AccessTokenClient accessTokenClient;

    @Autowired
    AktørController(AktørService aktørService, AccessTokenClient accessTokenClient) {
        this.aktørService = aktørService;
        this.accessTokenClient = accessTokenClient;
    }

    @GetMapping
    public ResponseEntity<String> getAktørIdForPersonIdent(@NotNull @RequestHeader(name = "Nav-Personident") String personIdent) {
        return aktørService.getAktørId(personIdent);
    }

    @GetMapping(path = "/fraaktorid")
    public ResponseEntity<String> getPersonIdentForAktørId(@NotNull @RequestHeader(name = "Nav-Aktorid") String aktørId) {
        return aktørService.getPersonIdent(new AktørId(aktørId));
    }

    @GetMapping(path = "/azureToken")
    @Unprotected
    public ResponseEntity<AccessTokenDto> getAccessToken(@NotNull @RequestHeader(name = "resource") String resource) {
        AccessTokenDto accessTokenDto = accessTokenClient.getAccessToken(resource);
        return ResponseEntity.ok(accessTokenDto);
    }
}
