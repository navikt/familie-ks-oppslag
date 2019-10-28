package no.nav.familie.ks.oppslag.oppgave.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.kontrakter.oppgave.Oppgave;
import no.nav.familie.ks.oppslag.oppgave.OppgaveIkkeFunnetException;
import no.nav.familie.log.mdc.MDCConstants;
import no.nav.oppgave.v1.FinnOppgaveResponseDto;
import no.nav.oppgave.v1.OppgaveJsonDto;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

public class OppgaveClient {

    private static final String TEMA = "KON";
    private static final String OPPGAVE_TYPE = "BEH_SAK";
    private static final String X_CORRELATION_ID = "X-Correlation-ID";

    private final URI oppgaveUri;
    private final RestTemplate restTemplate;
    private final StsRestClient stsRestClient;
    private final ObjectMapper objectMapper;

    public OppgaveClient(String url,
                         RestTemplate restTemplate,
                         StsRestClient stsRestClient,
                         ObjectMapper objectMapper) {
        this.oppgaveUri = URI.create(url + "/oppgaver");
        this.restTemplate = restTemplate;
        this.stsRestClient = stsRestClient;
        this.objectMapper = objectMapper;
    }

    public OppgaveJsonDto finnOppgave(Oppgave request) {
        URI requestUrl = lagRequestUrlMed(oppgaveUri, request.getAktorId(), request.getJournalpostId());
        return requestOppgaveJson(requestUrl);
    }

    public OppgaveJsonDto finnOppgave(String oppgaveId) {
        URI requestUrl = URI.create(oppgaveUri + "/" + oppgaveId);
        return requestOppgaveJson(requestUrl);
    }

    public void oppdaterOppgave(OppgaveJsonDto dto, String beskrivelse) {
        dto.setBeskrivelse(dto.getBeskrivelse() + beskrivelse);
        try {
            putRequest(URI.create(oppgaveUri + "/" + dto.getId()), objectMapper.writeValueAsString(dto), String.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Mapping av OppgaveJsonDto til String feilet.", e);
        }
    }

    private URI lagRequestUrlMed(URI oppgaveUri, String aktoerId, String journalpostId) {
        return URI.create(oppgaveUri + String.format("?aktoerId=%s&tema=%s&oppgavetype=%s&journalpostId=%s", aktoerId, TEMA, OPPGAVE_TYPE, journalpostId));
    }

    private OppgaveJsonDto requestOppgaveJson(URI requestUrl) {
        var response = getRequest(requestUrl, FinnOppgaveResponseDto.class);
        if (Objects.requireNonNull(response.getBody()).getOppgaver().isEmpty()) {
            throw new OppgaveIkkeFunnetException("Mislykket finnOppgave request med url: " + requestUrl.getPath());
        }
        return response.getBody().getOppgaver().get(0);
    }

    private <T> ResponseEntity<T> getRequest(URI uri, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(stsRestClient.getSystemOIDCToken());
        headers.add(X_CORRELATION_ID, MDC.get(MDCConstants.MDC_CALL_ID));

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), responseType);
    }

    private <T> void putRequest(URI uri, String requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(stsRestClient.getSystemOIDCToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(X_CORRELATION_ID, MDC.get(MDCConstants.MDC_CALL_ID));

        restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(requestBody, headers), responseType);
    }
}
