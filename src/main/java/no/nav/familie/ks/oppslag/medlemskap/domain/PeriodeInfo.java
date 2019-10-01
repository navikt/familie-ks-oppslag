package no.nav.familie.ks.oppslag.medlemskap.domain;

import java.time.LocalDate;

public class PeriodeInfo {

    private PeriodeStatus periodeStatus;
    private PeriodeStatusÅrsak periodeStatusÅrsak; // Kun hvis periodeStatus uavklart eller avvist
    private LocalDate fom;
    private LocalDate tom;
    private boolean erMedlem; //Gjelder unntaket medlem i Folketrygden
    private String grunnlag; //Hva som gir grunnlag for medlemskapsunntaket
    private String dekning; //Hva som gir dekning for medlemskapsunntaket

    public PeriodeStatus getPeriodeStatus() {
        return periodeStatus;
    }

    public PeriodeStatusÅrsak getPeriodeStatusÅrsak() {
        return periodeStatusÅrsak;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public boolean isErMedlem() {
        return erMedlem;
    }

    public String getGrunnlag() {
        return grunnlag;
    }

    public String getDekning() {
        return dekning;
    }

    public static class Builder {

        private PeriodeInfo periodeInfo;

        public Builder() {
            periodeInfo = new PeriodeInfo();
        }

        public Builder medPeriodeStatus(PeriodeStatus periodeStatus) {
            periodeInfo.periodeStatus = periodeStatus;
            return this;
        }

        public Builder medPeriodeStatusÅrsak(PeriodeStatusÅrsak årsak) {
            periodeInfo.periodeStatusÅrsak = årsak;
            return this;
        }

        public Builder medFom(LocalDate fom) {
            periodeInfo.fom = fom;
            return this;
        }

        public Builder medTom(LocalDate tom) {
            periodeInfo.tom = tom;
            return this;
        }

        public Builder medErMedlem(boolean erMedlem) {
            periodeInfo.erMedlem = erMedlem;
            return this;
        }

        public Builder medGrunnlag(String grunnlag) {
            periodeInfo.grunnlag = grunnlag;
            return this;
        }

        public Builder medDekning(String dekning) {
            periodeInfo.dekning = dekning;
            return this;
        }

        public PeriodeInfo build() {
            return periodeInfo;
        }
    }
}
