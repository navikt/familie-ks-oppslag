package no.nav.familie.ks.oppslag.oppgave;

import no.nav.familie.ks.oppslag.oppgave.domene.Oppgave;
import no.nav.familie.ks.oppslag.oppgave.internal.OppgaveConsumer;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOppgaveIkkeFunnetException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOptimistiskLasingException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSSikkerhetsbegrensningException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.soap.SoapFaultException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import static org.springframework.http.HttpStatus.*;

@Service
@ApplicationScope
public class OppgaveService {

    private final OppgaveConsumer oppgaveConsumer;

    @Autowired
    public OppgaveService(OppgaveConsumer oppgaveConsumer) {
        this.oppgaveConsumer = oppgaveConsumer;
    }

    ResponseEntity opprettEllerOppdaterOppgave(Oppgave request) {
        try {
            if (request.getEksisterendeOppgaveId() != null) {
                return ResponseEntity.ok(oppgaveConsumer.oppdaterOppgave(request));
            } else {
                return opprettOppgaveResponse(request);
            }
        } catch (WSSikkerhetsbegrensningException | WSOppgaveIkkeFunnetException | WSOptimistiskLasingException e) {
            return ResponseEntity.status(setPassendeStatus(e)).header("message", e.getMessage()).build();
        } catch (SoapFaultException e) {
            return ResponseEntity.status(PRECONDITION_FAILED)
                    .header("message", String.format("SOAP tjenesten returnerte en SOAP Fault: %s", e.getMessage())).build();
        }
    }

    private ResponseEntity opprettOppgaveResponse(Oppgave request) throws WSSikkerhetsbegrensningException {
        var response = oppgaveConsumer.opprettOppgave(request);
        if (response.getOppgaveId() != null) {
            return ResponseEntity.ok(response.getOppgaveId());
        }
        return ResponseEntity.status(EXPECTATION_FAILED)
                .header("message", "Ugyldig respons: Fikk ingen oppgaveId tilbake fra GSAK").build();
    }

    private HttpStatus setPassendeStatus(Exception e) {
        return e instanceof WSSikkerhetsbegrensningException ? UNAUTHORIZED :
                e instanceof WSOppgaveIkkeFunnetException ? NOT_FOUND : EXPECTATION_FAILED;
    }
}
