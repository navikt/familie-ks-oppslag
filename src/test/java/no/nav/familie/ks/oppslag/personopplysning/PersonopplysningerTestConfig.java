package no.nav.familie.ks.oppslag.personopplysning;

import no.nav.familie.ks.oppslag.felles.ws.DateUtil;
import no.nav.familie.ks.oppslag.personopplysning.domene.TpsOversetter;
import no.nav.familie.ks.oppslag.personopplysning.internal.PersonConsumer;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class PersonopplysningerTestConfig {

    private static final String PERSONIDENT = "11111111111";
    private static final String AKTØR_ID = "1000011111111";
    private static final LocalDate TOM = LocalDate.now();
    private static final LocalDate FOM = TOM.minusYears(5);
    private static final LocalDate FODSELSDATO = LocalDate.now().minusYears(1);
    private static final Landkoder NORGE = new Landkoder().withValue("NOR");
    private static final Bostedsadresse NORSK_ADRESSE = new Bostedsadresse()
            .withStrukturertAdresse(new Gateadresse()
                    .withGatenavn("Sannergata")
                    .withHusnummer(2)
                    .withPoststed(new Postnummer().withValue("0560"))
                    .withLandkode(NORGE));

    @Bean
    @Profile("mock-personopplysninger")
    @Primary
    PersonopplysningerService personopplysningerServiceMock(@Autowired TpsOversetter tpsOversetter) throws HentPersonSikkerhetsbegrensning, HentPersonPersonIkkeFunnet, HentPersonhistorikkSikkerhetsbegrensning, HentPersonhistorikkPersonIkkeFunnet {
        PersonConsumer personConsumerMock = mock(PersonConsumer.class);
        when(personConsumerMock.hentPersonResponse(any())).thenReturn(hentPersonResponse());
        when(personConsumerMock.hentPersonhistorikkResponse(any())).thenReturn(hentPersonHistorikkResponse());

        return new PersonopplysningerService(personConsumerMock, tpsOversetter);
    }

    private HentPersonResponse hentPersonResponse() {
        HentPersonResponse response = new HentPersonResponse();
        Person person = new Person();
        response.setPerson(new Person()
                .withAktoer(new PersonIdent().withIdent(new NorskIdent().withIdent(PERSONIDENT)))
                .withBostedsadresse(NORSK_ADRESSE)
                .withFoedselsdato(new Foedselsdato().withFoedselsdato(DateUtil.convertToXMLGregorianCalendar(FODSELSDATO)))
                .withStatsborgerskap(new Statsborgerskap().withLand(NORGE)));
        return response.withPerson(person);
    }

    private HentPersonhistorikkResponse hentPersonHistorikkResponse() {
        HentPersonhistorikkResponse response = new HentPersonhistorikkResponse();
        response.setAktoer(new AktoerId().withAktoerId(AKTØR_ID));
        response
                .withStatsborgerskapListe(hentStatsborgerskap())
                .withBostedsadressePeriodeListe(hentBostedsadresse());

        return response;
    }

    private Collection<StatsborgerskapPeriode> hentStatsborgerskap() {
        StatsborgerskapPeriode statsborgerskapPeriode = new StatsborgerskapPeriode();
        statsborgerskapPeriode
                .withStatsborgerskap(new Statsborgerskap().withLand(NORGE))
                .withPeriode(new Periode()
                        .withFom(DateUtil.convertToXMLGregorianCalendar(FOM))
                        .withTom(DateUtil.convertToXMLGregorianCalendar(TOM)));

        return Collections.singletonList(statsborgerskapPeriode);
    }

    private Collection<BostedsadressePeriode> hentBostedsadresse() {
        BostedsadressePeriode bostedsadressePeriode = new BostedsadressePeriode();
        bostedsadressePeriode
                .withBostedsadresse(NORSK_ADRESSE)
                .withPeriode(new Periode()
                        .withFom(DateUtil.convertToXMLGregorianCalendar(FOM))
                        .withTom(DateUtil.convertToXMLGregorianCalendar(TOM)));

        return Collections.singletonList(bostedsadressePeriode);
    }
}
