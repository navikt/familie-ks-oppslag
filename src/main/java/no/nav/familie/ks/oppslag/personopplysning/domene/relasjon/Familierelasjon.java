package no.nav.familie.ks.oppslag.personopplysning.domene.relasjon;

import no.nav.familie.ks.oppslag.personopplysning.domene.AktørId;

import java.time.LocalDate;

public class Familierelasjon {
    private AktørId aktørId;
    private RelasjonsRolleType relasjonsrolle;
    private LocalDate fødselsdato;
    private String adresse;
    private Boolean harSammeBosted;

    public Familierelasjon(AktørId aktørId, RelasjonsRolleType relasjonsrolle, LocalDate fødselsdato,
                           String adresse, Boolean harSammeBosted) {
        this.aktørId = aktørId;
        this.relasjonsrolle = relasjonsrolle;
        this.fødselsdato = fødselsdato;
        this.adresse = adresse;
        this.harSammeBosted = harSammeBosted;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public RelasjonsRolleType getRelasjonsrolle() {
        return relasjonsrolle;
    }

    public String getAdresse() {
        return adresse;
    }

    public Boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "<relasjon=" + relasjonsrolle  //$NON-NLS-1$
                + ", fødselsdato=" + fødselsdato //$NON-NLS-1$
                + ">"; //$NON-NLS-1$
    }
}
