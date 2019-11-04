package no.nav.familie.ks.oppslag.azure;

import no.nav.familie.http.azure.AccessTokenClient;
import no.nav.familie.ks.oppslag.azure.domene.Gruppe;
import no.nav.familie.ks.oppslag.azure.domene.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class AzureGraphService {

    private RestTemplate restTemplate = new RestTemplate();
    private AccessTokenClient accessTokenClient;
    private String scope;
    private String aadGrapURI;

    @Autowired
    public AzureGraphService(AccessTokenClient accessTokenClient,
                             @Value("${AAD_SCOPE}") String scope,
                             @Value("${AAD_GRAPH_API_URI}") String URI) {
        this.accessTokenClient = accessTokenClient;
        this.scope = scope;
        this.aadGrapURI = URI;
    }

    public Person getPerson() {

        var headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenClient.getAccessToken(scope).access_token);
        headers.add("Accept", "application/json");
        var entity = new HttpEntity(headers);

        var response = restTemplate.exchange(String.format("%me?$select=displayName,onPremisesSamAccountName,userPrincipalName,onPremisesSamAccountName", aadGrapURI), HttpMethod.GET, entity, Person.class);

        Person person = response.getBody();
        var gruppeResponse = restTemplate.exchange(String.format("%sme/memberOf?$select=onPremisesSamAccountName,displayName,id", aadGrapURI), HttpMethod.GET, entity, Gruppe[].class);
        person.setGrupper(List.of(gruppeResponse.getBody()));

        return person;
    }

}
