package no.nav.familie.ks.oppslag.oppgave;


import com.fasterxml.jackson.core.JsonProcessingException;
import no.nav.familie.ks.oppslag.oppgave.internal.OppgaveClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@Configuration
public class OppgaveTestConfig {

    @Bean
    @Profile("mock-oppgave")
    @Primary
    public OppgaveClient oppgaveClientMock() throws JsonProcessingException {
        OppgaveClient oppgaveClientMock = mock(OppgaveClient.class);

        doNothing().when(oppgaveClientMock).oppdaterOppgave(any(), anyString());
        doNothing().when(oppgaveClientMock).ping();
        return oppgaveClientMock;
    }
}
