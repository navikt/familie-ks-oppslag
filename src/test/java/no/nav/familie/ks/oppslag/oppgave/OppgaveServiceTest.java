package no.nav.familie.ks.oppslag.oppgave;

import no.nav.familie.ks.oppslag.oppgave.domene.Oppgave;
import no.nav.familie.ks.oppslag.oppgave.internal.OppgaveConsumer;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOppgaveIkkeFunnetException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOptimistiskLasingException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSSikkerhetsbegrensningException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class OppgaveServiceTest {

    private OppgaveConsumer oppgaveConsumer;
    private OppgaveService oppgaveService;

    @Before
    public void setUp() throws WSSikkerhetsbegrensningException, WSOppgaveIkkeFunnetException, WSOptimistiskLasingException {
        oppgaveConsumer = new OppgaveTestConfig().oppgaveConsumerMock();
        oppgaveService = new OppgaveService(oppgaveConsumer);
    }

    @Test
    public void opprettOppgaveSkalGiFeilHvisIngenOppgaveIdBlirReturnert() {
        String forventetFeilmelding = "Ugyldig respons: Fikk ingen oppgaveId tilbake fra GSAK";

        Oppgave mock = new Oppgave(null, null, null, null, "Test null-response", null);
        ResponseEntity response = oppgaveService.opprettEllerOppdaterOppgave(mock);
        assertThat(Objects.requireNonNull(response.getHeaders().get("message")).get(0)).isEqualTo(forventetFeilmelding);
    }

    @Test
    public void opprettEllerOppdaterOppgaveSkalReturnereFeilMeldingVedExcetion() {
        String forventetFeilmelding = "feilmelding";

        Oppgave opprett = new Oppgave(null, null, null, null, "WS-exception", null);
        Oppgave oppdater = new Oppgave("1234", null, null, null, "WS-exception", null);

        ResponseEntity opprettResponse = oppgaveService.opprettEllerOppdaterOppgave(opprett);
        ResponseEntity oppdaterResponse = oppgaveService.opprettEllerOppdaterOppgave(oppdater);

        assertThat(Objects.requireNonNull(opprettResponse.getHeaders().get("message")).get(0)).isEqualTo(forventetFeilmelding);
        assertThat(Objects.requireNonNull(oppdaterResponse.getHeaders().get("message")).get(0)).isEqualTo(forventetFeilmelding);
    }

}
