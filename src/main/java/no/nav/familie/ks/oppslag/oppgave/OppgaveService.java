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
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.annotation.ApplicationScope;

import static org.springframework.http.HttpStatus.*;

@Service
@ApplicationScope
public class OppgaveService {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);
    private final OppgaveClient oppgaveClient;

    @Autowired
    public OppgaveService(OppgaveClient oppgaveClient) {
        this.oppgaveClient = oppgaveClient;
    }

    ResponseEntity opprettEllerOppdaterOppgave(Oppgave request) {
        OppgaveJsonDto oppgaveJsonDto;
        try {
            if (StringUtils.nullOrEmpty(request.getEksisterendeOppgaveId())) {
                oppgaveJsonDto = oppgaveClient.finnOppgave(request);
            } else {
                oppgaveJsonDto = oppgaveClient.finnOppgave(request.getEksisterendeOppgaveId());
            }
            oppgaveClient.oppdaterOppgave(oppgaveJsonDto, request.getBeskrivelse());
        } catch (JsonProcessingException e) {
            LOG.info("Mapping av OppgaveJsonDto til String feilet.");
            return ResponseEntity.status(EXPECTATION_FAILED).build();
        }

        return ResponseEntity.ok(oppgaveJsonDto.getId());
    }
}
