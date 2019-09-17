package no.nav.familie.ks.oppslag.personopplysning;

import no.nav.familie.ks.oppslag.DevLauncher;
import no.nav.familie.ks.oppslag.personopplysning.domene.Personinfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"dev", "mock-aktor", "mock-personopplysninger"})
public class PersonopplysningControllerTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    public void testHttpResponse() {

        ResponseEntity<String> cookie = restTemplate.exchange(
                url("/local/cookie"), HttpMethod.GET, new HttpEntity<String>(null, headers), String.class
        );
        headers.add("Authorization", "Bearer " + cookie.getBody().split("value\":\"")[1].split("\",\"")[0]);

        ResponseEntity<Personinfo> response = restTemplate.exchange(
                url("/api/personopplysning/info?id=1"), HttpMethod.GET, new HttpEntity<String>(null, headers), Personinfo.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private String url(String uri) {
        return "http://localhost:" + port + uri;
    }
}

