package no.nav.familie.ks.oppslag.personopplysning.internal;

import no.nav.familie.ks.oppslag.felles.ws.sts.NAVSTSClient;
import no.nav.familie.ks.oppslag.felles.ws.sts.StsConfigurationUtil;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonConsumerProducer {

    @Bean
    public PersonConsumer personConsumer(PersonV3 consumerPort) {
        //PersonV3 port = wrapWithSts(consumerPort, NAVSTSClient.StsClientType.SYSTEM_SAML);
        return new PersonConsumer(consumerPort);
    }

    private PersonV3 wrapWithSts(PersonV3 port, NAVSTSClient.StsClientType samlTokenType) {
        return StsConfigurationUtil.wrapWithSts(port, samlTokenType);
    }

}
