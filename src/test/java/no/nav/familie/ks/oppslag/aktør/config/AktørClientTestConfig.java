package no.nav.familie.ks.oppslag.aktør.config;

import no.nav.familie.ks.oppslag.aktør.domene.Aktør;
import no.nav.familie.ks.oppslag.aktør.domene.Ident;
import no.nav.familie.ks.oppslag.aktør.internal.AktørResponse;
import no.nav.familie.ks.oppslag.aktør.internal.AktørregisterClient;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class AktørClientTestConfig {

    @Bean
    @Profile("mock-aktor")
    @Primary
    public AktørregisterClient aktørregisterClientMock() {
        AktørregisterClient aktørregisterClient = mock(AktørregisterClient.class);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Ident testIdent = new Ident().withIdent("1000011111111");

        when(aktørregisterClient.hentAktørId(stringCaptor.capture())).thenAnswer(invocation -> {
            String identArg = invocation.getArgument(0);

            return new AktørResponse()
                    .withAktør(identArg, new Aktør().withIdenter(Collections.singletonList(testIdent)));
        });

        when(aktørregisterClient.hentPersonIdent(stringCaptor.capture())).thenAnswer(invocation -> {
            String identArg = invocation.getArgument(0);

            return new AktørResponse()
                    .withAktør(identArg, new Aktør().withIdenter(Collections.singletonList(testIdent)));
        });
        return aktørregisterClient;
    }
}
