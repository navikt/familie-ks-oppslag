package no.nav.familie.ks.oppslag.aktør.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import no.nav.familie.http.client.NavHttpHeaders;
import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.oppslag.felles.MDCOperations;
import no.nav.familie.log.mdc.MDCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class AktørregisterClient {

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_PERSONIDENTER = "Nav-Personidenter";
    private static final String AKTOERID_IDENTGRUPPE = "AktoerId";
    private static final String PERSONIDENT_IDENTGRUPPE = "NorskIdent";
    private final Timer aktoerResponstid = Metrics.timer("aktoer.respons.tid");
    private final Counter aktoerSuccess = Metrics.counter("aktoer.response", "status", "success");
    private final Counter aktoerFailure = Metrics.counter("aktoer.response", "status", "failure");
    private static final Logger LOG = LoggerFactory.getLogger(AktørregisterClient.class);
    private HttpClient httpClient;
    private StsRestClient stsRestClient;
    private ObjectMapper objectMapper;
    private String aktørRegisterUrl;
    private String consumer;
    private RestTemplate restTemplate;

    @Autowired
    public AktørregisterClient(@Value("${AKTOERID_URL}") String aktørRegisterUrl,
                               @Value("${CREDENTIAL_USERNAME}") String consumer,
                               StsRestClient stsRestClient) {
        this.stsRestClient = stsRestClient;
        this.consumer = consumer;
        this.aktørRegisterUrl = aktørRegisterUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    public AktørResponse hentAktørId(String personIdent) {
        URI uri = URI.create(String.format("%s/api/v1/identer?gjeldende=true&identgruppe=%s", aktørRegisterUrl, AKTOERID_IDENTGRUPPE));
        return hentRespons(personIdent, uri);
    }

    private AktørResponse hentRespons(String personIdent, URI uri) {
        String systembrukerToken = stsRestClient.getSystemOIDCToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(ACCEPT, "application/json")
                .header(NAV_PERSONIDENTER, personIdent)
                .header(NAV_CONSUMER_ID, consumer)
                .header(NAV_CALL_ID, MDCOperations.getCallId())
                .header(AUTHORIZATION, "Bearer " + systembrukerToken)
                .timeout(Duration.ofSeconds(5))
                .build();
        try {
            long startTime = System.nanoTime();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            aktoerResponstid.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            aktoerSuccess.increment();
            return objectMapper.readValue(httpResponse.body(), AktørResponse.class);
        } catch (IOException | InterruptedException e) {
            aktoerFailure.increment();
            throw new RuntimeException("Feil ved kall mot Aktørregisteret", e);
        }
    }


    public AktørResponse hentPersonIdent(String personIdent) {
        URI uri = URI.create(String.format("%s/api/v1/identer?gjeldende=true&identgruppe=%s", aktørRegisterUrl, PERSONIDENT_IDENTGRUPPE));
        return hentRespons(personIdent, uri);
    }

    public void ping() {
        URI uri = URI.create(String.format("%s/internal/isAlive", aktørRegisterUrl));

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken());
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));

        HttpEntity httpEntity = new HttpEntity(headers);

        restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
    }

}
