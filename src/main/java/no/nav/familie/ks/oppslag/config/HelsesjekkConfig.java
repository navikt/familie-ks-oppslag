package no.nav.familie.ks.oppslag.config;

import no.nav.familie.ks.oppslag.helse.PersonV3Helsesjekk;
import no.nav.familie.ks.oppslag.personopplysning.internal.PersonConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ServiceConfig.class)
public class HelsesjekkConfig {

    @Bean
    public PersonV3Helsesjekk personV3Helsesjekk(PersonConsumer personConsumer) {
        return new PersonV3Helsesjekk(personConsumer);
    }
}
