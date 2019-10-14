package no.nav.familie.ks.oppslag.oppgave.internal;

import no.nav.tjeneste.virksomhet.behandleoppgave.v1.BehandleOppgaveV1;
import no.nav.tjeneste.virksomhet.oppgave.v3.binding.OppgaveV3;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class OppgaveConsumerConfig {

    @Bean
    public OppgaveConsumer oppgaveConsumer(BehandleOppgaveV1 behandleOppgaveV1Port, OppgaveV3 oppgaveV3Port) {
        return new OppgaveConsumer(behandleOppgaveV1Port, oppgaveV3Port);
    }
}
