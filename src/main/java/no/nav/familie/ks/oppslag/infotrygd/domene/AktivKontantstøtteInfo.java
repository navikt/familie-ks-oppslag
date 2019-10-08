package no.nav.familie.ks.oppslag.infotrygd.domene;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AktivKontantstøtteInfo {
    private Boolean harAktivKontantstotte;

    public AktivKontantstøtteInfo() {
    }

    public Boolean getHarAktivKontantstotte() {
        return harAktivKontantstotte;
    }

    public void setHarAktivKontantstotte(Boolean harAktivKontantstotte) {
        this.harAktivKontantstotte = harAktivKontantstotte;
    }
}