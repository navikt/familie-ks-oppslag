package no.nav.familie.ks.oppslag.aktør;

import no.nav.familie.ks.oppslag.felles.rest.StsRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

public class AktørregisterClient {

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_PERSONIDENTER = "Nav-Personidenter";
    private static final String AKTOERID_IDENTGRUPPE = "AktoerId";

    private HttpClient httpClient;
    private StsRestClient stsRestClient;
    private String aktørRegisterUrl;
    private String consumer;

    AktørregisterClient(@Value("${AKTOERID_URL}") String aktørRegisterUrl,
                        @Value("${CREDENTIAL_USERNAME}") String consumer,
                        @Autowired StsRestClient stsRestClient) {
        this.aktørRegisterUrl = aktørRegisterUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.consumer = consumer;
        this.stsRestClient = stsRestClient;
    }

    public String getAktoerId(String personIdent) {
        URI uri = URI.create(String.format("%s/identer?gjeldende=true&identgruppe=%s", aktørRegisterUrl, AKTOERID_IDENTGRUPPE));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(ACCEPT, "application/json")
                .header(NAV_PERSONIDENTER, personIdent)
                .header(NAV_CONSUMER_ID, consumer)
                .header(AUTHORIZATION, "Bearer " + stsRestClient.getSystemOIDCToken())
                .timeout(Duration.ofSeconds(5))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HTTP_OK) {
                return response.body();
            } else if (response.statusCode() == HTTP_NOT_FOUND) {
                throw new RuntimeException(String.format("Ident %s finnes ikke i Aktørregisteret", personIdent));
            } else {
                throw new RuntimeException(String.format("Feil ved kall mot Aktørregisteret. Statuskode %s", response.statusCode()));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Feil ved kall mot Aktørregisteret", e);
        }
    }
}
