package no.nav.familie.ks.oppslag.aktør;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.oppslag.aktør.internal.AktørIkkeFunnetException;
import no.nav.familie.ks.oppslag.aktør.internal.AktørResponse;
import no.nav.familie.ks.oppslag.felles.MDCOperations;
import no.nav.familie.ks.oppslag.felles.rest.StsRestClient;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
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
import java.util.Optional;

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
    private static final Logger LOG = LoggerFactory.getLogger(AktørregisterClient.class);

    private HttpClient httpClient;
    private StsRestClient stsRestClient;
    private String aktørRegisterUrl;
    private String consumer;
    private ObjectMapper objectMapper;
    private final CacheManager aktørCacheManager;

    AktørregisterClient(@Value("${AKTOERID_URL}") String aktørRegisterUrl,
                        @Value("${CREDENTIAL_USERNAME}") String consumer,
                        @Autowired StsRestClient stsRestClient,
                        @Autowired CacheManager aktørCacheManager) {
        this.aktørRegisterUrl = aktørRegisterUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.consumer = consumer;
        this.stsRestClient = stsRestClient;
        this.objectMapper = new ObjectMapper();
        this.aktørCacheManager = aktørCacheManager;
    }

    public String getAktørId(String personIdent) {
        return Optional.ofNullable(aktørCache().get(personIdent)).orElseGet(() -> {
            String aktørId = hentAktørIdFraRegister(personIdent);
            aktørCache().put(personIdent, aktørId);
            return aktørId;
        });
    }

    private String hentAktørIdFraRegister(String personIdent) {
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
            LOG.info("Response fra aktørreg. Statuskode: {}, Body: {}", response.statusCode(), response.body());

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

    private Cache<String, String> aktørCache() {
        return aktørCacheManager.getCache("aktørIdCache", String.class, String.class);
    }
}
