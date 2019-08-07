package no.nav.familie.ks.oppslag.personopplysning.domene;

import no.nav.familie.ks.oppslag.personopplysning.domene.adresse.Adresseinfo;
import no.nav.familie.ks.oppslag.personopplysning.domene.adresse.TpsAdresseOversetter;
import no.nav.familie.ks.oppslag.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.familie.ks.oppslag.personopplysning.domene.relasjon.RelasjonsRolleType;
import no.nav.familie.ks.oppslag.personopplysning.domene.relasjon.SivilstandType;
import no.nav.familie.ks.oppslag.personopplysning.domene.status.PersonstatusPeriode;
import no.nav.familie.ks.oppslag.personopplysning.domene.status.PersonstatusType;
import no.nav.familie.ks.oppslag.personopplysning.domene.tilhørighet.Landkode;


import no.nav.familie.ks.oppslag.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.familie.ks.oppslag.felles.ws.DateUtil;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class TpsOversetter {

    private static final Logger log = LoggerFactory.getLogger(TpsOversetter.class);

    private TpsAdresseOversetter tpsAdresseOversetter;

    public TpsOversetter(TpsAdresseOversetter tpsAdresseOversetter) {
        this.tpsAdresseOversetter = tpsAdresseOversetter;
    }

    private Landkode utledLandkode(Statsborgerskap statsborgerskap) {
        Landkode landkode = Landkode.UDEFINERT;
        if (Optional.ofNullable(statsborgerskap).isPresent()) {
            landkode = new Landkode(statsborgerskap.getLand().getKodeRef());
        }
        return landkode;
    }

    /*public Personinfo tilBrukerInfo(AktørId aktørId, Bruker bruker) { // NOSONAR - ingen forbedring å forkorte metoden her
        String navn = bruker.getPersonnavn().getSammensattNavn();
        String adresse = tpsAdresseOversetter.finnAdresseFor(bruker);
        String adresseLandkode = tpsAdresseOversetter.finnAdresseLandkodeFor(bruker);
        String utlandsadresse = tpsAdresseOversetter.finnUtlandsadresseFor(bruker);

        LocalDate fødselsdato = finnFødselsdato(bruker);
        LocalDate dødsdato = finnDødsdato(bruker);

        Aktoer aktoer = bruker.getAktoer();
        PersonIdent pi = (PersonIdent) aktoer;
        String ident = pi.getIdent().getIdent();
        PersonstatusType personstatus = tilPersonstatusType(bruker.getPersonstatus());
        Set<Familierelasjon> familierelasjoner = bruker.getHarFraRolleI().stream()
                .map(this::tilRelasjon)
                .collect(toSet());

        Landkode landkode = utledLandkode(bruker.getStatsborgerskap());

        String diskresjonskode = bruker.getDiskresjonskode() == null ? null : bruker.getDiskresjonskode().getValue();
        String geografiskTilknytning = bruker.getGeografiskTilknytning() != null ? bruker.getGeografiskTilknytning().getGeografiskTilknytning() : null;

        List<Adresseinfo> adresseinfoList = tpsAdresseOversetter.lagListeMedAdresseInfo(bruker);

        return new Personinfo.Builder()
                .medAktørId(aktørId)
                .medPersonIdent(no.nav.familie.ks.oppslag.personopplysning.domene.PersonIdent.fra(ident))
                .medNavn(navn)
                .medAdresse(adresse)
                .medAdresseLandkode(adresseLandkode)
                .medFødselsdato(fødselsdato)
                .medDødsdato(dødsdato)
                .medPersonstatusType(personstatus)
                .medStatsborgerskap(landkode)
                .medFamilierelasjon(familierelasjoner)
                .medUtlandsadresse(utlandsadresse)
                .medGegrafiskTilknytning(geografiskTilknytning)
                .medDiskresjonsKode(diskresjonskode)
                .medAdresseInfoList(adresseinfoList)
                .medLandkode(landkode)
                .build();
    }*/

    public Personinfo tilPersoninfo(AktørId aktørId, Person person) {

        Set<Familierelasjon> familierelasjoner = person.getHarFraRolleI().stream()
                .map(this::tilRelasjon)
                .collect(toSet());

        String diskresjonskode = person.getDiskresjonskode() == null ? null : person.getDiskresjonskode().getValue();

        return new Personinfo.Builder()
                .medAktørId(aktørId)
                .medFamilierelasjon(familierelasjoner)
                .medAdresse(tpsAdresseOversetter.finnAdresseFor(person))
                .medPersonstatusType(tilPersonstatusType(person.getPersonstatus()))
                .medSivilstandType(tilSivilstandType(person.getSivilstand()))
                .medStatsborgerskap(utledLandkode(person.getStatsborgerskap()))
                .medFødselsdato(finnFødselsdato(person))
                .medDødsdato(finnDødsdato(person))
                .medDiskresjonsKode(diskresjonskode)
                .medNavn(person.getPersonnavn().getSammensattNavn())
                .build();
    }

    public PersonhistorikkInfo tilPersonhistorikkInfo(String aktørId, HentPersonhistorikkResponse response) {

        PersonhistorikkInfo.Builder builder = PersonhistorikkInfo
                .builder()
                .medAktørId(aktørId);

        konverterPersonstatusPerioder(response, builder);

        konverterStatsborgerskapPerioder(response, builder);

        tpsAdresseOversetter.konverterBostedadressePerioder(response, builder);
        tpsAdresseOversetter.konverterPostadressePerioder(response, builder);
        tpsAdresseOversetter.konverterMidlertidigAdressePerioder(response, builder);

        return builder.build();
    }

    private void konverterPersonstatusPerioder(HentPersonhistorikkResponse response, PersonhistorikkInfo.Builder builder) {
        Optional.ofNullable(response.getPersonstatusListe()).ifPresent(list -> {
            list.forEach(e -> {
                Personstatus personstatus = new Personstatus();
                personstatus.setPersonstatus(e.getPersonstatus());
                PersonstatusType personstatusType = tilPersonstatusType(personstatus);

                Periode gyldighetsperiode = Periode.innenfor(
                        DateUtil.convertToLocalDate(e.getPeriode().getFom()),
                        DateUtil.convertToLocalDate(e.getPeriode().getTom()));

                PersonstatusPeriode periode = new PersonstatusPeriode(gyldighetsperiode, personstatusType);
                builder.leggTil(periode);
            });
        });
    }

    private void konverterStatsborgerskapPerioder(HentPersonhistorikkResponse response, PersonhistorikkInfo.Builder builder) {
        Optional.ofNullable(response.getStatsborgerskapListe()).ifPresent(list -> {
            list.forEach(e -> {
                Periode periode = Periode.innenfor(
                        DateUtil.convertToLocalDate(e.getPeriode().getFom()),
                        DateUtil.convertToLocalDate(e.getPeriode().getTom()));

                Landkode landkoder = new Landkode(e.getStatsborgerskap().getLand().getValue());
                StatsborgerskapPeriode element = new StatsborgerskapPeriode(periode, landkoder);
                builder.leggTil(element);
            });
        });
    }

    private PersonstatusType tilPersonstatusType(Personstatus personstatus) {
        return PersonstatusType.valueOf(personstatus.getPersonstatus().getValue());
    }

    private SivilstandType tilSivilstandType(Sivilstand sivilstand) {
        return SivilstandType.valueOf(sivilstand.getSivilstand().getValue());
    }

    private LocalDate finnDødsdato(Person person) {
        LocalDate dødsdato = null;
        Doedsdato dødsdatoJaxb = person.getDoedsdato();
        if (dødsdatoJaxb != null) {
            dødsdato = DateUtil.convertToLocalDate(dødsdatoJaxb.getDoedsdato());
        }
        return dødsdato;
    }

    private LocalDate finnFødselsdato(Person person) {
        LocalDate fødselsdato = null;
        Foedselsdato fødselsdatoJaxb = person.getFoedselsdato();
        if (fødselsdatoJaxb != null) {
            fødselsdato = DateUtil.convertToLocalDate(fødselsdatoJaxb.getFoedselsdato());
        }
        return fødselsdato;
    }

    private Familierelasjon tilRelasjon(no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon familierelasjon) {
        String rollekode = familierelasjon.getTilRolle().getValue();
        RelasjonsRolleType relasjonsrolle = RelasjonsRolleType.valueOf(rollekode);
        AktoerId aktoer = (AktoerId) familierelasjon.getTilPerson().getAktoer();
        AktørId aktørId = aktoer == null ? null : new AktørId(aktoer.getAktoerId());
        Boolean harSammeBosted = familierelasjon.isHarSammeBosted();

        return new Familierelasjon(aktørId, relasjonsrolle,
                tilLocalDate(familierelasjon.getTilPerson().getFoedselsdato()), harSammeBosted);
    }

    private LocalDate tilLocalDate(Foedselsdato fødselsdatoJaxb) {
        if (fødselsdatoJaxb != null) {
            return DateUtil.convertToLocalDate(fødselsdatoJaxb.getFoedselsdato());
        }
        return null;
    }
}
