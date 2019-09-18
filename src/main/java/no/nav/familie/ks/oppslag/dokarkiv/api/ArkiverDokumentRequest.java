package no.nav.familie.ks.oppslag.dokarkiv.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class ArkiverDokumentRequest {

    @NotBlank
    private String fnr;
    @NotBlank
    private String navn;
    private DokumentType dokumentType;
    private boolean forsøkFerdigstill;

    @NotEmpty
    private List<Dokument> dokumenter;

    public ArkiverDokumentRequest(String fnr, String navn, DokumentType dokumentType, boolean forsøkFerdigstill, List<Dokument> dokumenter) {
        this.fnr = fnr;
        this.navn = navn;
        this.dokumentType = dokumentType;
        this.forsøkFerdigstill = forsøkFerdigstill;
        this.dokumenter = dokumenter;
    }

    public String getFnr() {
        return fnr;
    }

    public String getNavn() {
        return navn;
    }

    public DokumentType getDokumentType() {
        return dokumentType;
    }

    public boolean isForsøkFerdigstill() {
        return forsøkFerdigstill;
    }

    public List<Dokument> getDokumenter() {
        return dokumenter;
    }

    @Override
    public String toString() {
        return "ArkiverDokumentRequest{" +
                "fnr='" + fnr + '\'' +
                ", navn='" + navn + '\'' +
                ", dokumentType='" + dokumentType + '\'' +
                ", forsøkFerdigstill=" + forsøkFerdigstill +
                ", dokumenter=" + dokumenter +
                '}';
    }


}
