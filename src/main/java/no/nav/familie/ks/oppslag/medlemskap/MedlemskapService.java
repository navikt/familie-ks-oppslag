package no.nav.familie.ks.oppslag.medlemskap;

import no.nav.familie.ks.oppslag.felles.MDCOperations;
import no.nav.familie.ks.oppslag.felles.rest.StsRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Service
public class MedlemskapService {

    private static final Logger LOG = LoggerFactory.getLogger(MedlemskapService.class);
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_PERSONIDENT = "Nav-Personident";

    private String medl2Url;
    private HttpClient httpClient;
    private StsRestClient stsRestClient;
    private String srvBruker;

    public MedlemskapService(@Value("MEDL2_URL") String url, @Value("CREDENTIAL_USERNAME") String srvBruker, @Autowired StsRestClient stsRestClient) {
        this.medl2Url = url;
        this.srvBruker = srvBruker;
        this.stsRestClient = stsRestClient;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String hentMedlemskapsUnntak(String aktørId) {
        URI uri = URI.create(String.format("%s/medlemskapsunntak", medl2Url));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(ACCEPT, "application/json")
                .header(NAV_PERSONIDENT, aktørId)
                .header(NAV_CONSUMER_ID, srvBruker)
                .header(NAV_CALL_ID, MDCOperations.getCallId())
                .header(AUTHORIZATION, "Bearer " + stsRestClient.getSystemOIDCToken())
                .timeout(Duration.ofSeconds(5))
                .build();

        try {
            LOG.info("Prøver å hente medlemskapsunntak fra MEDL2");
            var httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Feil ved kall til medl2", e);
        }
    }

}
