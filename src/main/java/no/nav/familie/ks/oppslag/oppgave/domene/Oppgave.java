package no.nav.familie.ks.oppslag.oppgave.domene;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Oppgave {

    @JsonProperty
    private final String gosysSakId;

    @JsonProperty
    private final String eksisterendeOppgaveId;

    @JsonProperty
    private final String behandlendeEnhetId;

    @JsonProperty
    private final String fnr;

    @JsonProperty
    private final String beskrivelse;

    @JsonProperty
    private final Integer behandlingsfristDager;

    @JsonCreator
    public Oppgave(
            @JsonProperty("gosysSakId") String gosysSakId,
            @JsonProperty("eksisterendeOppgaveId") String eksisterendeOppgaveId,
            @JsonProperty("behandlendeEnhetId") String behandlendeEnhetId,
            @JsonProperty("fnr") String fnr,
            @JsonProperty("beskrivelse") String beskrivelse,
            @JsonProperty("behandlingsfristDager") Integer behandlingsfristDager) {

        this.gosysSakId = gosysSakId;
        this.eksisterendeOppgaveId = eksisterendeOppgaveId;
        this.behandlendeEnhetId = behandlendeEnhetId;
        this.fnr = fnr;
        this.beskrivelse = beskrivelse;
        this.behandlingsfristDager = behandlingsfristDager;
    }

    public String getGosysSakId() {
        return gosysSakId;
    }

    public String getEksisterendeOppgaveId() {
        return eksisterendeOppgaveId;
    }

    public String getBehandlendeEnhetId() {
        return behandlendeEnhetId;
    }

    public String getFnr() {
        return fnr;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public Integer getBehandlingsfristDager() {
        return behandlingsfristDager;
    }
}
