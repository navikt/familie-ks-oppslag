package no.nav.familie.ks.oppslag.medlemskap.internal;

import no.nav.familie.ks.oppslag.felles.rest.StsRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MedlClientConfig {

    @Bean
    public MedlClient medlClient(@Value("${MEDL2_URL}") String url,
                                 @Value("${CREDENTIAL_USERNAME}") String srvBruker,
                                 @Autowired StsRestClient stsRestClient) {
        return new MedlClient(url, srvBruker, stsRestClient);
    }
}
