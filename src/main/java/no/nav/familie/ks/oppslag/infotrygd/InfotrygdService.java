package no.nav.familie.ks.oppslag.infotrygd;

import no.nav.familie.http.azure.AccessTokenClient;
import no.nav.familie.ks.oppslag.infotrygd.domene.AktivKontantstøtteInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InfotrygdService {
    private RestTemplate restTemplate = new RestTemplate();
    private AccessTokenClient accessTokenClient;
    private String scope;
    private String infotrygdURL;

    @Autowired
    public InfotrygdService(AccessTokenClient accessTokenClient, 
                            @Value("${INFOTRYGD_KS_SCOPE}") String scope, 
                            @Value("${INFOTRYGD_URL}") String infotrygdURL) {
        this.accessTokenClient = accessTokenClient;
        this.scope = scope;
        this.infotrygdURL = infotrygdURL;
    }

    AktivKontantstøtteInfo hentAktivKontantstøtteFor(String fnr) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenClient.getAccessToken(scope).access_token);
        headers.add("Accept", "application/json");
        headers.add("fnr", fnr);
        var entity = new HttpEntity(headers);

        var response = restTemplate.exchange(String.format("%s/v1/harBarnAktivKontantstotte", infotrygdURL),
                                        HttpMethod.GET,
                                        entity,
                                        AktivKontantstøtteInfo.class);

        return response.getBody();
    }
}
