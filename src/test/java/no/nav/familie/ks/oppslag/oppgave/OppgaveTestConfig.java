package no.nav.familie.ks.oppslag.oppgave;

import no.nav.familie.ks.oppslag.oppgave.domene.Oppgave;
import no.nav.familie.ks.oppslag.oppgave.internal.OppgaveConsumer;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOppgaveIkkeFunnetException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOptimistiskLasingException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSSikkerhetsbegrensningException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.meldinger.WSOpprettOppgaveResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class OppgaveTestConfig {

    @Bean
    @Profile("mock-oppgave")
    @Primary
    public OppgaveConsumer oppgaveConsumerMock() throws WSSikkerhetsbegrensningException, WSOppgaveIkkeFunnetException, WSOptimistiskLasingException {
        OppgaveConsumer oppgaveConsumer = mock(OppgaveConsumer.class);

        when(oppgaveConsumer.opprettOppgave(matcherBeskrivelse("Test null-response"))).thenReturn(new WSOpprettOppgaveResponse());
        when(oppgaveConsumer.opprettOppgave(matcherBeskrivelse("WS-exception"))).thenThrow(new WSSikkerhetsbegrensningException("feilmelding"));
        when(oppgaveConsumer.oppdaterOppgave(matcherBeskrivelse("WS-exception"))).thenThrow(new WSOppgaveIkkeFunnetException("feilmelding"));

        return oppgaveConsumer;
    }

    private Oppgave matcherBeskrivelse(String beskrivelse) {
        return refEq(new Oppgave(null, null, null, null, beskrivelse, null),
                "gosysSakId", "eksisterendeOppgaveId", "behandlendeEnhetId", "fnr", "behandlingsfristDager");
    }
}
