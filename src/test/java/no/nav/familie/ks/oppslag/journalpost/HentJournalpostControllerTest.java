package no.nav.familie.ks.oppslag.journalpost;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import no.nav.familie.ks.oppslag.DevLauncher;
import no.nav.security.oidc.test.support.JwtTokenGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.ConnectionOptions;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"dev", "mock-sts"})
public class HentJournalpostControllerTest {
    public static final int MOCK_SERVER_PORT = 18321;
    public static final String JOURNALPOST_ID = "12345678";
    public static final String SAKSNUMMER = "87654321";
    public static final String JOURNALPOST_BASE_URL = "/api/journalpost/";
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, MOCK_SERVER_PORT);

    private ObjectMapper objectMapper = new ObjectMapper();
    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        headers.setBearerAuth(JwtTokenGenerator.signedJWTAsString("testbruker"));
    }

    @Test
    public void skal_returnere_saksnummer_og_status_OK() throws IOException {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("POST")
                                .withPath("/rest/saf/graphql")
                                .withBody(testdata("gyldigrequest.json"))
                )
                .respond(
                        HttpResponse.response().withBody(testdata("gyldigresponse.json")).withHeaders(
                                new Header("Content-Type", "application/json"))
                );


        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(JOURNALPOST_BASE_URL + JOURNALPOST_ID + "/sak"), HttpMethod.GET, new HttpEntity<String>(null, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(SAKSNUMMER);
    }

    @Test
    public void skal_returnere_status_404_hvis_sak_mangler() throws IOException {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("POST")
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withPath("/rest/saf/graphql")
                )
                .respond(
                        HttpResponse.response().withBody(testdata("mangler_sak.json")).withHeaders(
                                new Header("Content-Type", "application/json"))
                );


        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(JOURNALPOST_BASE_URL + JOURNALPOST_ID + "/sak"), HttpMethod.GET, new HttpEntity<String>(null, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Sak mangler for journalpostId=" + JOURNALPOST_ID);
    }

    @Test
    public void skal_returnere_status_404_hvis_sak_ikke_er_GSAK() throws IOException {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("POST")
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withPath("/rest/saf/graphql")
                )
                .respond(
                        HttpResponse.response().withBody(testdata("feil_arkivsaksystem.json")).withHeaders(
                                new Header("Content-Type", "application/json"))
                );


        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(JOURNALPOST_BASE_URL + JOURNALPOST_ID + "/sak"), HttpMethod.GET, new HttpEntity<String>(null, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Sak mangler for journalpostId=" + JOURNALPOST_ID);
    }

    @Test
    public void skal_status_404_hvis_journalpost_ikke_finnes() {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("POST")
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withPath("/rest/saf/graphql")
                )

                .respond(
                        HttpResponse.response().withStatusCode(404)
                );


        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(JOURNALPOST_BASE_URL + JOURNALPOST_ID + "/sak"), HttpMethod.GET, new HttpEntity<String>(null, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Fant ikke journalpost med id=" + JOURNALPOST_ID);
    }

    @Test
    public void skal_returnere_500_ved_ukjent_feil() {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("POST")
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withPath("/rest/saf/graphql")
                )

                .respond(
                        HttpResponse.response().withStatusCode(500).withBody("feilmelding")
                );


        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(JOURNALPOST_BASE_URL + JOURNALPOST_ID + "/sak"), HttpMethod.GET, new HttpEntity<String>(null, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Feil ved henting av journalpostId=" + JOURNALPOST_ID);
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private String testdata(String filnavn) throws IOException {
        return Files.readString(new ClassPathResource("saf/" + filnavn).getFile().toPath(), StandardCharsets.UTF_8);
    }
}