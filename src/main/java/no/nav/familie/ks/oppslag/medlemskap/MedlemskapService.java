package no.nav.familie.ks.oppslag.medlemskap;

import no.nav.familie.http.client.HttpRequestUtil;
import no.nav.familie.http.client.NavHttpHeaders;
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

import static javax.ws.rs.core.HttpHeaders.ACCEPT;

@Service
public class MedlemskapService {

    private static final Logger LOG = LoggerFactory.getLogger(MedlemskapService.class);

    private String medl2Url;
    private HttpClient httpClient;
    private StsRestClient stsRestClient;
    private String srvBruker;

    public MedlemskapService(@Value("${MEDL2_URL}") String url, @Value("${CREDENTIAL_USERNAME}") String srvBruker, @Autowired StsRestClient stsRestClient) {
        this.medl2Url = url;
        this.srvBruker = srvBruker;
        this.stsRestClient = stsRestClient;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String hentMedlemskapsUnntak(String aktørId) {
        URI uri = URI.create(String.format("%s/medlemskapsunntak", medl2Url));
        HttpRequest request = HttpRequestUtil.createRequest(stsRestClient.getSystemOIDCToken())
                .uri(uri)
                .header(ACCEPT, "application/json")
                .header(NavHttpHeaders.NAV_PERSONIDENT.asString(), aktørId)
                .header(NavHttpHeaders.NAV_CONSUMER_ID.asString(), srvBruker)
                .build();

        try {
            LOG.info("Prøver å hente medlemskapsunntak fra MEDL2");
            var httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            LOG.info("Response status: {}", httpResponse.statusCode());
            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Feil ved kall til medl2", e);
        }
    }

}
