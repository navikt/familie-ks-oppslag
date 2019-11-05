package no.nav.familie.ks.oppslag.azure;

import no.nav.familie.ks.oppslag.azure.domene.Gruppe;
import no.nav.familie.ks.oppslag.azure.domene.Person;
import no.nav.familie.ks.oppslag.config.BaseService;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AzureGraphService extends BaseService {

    private static final String OAUTH2_CLIENT_CONFIG_KEY = "aad-graph-clientcredentials";
    private String aadGrapURI;

    @Autowired
    public AzureGraphService(RestTemplateBuilder restTemplateBuilderMedProxy,
                             ClientConfigurationProperties clientConfigurationProperties,
                             OAuth2AccessTokenService oAuth2AccessTokenService,
                             @Value("${AAD_GRAPH_API_URI}") String URI) {
        super(OAUTH2_CLIENT_CONFIG_KEY, restTemplateBuilderMedProxy, clientConfigurationProperties, oAuth2AccessTokenService);

        this.aadGrapURI = URI;
    }

    public Person getPerson() {

        var headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        var entity = new HttpEntity(headers);

        var response = restTemplate.exchange(String.format("%sme?$select=displayName,onPremisesSamAccountName,userPrincipalName,onPremisesSamAccountName", aadGrapURI), HttpMethod.GET, entity, Person.class);

        Person person = response.getBody();
        var gruppeResponse = restTemplate.exchange(String.format("%sme/memberOf?$select=onPremisesSamAccountName,displayName,id", aadGrapURI), HttpMethod.GET, entity, Gruppe[].class);
        if (person != null && gruppeResponse.getBody() != null) {
            person.setGrupper(List.of(gruppeResponse.getBody()));
        }

        return person;
    }

}
