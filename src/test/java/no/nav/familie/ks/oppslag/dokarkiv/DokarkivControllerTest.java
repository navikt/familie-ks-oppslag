package no.nav.familie.ks.oppslag.dokarkiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.oppslag.DevLauncher;
import no.nav.familie.ks.oppslag.dokarkiv.api.ArkiverDokumentRequest;
import no.nav.familie.ks.oppslag.dokarkiv.api.Dokument;
import no.nav.familie.ks.oppslag.dokarkiv.api.DokumentType;
import no.nav.familie.ks.oppslag.dokarkiv.api.FilType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
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
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"dev", "mock-sts"})
public class DokarkivControllerTest {
    public static final int MOCK_SERVER_PORT = 18321;
    public static final String JOURNALPOST_ID = "12345678";
    public static final String FULLT_NAVN = "Foo Bar";
    public static final String DOKARKIV_URL = "/api/dokarkiv/";
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, MOCK_SERVER_PORT);

    private ObjectMapper objectMapper = new ObjectMapper();
    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private MockServerClient mockServerClient;
    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> cookie = restTemplate.exchange(
                createURLWithPort("/local/cookie"), HttpMethod.GET, entity, String.class);
        headers.add("Authorization", "Bearer " + cookie.getBody().split("value\":\"")[1].split("\",\"")[0]);
    }

    @Test
    public void skal_returnere_Bad_Request_hvis_fNr_mangler() {
        ArkiverDokumentRequest body = new ArkiverDokumentRequest(null, FULLT_NAVN, DokumentType.KONTANTSTØTTE_SØKNAD, false, List.of(new Dokument("foo".getBytes(), FilType.PDFA)));

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(DOKARKIV_URL), HttpMethod.POST, new HttpEntity<ArkiverDokumentRequest>(body, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("fnr\":\"must not be blank");
    }

    @Test
    public void skal_returnere_Bad_Request_hvis_ingen_dokumenter() {
        ArkiverDokumentRequest body = new ArkiverDokumentRequest("fnr", "Foobar", DokumentType.KONTANTSTØTTE_SØKNAD, false, new LinkedList<>());

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(DOKARKIV_URL), HttpMethod.POST, new HttpEntity<ArkiverDokumentRequest>(body, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("dokumenter\":\"must not be empty");
    }

    @Test
    public void skal_midlertidig_journalføre_dokument() throws IOException {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("POST")
                                .withPath("/rest/journalpostapi/v1/journalpost")
                                .withQueryStringParameter("foersoekFerdigstill", "false")
                )
                .respond(
                        HttpResponse.response().withBody(gyldigDokarkivResponse())
                );


        ArkiverDokumentRequest body = new ArkiverDokumentRequest("FNR", FULLT_NAVN, DokumentType.KONTANTSTØTTE_SØKNAD, false, List.of(new Dokument("foo".getBytes(), FilType.PDFA)));
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(DOKARKIV_URL), HttpMethod.POST, new HttpEntity<>(body, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo("{\"journalpostId\":\"12345678\",\"ferdigstilt\":false}");
    }

    @Test
    public void dokarkiv_returnerer_401() throws IOException {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("POST")
                                .withPath("/rest/journalpostapi/v1/journalpost")
                                .withQueryStringParameter("foersoekFerdigstill", "false")
                )
                .respond(
                        HttpResponse.response().withStatusCode(401)
                );


        ArkiverDokumentRequest body = new ArkiverDokumentRequest("FNR", "Foobar", DokumentType.KONTANTSTØTTE_SØKNAD, false, List.of(new Dokument("foo".getBytes(), FilType.PDFA)));
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(DOKARKIV_URL), HttpMethod.POST, new HttpEntity<>(body, headers), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private String gyldigDokarkivResponse() throws IOException {
        return Files.readString(new ClassPathResource("dokarkiv/gyldigresponse.json").getFile().toPath(), StandardCharsets.UTF_8);
    }
}
