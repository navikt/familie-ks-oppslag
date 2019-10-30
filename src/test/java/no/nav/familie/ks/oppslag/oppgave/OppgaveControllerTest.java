package no.nav.familie.ks.oppslag.oppgave;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import no.nav.familie.ks.kontrakter.oppgave.Oppgave;
import no.nav.familie.ks.oppslag.OppslagSpringRunnerTest;
import no.nav.familie.ks.oppslag.config.ApiExceptionHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(profiles = {"dev", "mock-sts", "mock-aktor"})
public class OppgaveControllerTest extends OppslagSpringRunnerTest {
    private static final String OPPDATER_OPPGAVE_URL = "/api/oppgave/oppdater";
    private static final Integer MOCK_SERVER_PORT = 18321;

    private Logger oppgaveControllerLogger = (Logger) LoggerFactory.getLogger(OppgaveController.class);
    private Logger exceptionHandler = (Logger) LoggerFactory.getLogger(ApiExceptionHandler.class);

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, MOCK_SERVER_PORT);

    @Before
    public void setup() {
        oppgaveControllerLogger.addAppender(listAppender);
        exceptionHandler.addAppender(listAppender);

        headers.setBearerAuth(getLokalTestToken());
    }

    @Test
    public void skal_logge_stack_trace_ved_NullPointerException() {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("GET")
                                .withPath("/oppgaver")
                )
                .respond(
                        HttpResponse.response()
                );

        Oppgave test = new Oppgave("1234567891011", "1", null, "test NPE");

        restTemplate.exchange(
                localhost(OPPDATER_OPPGAVE_URL), HttpMethod.POST, new HttpEntity<>(test, headers), String.class
        );
        assertThat(loggingEvents).extracting(ILoggingEvent::getFormattedMessage).anyMatch(s -> s.contains("java.lang.NullPointerException\n\tat"));
    }

    @Test
    public void skal_skrive_til_logg_når_det_oppstår_RestClientException() {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("GET")
                                .withPath("/oppgaver")
                )
                .respond(
                        HttpResponse.notFoundResponse()
                );

        Oppgave test = new Oppgave("1234567891011", "1", null, "test RestClientException");

        restTemplate.exchange(
                localhost(OPPDATER_OPPGAVE_URL), HttpMethod.POST, new HttpEntity<>(test, headers), String.class
        );
        assertThat(loggingEvents).extracting(ILoggingEvent::getFormattedMessage).anyMatch(s -> s.contains("HttpClientErrorException"));
    }

    @Test
    public void skal_skrive_til_logg_hvis_oppgave_ikke_ble_funnet() throws IOException {
        mockServerRule.getClient()
                .when(
                        HttpRequest
                                .request()
                                .withMethod("GET")
                                .withPath("/oppgaver")
                )
                .respond(
                        HttpResponse.response().withBody(gyldigOppgaveResponse()).withHeaders(
                                new Header("Content-Type", "application/json"))
                );

        Oppgave test = new Oppgave("1234567891011", "1", null, "test oppgave ikke funnet");

        restTemplate.exchange(
                localhost(OPPDATER_OPPGAVE_URL), HttpMethod.POST, new HttpEntity<>(test, headers), String.class
        );
        assertThat(loggingEvents).extracting(ILoggingEvent::getFormattedMessage).anyMatch(s ->
                s.contains("OppgaveIkkeFunnetException: Mislykket finnOppgave request med url: http://localhost:18321/oppgaver?aktoerId=1234567891011")
        );
    }

    private String gyldigOppgaveResponse() throws IOException {
        return Files.readString(new ClassPathResource("oppgave/tom_response.json").getFile().toPath(), StandardCharsets.UTF_8);
    }
}
