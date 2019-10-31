package no.nav.familie.ks.oppslag.personopplysning;

import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.oppslag.felles.ws.DateUtil;
import no.nav.familie.ks.oppslag.personopplysning.internal.PersonConsumer;
import no.nav.tjeneste.virksomhet.person.v3.binding.*;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Configuration
public class PersonopplysningerTestConfig {

    private static final LocalDate TOM = LocalDate.now();
    private static final LocalDate FOM = TOM.minusYears(5);
    private static final LocalDate FOM_BARNET = LocalDate.of(2018,5,1);
    private static final Landkoder NORGE = new Landkoder().withValue("NOR");
    private static final Bostedsadresse NORSK_ADRESSE = new Bostedsadresse()
            .withStrukturertAdresse(new Gateadresse()
                    .withGatenavn("Sannergata")
                    .withHusnummer(2)
                    .withPoststed(new Postnummer().withValue("0560"))
                    .withLandkode(NORGE));
    private static final PersonIdent MOR_PERSON_IDENT = new PersonIdent().withIdent(new NorskIdent().withIdent(SøknadTestdata.morPersonident));
    private static final PersonIdent BARN_PERSON_IDENT = new PersonIdent().withIdent(new NorskIdent().withIdent(SøknadTestdata.barnPersonident));
    private static final PersonIdent FAR_PERSON_IDENT = new PersonIdent().withIdent(new NorskIdent().withIdent(SøknadTestdata.farPersonident));

    @Bean
    @Profile("mock-personopplysninger")
    @Primary
    public PersonConsumer personConsumerMock() throws HentPersonhistorikkSikkerhetsbegrensning, HentPersonhistorikkPersonIkkeFunnet, HentPersonSikkerhetsbegrensning, HentPersonPersonIkkeFunnet {
        PersonConsumer personConsumer = mock(PersonConsumer.class);
        ArgumentCaptor<HentPersonRequest> personRequestCaptor = ArgumentCaptor.forClass(HentPersonRequest.class);
        ArgumentCaptor<HentPersonhistorikkRequest> historikkRequestCaptor = ArgumentCaptor.forClass(HentPersonhistorikkRequest.class);

        when(personConsumer.hentPersonhistorikkResponse(historikkRequestCaptor.capture())).thenAnswer(invocation -> {
            PersonIdent personIdent = (PersonIdent) historikkRequestCaptor.getValue().getAktoer();
            if (personIdent.getIdent().getIdent().equals(SøknadTestdata.barnPersonident)) {
                return hentPersonhistorikkResponseBarn();
            } else {
                return hentPersonHistorikkResponse(personIdent.getIdent().getIdent().equals(SøknadTestdata.morPersonident));
            }
        });
        when(personConsumer.hentPersonResponse(personRequestCaptor.capture())).thenAnswer(invocation -> {
            PersonIdent personIdent = (PersonIdent) personRequestCaptor.getValue().getAktoer();

            if (personIdent.getIdent().getIdent().equals(SøknadTestdata.morPersonident)) {
                return hentPersonResponseForMor();
            } else if (personIdent.getIdent().getIdent().equals(SøknadTestdata.barnPersonident)) {
                return hentPersonResponseForBarn();
            } else {
                return hentPersonResponseForFar();
            }
        });

        doNothing().when(personConsumer).ping();
        return personConsumer;
    }

    private HentPersonhistorikkResponse hentPersonHistorikkResponse(boolean erMor) {
        HentPersonhistorikkResponse response = new HentPersonhistorikkResponse();
        response.setAktoer(erMor? MOR_PERSON_IDENT : FAR_PERSON_IDENT);
        response
                .withStatsborgerskapListe(hentStatsborgerskap(false))
                .withPersonstatusListe(hentPersonstatus(false))
                .withMidlertidigAdressePeriodeListe(hentMidlertidigAdresse())
                .withBostedsadressePeriodeListe(hentBostedsadresse(false));

        return response;
    }

    private HentPersonhistorikkResponse hentPersonhistorikkResponseBarn() {
        HentPersonhistorikkResponse response = new HentPersonhistorikkResponse();
        response.setAktoer(BARN_PERSON_IDENT);
        response
                .withStatsborgerskapListe(hentStatsborgerskap(true))
                .withBostedsadressePeriodeListe(hentBostedsadresse(true))
                .withPersonstatusListe(hentPersonstatus(true));

        return response;
    }

    private HentPersonResponse hentPersonResponseForMor() {
        HentPersonResponse response = new HentPersonResponse();
        return response.withPerson(hentPersoninfoMor());
    }

    private HentPersonResponse hentPersonResponseForBarn() {
        HentPersonResponse response = new HentPersonResponse();
        return response.withPerson(hentPersoninfoBarn());
    }

    private HentPersonResponse hentPersonResponseForFar() {
        HentPersonResponse response = new HentPersonResponse();
        return response.withPerson(hentPersoninfoFar());
    }

    private Person hentPersoninfoMor() {
        Bruker person = new Bruker();
        person
                .withBostedsadresse(NORSK_ADRESSE)
                .withKjoenn(new Kjoenn().withKjoenn(new Kjoennstyper().withKodeRef("KVINNE")))
                .withSivilstand(new Sivilstand().withSivilstand(new Sivilstander().withValue("GIFT")))
                .withPersonstatus(new Personstatus().withPersonstatus(new Personstatuser().withValue("BOSA")))
                .withPersonnavn(new Personnavn().withSammensattNavn("TEST TESTESEN"))
                .withHarFraRolleI(hentFamilierelasjonerMor())
                .withGeografiskTilknytning(new Bydel().withGeografiskTilknytning("0315"))
                .withGjeldendePostadressetype(new Postadressetyper().withValue("BOSTEDSADRESSE"))
                .withStatsborgerskap(new Statsborgerskap().withLand(NORGE))
                .withFoedselsdato(hentFoedselsdato("1990-01-01"))
                .withAktoer(MOR_PERSON_IDENT);

        return person;
    }

    private Person hentPersoninfoFar() {
        Bruker person = new Bruker();
        person
                .withBostedsadresse(NORSK_ADRESSE)
                .withKjoenn(new Kjoenn().withKjoenn(new Kjoennstyper().withKodeRef("MANN")))
                .withSivilstand(new Sivilstand().withSivilstand(new Sivilstander().withValue("GIFT")))
                .withPersonstatus(new Personstatus().withPersonstatus(new Personstatuser().withValue("BOSA")))
                .withPersonnavn(new Personnavn().withSammensattNavn("EKTEMANN TESTESEN"))
                .withHarFraRolleI(hentFamilierelasjonerFar())
                .withGeografiskTilknytning(new Bydel().withGeografiskTilknytning("0315"))
                .withGjeldendePostadressetype(new Postadressetyper().withValue("BOSTEDSADRESSE"))
                .withStatsborgerskap(new Statsborgerskap().withLand(NORGE))
                .withFoedselsdato(hentFoedselsdato("1990-01-01"))
                .withAktoer(FAR_PERSON_IDENT);

        return person;
    }

    private Person hentPersoninfoBarn() {
        Bruker barn = new Bruker();
        barn
                .withBostedsadresse(NORSK_ADRESSE)
                .withKjoenn(new Kjoenn().withKjoenn(new Kjoennstyper().withKodeRef("KVINNE")))
                .withSivilstand(new Sivilstand().withSivilstand(new Sivilstander().withValue("UGIF")))
                .withPersonstatus(new Personstatus().withPersonstatus(new Personstatuser().withValue("BOSA")))
                .withPersonnavn(new Personnavn().withSammensattNavn("BARN TESTESEN"))
                .withHarFraRolleI(hentFamilierelasjonerBarn())
                .withGeografiskTilknytning(new Bydel().withGeografiskTilknytning("0315"))
                .withGjeldendePostadressetype(new Postadressetyper().withValue("BOSTEDSADRESSE"))
                .withStatsborgerskap(new Statsborgerskap().withLand(NORGE))
                .withFoedselsdato(hentFoedselsdato("2018-05-01"))
                .withAktoer(BARN_PERSON_IDENT);

        return barn;
    }

    private Collection<Familierelasjon> hentFamilierelasjonerMor() {
        Familierelasjon giftMed = new Familierelasjon();
        giftMed
                .withHarSammeBosted(true)
                .withTilRolle(new Familierelasjoner().withValue("EKTE"))
                .withTilPerson(new Person()
                        .withAktoer(new PersonIdent().withIdent(new NorskIdent().withIdent(SøknadTestdata.farPersonident)))
                        .withFoedselsdato(hentFoedselsdato("1990-01-01"))
                        .withPersonnavn(new Personnavn().withSammensattNavn("EKTEMANN TESTESEN")));

        Familierelasjon barnet = new Familierelasjon();
        barnet
                .withHarSammeBosted(true)
                .withTilRolle(new Familierelasjoner().withValue("BARN"))
                .withTilPerson(new Person()
                        .withAktoer(BARN_PERSON_IDENT)
                        .withFoedselsdato(hentFoedselsdato("2018-05-01"))
                        .withPersonnavn(new Personnavn().withSammensattNavn("BARN TESTESEN")));

        return Arrays.asList(giftMed, barnet);
    }

    private Collection<Familierelasjon> hentFamilierelasjonerBarn() {
        Familierelasjon far = new Familierelasjon();
        far
                .withHarSammeBosted(true)
                .withTilRolle(new Familierelasjoner().withValue("FARA"))
                .withTilPerson(new Person()
                        .withAktoer(new PersonIdent().withIdent(new NorskIdent().withIdent(SøknadTestdata.farPersonident)))
                        .withFoedselsdato(hentFoedselsdato("1990-01-01"))
                        .withPersonnavn(new Personnavn().withSammensattNavn("EKTEMANN TESTESEN")));

        Familierelasjon mor = new Familierelasjon();
        mor
                .withHarSammeBosted(true)
                .withTilRolle(new Familierelasjoner().withValue("MORA"))
                .withTilPerson(new Person()
                        .withAktoer(MOR_PERSON_IDENT)
                        .withFoedselsdato(hentFoedselsdato("1990-01-01"))
                        .withPersonnavn(new Personnavn().withSammensattNavn("TEST TESTESEN")));

        return Arrays.asList(far, mor);
    }

    private Collection<Familierelasjon> hentFamilierelasjonerFar() {
        Familierelasjon giftMed = new Familierelasjon();
        giftMed
                .withHarSammeBosted(true)
                .withTilRolle(new Familierelasjoner().withValue("EKTE"))
                .withTilPerson(new Person()
                        .withAktoer(MOR_PERSON_IDENT)
                        .withFoedselsdato(hentFoedselsdato("1990-01-01"))
                        .withPersonnavn(new Personnavn().withSammensattNavn("TEST TESTESEN")));

        Familierelasjon barnet = new Familierelasjon();
        barnet
                .withHarSammeBosted(true)
                .withTilRolle(new Familierelasjoner().withValue("BARN"))
                .withTilPerson(new Person()
                        .withAktoer(BARN_PERSON_IDENT)
                        .withFoedselsdato(hentFoedselsdato("2018-05-01"))
                        .withPersonnavn(new Personnavn().withSammensattNavn("BARN TESTESEN")));

        return Arrays.asList(giftMed, barnet);
    }

    private Foedselsdato hentFoedselsdato(String dato) {
        try {
            return new Foedselsdato().withFoedselsdato(DatatypeFactory.newInstance().newXMLGregorianCalendar(dato));
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private Collection<PersonstatusPeriode> hentPersonstatus(boolean erBarnet) {
        PersonstatusPeriode personstatusPeriode = new PersonstatusPeriode();
        personstatusPeriode
                .withPersonstatus(new Personstatuser().withValue("BOSA"))
                .withPeriode(new Periode()
                        .withFom(DateUtil.convertToXMLGregorianCalendar(erBarnet ? FOM_BARNET : FOM))
                        .withTom(DateUtil.convertToXMLGregorianCalendar(TOM)));

        return Collections.singletonList(personstatusPeriode);

    }

    private Collection<StatsborgerskapPeriode> hentStatsborgerskap(boolean erBarnet) {
        StatsborgerskapPeriode statsborgerskapPeriode = new StatsborgerskapPeriode();
        statsborgerskapPeriode
                .withStatsborgerskap(new Statsborgerskap().withLand(NORGE))
                .withPeriode(new Periode()
                        .withFom(DateUtil.convertToXMLGregorianCalendar(erBarnet ? FOM_BARNET : FOM))
                        .withTom(DateUtil.convertToXMLGregorianCalendar(TOM)));

        return Collections.singletonList(statsborgerskapPeriode);
    }

    private Collection<BostedsadressePeriode> hentBostedsadresse(boolean erBarnet) {
        BostedsadressePeriode bostedsadressePeriode = new BostedsadressePeriode();
        bostedsadressePeriode
                .withBostedsadresse(NORSK_ADRESSE)
                .withPeriode(new Periode()
                        .withFom(DateUtil.convertToXMLGregorianCalendar(erBarnet ? FOM_BARNET : FOM))
                        .withTom(DateUtil.convertToXMLGregorianCalendar(TOM)));

        return Collections.singletonList(bostedsadressePeriode);
    }

    private Collection<MidlertidigPostadresse> hentMidlertidigAdresse() {
        MidlertidigPostadresseUtland midlertidigPostadresseUtland = new MidlertidigPostadresseUtland();
        midlertidigPostadresseUtland
                .withUstrukturertAdresse(new UstrukturertAdresse()
                        .withAdresselinje1("TEST 1")
                        .withAdresselinje2("TEST 2")
                        .withAdresselinje3("TEST 3")
                        .withLandkode(new Landkoder().withValue("SWE")))
                .withPostleveringsPeriode(new Gyldighetsperiode()
                        .withFom(DateUtil.convertToXMLGregorianCalendar(FOM))
                        .withTom(DateUtil.convertToXMLGregorianCalendar(TOM)));

        return Collections.singletonList(midlertidigPostadresseUtland);
    }
}
