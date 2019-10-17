package no.nav.familie.ks.oppslag.oppgave.internal;

import no.nav.familie.ks.kontrakter.oppgave.Oppgave;
import no.nav.familie.ks.kontrakter.oppgave.OppgaveKt;
import no.nav.familie.ks.oppslag.felles.ws.DateUtil;
import no.nav.familie.ks.oppslag.oppgave.OppgaveService;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.BehandleOppgaveV1;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOppgaveIkkeFunnetException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSOptimistiskLasingException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.WSSikkerhetsbegrensningException;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.meldinger.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.binding.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.FinnOppgaveListeFilter;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.FinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.FinnOppgaveListeSok;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.soap.SoapFaultException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class OppgaveConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveConsumer.class);

    private static final String FAGOMRÅDE_KODE = "KON";
    private final String OPPGAVETYPE_KODE = "BEH_SAK_KON";
    private final String PRIORITET_KODE = "NORM_KON";
    private static final int DEFAULT_OPPGAVEFRIST_DAGER = 2;

    private BehandleOppgaveV1 behandleOppgavePort;
    private OppgaveV3 finnOppgavePort;

    public OppgaveConsumer(BehandleOppgaveV1 behandleOppgavePort, OppgaveV3 oppgavePort) {
        this.behandleOppgavePort = behandleOppgavePort;
        this.finnOppgavePort = oppgavePort;
    }

    public WSOpprettOppgaveResponse opprettOppgave(Oppgave request) throws WSSikkerhetsbegrensningException, SoapFaultException {
        return behandleOppgavePort.opprettOppgave(tilWSOpprett(request));
    }

    public Boolean oppdaterOppgave(Oppgave request) throws
            WSSikkerhetsbegrensningException, WSOppgaveIkkeFunnetException, WSOptimistiskLasingException, SoapFaultException {
        LOG.info(tilWSLagre(request).toString());

        behandleOppgavePort.lagreOppgave(tilWSLagre(request));
        return true;
    }

    private String finnBehandleSakOppgave(Oppgave request) {
        if (Objects.requireNonNull(request.getEksisterendeOppgaveId()).isEmpty()) {
            LOG.info("Sender FinnOppgaveListeRequest for Oppgave: " + OppgaveKt.toJson(request));
            LOG.info("Sender FinnOppgaveListeRequest for wsfilter: " + tilWSOppgaveListe(request).toString());
            return finnOppgavePort.finnOppgaveListe(tilWSOppgaveListe(request)).getOppgaveListe().get(0).getOppgaveId();
        }
        return request.getEksisterendeOppgaveId();
    }

    private WSOpprettOppgaveRequest tilWSOpprett(Oppgave request) {
        WSOppgave oppgave = new WSOppgave()
                .withSaksnummer(request.getGosysSakId())
                .withAnsvarligEnhetId(request.getBehandlendeEnhetId())
                .withFagomradeKode(FAGOMRÅDE_KODE)
                .withGjelderBruker(new WSAktor().withIdent(request.getFnr()))
                .withAktivFra(DateUtil.convertToXMLGregorianCalendar(iDag().atStartOfDay()))
                .withAktivTil(DateUtil.convertToXMLGregorianCalendar(helgeJustertFrist(iDag().plusDays(avklarFrist(request)))))
                .withOppgavetypeKode(OPPGAVETYPE_KODE)
                .withPrioritetKode(PRIORITET_KODE)
                .withBeskrivelse(request.getBeskrivelse())
                .withLest(false);

        return new WSOpprettOppgaveRequest()
                .withOpprettetAvEnhetId(Integer.parseInt(request.getBehandlendeEnhetId()))
                .withWsOppgave(oppgave);
    }

    private WSLagreOppgaveRequest tilWSLagre(Oppgave request) {
        String OppgaveId = finnBehandleSakOppgave(request);
        WSLagreOppgave oppgave = new WSLagreOppgave()
                .withOppgaveId(Integer.parseInt(OppgaveId))
                .withSaksnummer(request.getGosysSakId())
                .withBeskrivelse(request.getBeskrivelse());
        LOG.info("Sender WSLagreOppgaveRequest for Oppgave med ID: " + OppgaveId + "\n\nOppgaveJson: " + OppgaveKt.toJson(request));
        return new WSLagreOppgaveRequest()
                .withEndretAvEnhetId(Integer.parseInt(request.getBehandlendeEnhetId()))
                .withWsLagreOppgave(oppgave);
    }

    private FinnOppgaveListeRequest tilWSOppgaveListe(Oppgave request) {
        FinnOppgaveListeRequest oppgaveListeRequest = new FinnOppgaveListeRequest();
        FinnOppgaveListeSok oppgaveListeSok = new FinnOppgaveListeSok();
        FinnOppgaveListeFilter filter = new FinnOppgaveListeFilter();

        filter.getOppgavetypeKodeListe().add(OPPGAVETYPE_KODE);
        filter.setOpprettetEnhetId("4820");
        filter.setAnsvarligEnhetNavn("4820");
        oppgaveListeRequest.setFilter(filter);
        oppgaveListeRequest.setSok(oppgaveListeSok);
        oppgaveListeSok.setBrukerId(request.getFnr());
        oppgaveListeSok.setSakId(request.getGosysSakId());

        return oppgaveListeRequest;
    }

    private LocalDate iDag() {
        return LocalDate.now(ZoneId.systemDefault());
    }

    private int avklarFrist(Oppgave request) {
        return request.getBehandlingsfristDager() > 0 ? request.getBehandlingsfristDager() : DEFAULT_OPPGAVEFRIST_DAGER;
    }

    // Sett frist til mandag hvis fristen er i helgen.
    private LocalDateTime helgeJustertFrist(LocalDate dato) {
        if (dato.getDayOfWeek().getValue() > DayOfWeek.FRIDAY.getValue()) {
            return dato.plusDays(1L + DayOfWeek.SUNDAY.getValue() - dato.getDayOfWeek().getValue()).atStartOfDay();
        }
        return dato.atStartOfDay();
    }
}
