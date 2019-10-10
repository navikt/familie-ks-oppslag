package no.nav.familie.ks.oppslag.personopplysning.domene.relasjon;

import no.nav.familie.ks.oppslag.personopplysning.domene.Ident;
import no.nav.familie.ks.oppslag.personopplysning.domene.IdentType;

import java.time.LocalDate;
import java.util.Map;

public class Familierelasjon {
    private Map<IdentType, Ident> ident;
    private RelasjonsRolleType relasjonsrolle;
    private LocalDate fødselsdato;
    private Boolean harSammeBosted;

    public Familierelasjon(Map<IdentType, Ident> ident, RelasjonsRolleType relasjonsrolle, LocalDate fødselsdato,
                           Boolean harSammeBosted) {
        this.ident = ident;
        this.relasjonsrolle = relasjonsrolle;
        this.fødselsdato = fødselsdato;
        this.harSammeBosted = harSammeBosted;
    }

    public Map<IdentType, Ident> getIdent() {
        return ident;
    }

    public RelasjonsRolleType getRelasjonsrolle() {
        return relasjonsrolle;
    }

    public Boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "<relasjon=" + relasjonsrolle
                + ", fødselsdato=" + fødselsdato
                + ">";
    }
}
