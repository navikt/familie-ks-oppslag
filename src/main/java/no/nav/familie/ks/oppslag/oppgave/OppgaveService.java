package no.nav.familie.ks.oppslag.oppgave;


import com.fasterxml.jackson.core.JsonProcessingException;
import no.nav.familie.ks.kontrakter.oppgave.Oppgave;
import no.nav.familie.ks.oppslag.oppgave.internal.OppgaveClient;
import no.nav.oppgave.v1.OppgaveJsonDto;
import no.nav.sbl.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.annotation.ApplicationScope;

@Service
@ApplicationScope
public class OppgaveService {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);
    private final OppgaveClient oppgaveClient;

    @Autowired
    public OppgaveService(OppgaveClient oppgaveClient) {
        this.oppgaveClient = oppgaveClient;
    }

    ResponseEntity oppdaterOppgave(Oppgave request) {
        try {
            OppgaveJsonDto oppgaveJsonDto;
            if (StringUtils.nullOrEmpty(request.getEksisterendeOppgaveId())) {
                oppgaveJsonDto = oppgaveClient.finnOppgave(request);
            } else {
                oppgaveJsonDto = oppgaveClient.finnOppgave(request.getEksisterendeOppgaveId());
            }
            if (oppgaveJsonDto == null) {
                LOG.error("Fant ingen oppgave tilknyttet sak med journalpostId " + request.getJournalpostId());
                return ResponseEntity.noContent().build();
            }
            oppgaveClient.oppdaterOppgave(oppgaveJsonDto, request.getBeskrivelse());
            return ResponseEntity.ok(oppgaveJsonDto.getId());
        } catch (JsonProcessingException e) {
            LOG.info("Mapping av OppgaveJsonDto til String feilet.");
            return ResponseEntity.noContent().header("message", e.getMessage()).build();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOG.info("oppdaterOppgave kastet RestClientResponseException");
            return ResponseEntity.noContent().header("message", e.getMessage()).build();
        } catch (Exception e) {
            throw new RuntimeException("Ukjent feil ved kall mot oppgave/api/v1", e);
        }
    }
}
