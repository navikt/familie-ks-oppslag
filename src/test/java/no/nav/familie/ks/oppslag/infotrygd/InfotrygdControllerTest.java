package no.nav.familie.ks.oppslag.infotrygd;

import no.nav.familie.ks.oppslag.OppslagSpringRunnerTest;
import no.nav.familie.ks.oppslag.infotrygd.domene.AktivKontantstøtteInfo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"dev", "mock-sts"})
public class InfotrygdControllerTest extends OppslagSpringRunnerTest {
    @Before
    public void setUp() {
        headers.setBearerAuth(getLokalTestToken());
    }
    
    @Test
    public void skal_gi_bad_request_hvis_fnr_mangler() {
        var response = restTemplate.exchange(
                localhost("/api/infotrygd/harBarnAktivKontantstotte"), HttpMethod.GET, new HttpEntity(headers), AktivKontantstøtteInfo.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}