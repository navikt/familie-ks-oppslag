package no.nav.familie.ks.oppslag.dokarkiv.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class ArkiverDokumentRequest {

    @NotBlank
    private String fnr;

    private boolean forsøkFerdigstill;

    private String saksnummer;

    @NotEmpty
    private List<Dokument> dokumenter;

    public ArkiverDokumentRequest() {
    }

    public ArkiverDokumentRequest(String fnr, boolean forsøkFerdigstill, String saksnummer, List<Dokument> dokumenter) {
        this.fnr = fnr;
        this.forsøkFerdigstill = forsøkFerdigstill;
        this.saksnummer = saksnummer;
        this.dokumenter = dokumenter;
    }

    public String getFnr() {
        return fnr;
    }

    public boolean isForsøkFerdigstill() {
        return forsøkFerdigstill;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public List<Dokument> getDokumenter() {
        return dokumenter;
    }
}
