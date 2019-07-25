package no.nav.familie.ks.oppslag.aktør;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.oppslag.aktør.domene.Aktør;
import no.nav.familie.ks.oppslag.aktør.internal.AktørResponse;
import no.nav.familie.ks.oppslag.aktør.internal.AktørregisterClient;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_OK;

@Service
public class AktørService {

    private AktørregisterClient aktørregisterClient;
    private ObjectMapper objectMapper;
    private final CacheManager aktørCacheManager;

    AktørService(@Autowired AktørregisterClient aktørregisterClient,
                 @Autowired CacheManager aktørCacheManager) {

        this.objectMapper = new ObjectMapper();
        this.aktørCacheManager = aktørCacheManager;
        this.aktørregisterClient = aktørregisterClient;
    }

    public String getAktørId(String personIdent) {
        return Optional.ofNullable(aktørCache().get(personIdent)).orElseGet(() -> {
            String aktørId = hentAktørIdFraRegister(personIdent);
            aktørCache().put(personIdent, aktørId);
            return aktørId;
        });
    }

    private String hentAktørIdFraRegister(String personIdent) {
        HttpResponse<String> response = aktørregisterClient.hentAktørId(personIdent);
        try {
            Aktør aktørResponse = objectMapper.readValue(response.body(), AktørResponse.class).get(personIdent);

            if (response.statusCode() == HTTP_OK && aktørResponse.getFeilmelding() == null) {
                return aktørResponse.getIdenter().get(0).getIdent();
            } else {
                throw new RuntimeException(String.format("Feil ved kall mot Aktørregisteret. Statuskode: %s. Feilmelding: %s",
                        response.statusCode(),
                        aktørResponse.getFeilmelding())
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Klarte ikke deserialisere respons fra Aktørregisteret", e);
        }
    }

    private Cache<String, String> aktørCache() {
        return aktørCacheManager.getCache("aktørIdCache", String.class, String.class);
    }
}
