package no.nav.familie.ks.oppslag.aktør;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.oppslag.aktør.internal.AktørIkkeFunnetException;
import no.nav.familie.ks.oppslag.aktør.internal.AktørResponse;
import no.nav.familie.ks.oppslag.felles.MDCOperations;
import no.nav.familie.ks.oppslag.felles.rest.StsRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Service
public class AktørregisterClient {

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_PERSONIDENTER = "Nav-Personidenter";
    private static final String AKTOERID_IDENTGRUPPE = "AktoerId";

    private HttpClient httpClient;
    private StsRestClient stsRestClient;
    private String aktørRegisterUrl;
    private String consumer;
    private ObjectMapper objectMapper;

    AktørregisterClient(@Value("${AKTOERID_URL}") String aktørRegisterUrl,
                        @Value("${CREDENTIAL_USERNAME}") String consumer,
                        @Autowired StsRestClient stsRestClient) {
        this.aktørRegisterUrl = aktørRegisterUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.consumer = consumer;
        this.stsRestClient = stsRestClient;
        this.objectMapper = new ObjectMapper();
    }

    public String getAktoerId(String personIdent) {
        URI uri = URI.create(String.format("%s/identer?gjeldende=true&identgruppe=%s", aktørRegisterUrl, AKTOERID_IDENTGRUPPE));
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
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HTTP_OK) {
                AktørResponse aktørResponse = objectMapper.readValue(response.body(), AktørResponse.class);
                return aktørResponse.get(personIdent).getIdenter().get(0).getIdent();
            } else if (response.statusCode() == HTTP_NOT_FOUND) {
                throw new AktørIkkeFunnetException(personIdent);
            } else {
                throw new RuntimeException(String.format("Feil ved kall mot Aktørregisteret. Statuskode: %s. Feilmelding: %s", response.statusCode(), response.body()));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Feil ved kall mot Aktørregisteret", e);
        }
    }
}
