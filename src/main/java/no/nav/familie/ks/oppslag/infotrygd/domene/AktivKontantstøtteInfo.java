package no.nav.familie.ks.oppslag.infotrygd.domene;

public class AktivKontantstøtteInfo {
    private Boolean harAktivKontantstotte;

    public AktivKontantstøtteInfo(boolean harAktivKontantstotte) {
        this.harAktivKontantstotte = harAktivKontantstotte;
    }

    public Boolean getHarAktivKontantstotte() {
        return harAktivKontantstotte;
    }

    public void setHarAktivKontantstotte(Boolean harAktivKontantstotte) {
        this.harAktivKontantstotte = harAktivKontantstotte;
    }
}