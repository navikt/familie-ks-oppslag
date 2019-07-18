package no.nav.familie.ks.oppslag.personopplysning.internal;

import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonConsumerProducer {

    @Bean
    public PersonConsumer personConsumer(PersonV3 consumerPort) {
        return new PersonConsumer(consumerPort);
    }

}
