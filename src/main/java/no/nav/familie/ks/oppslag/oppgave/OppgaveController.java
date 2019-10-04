package no.nav.familie.ks.oppslag.oppgave;

import no.nav.familie.ks.kontrakter.oppgave.Oppgave;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ProtectedWithClaims(issuer = "intern")
@RequestMapping("/api/oppgave")
public class OppgaveController {

    private OppgaveService oppgaveService;

    OppgaveController(OppgaveService oppgaveService) {
        this.oppgaveService = oppgaveService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/opprett")
    public ResponseEntity<String> opprettOppgave(@RequestBody Oppgave request) {
        return oppgaveService.opprettEllerOppdaterOppgave(request);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/oppdater")
    public ResponseEntity oppdaterOppgave(@RequestBody Oppgave request) {
        if (request.getEksisterendeOppgaveId() == null) {
            return ResponseEntity.badRequest().header("message", "Mangler oppgaveId").build();
        }
        return oppgaveService.opprettEllerOppdaterOppgave(request);
    }
}
