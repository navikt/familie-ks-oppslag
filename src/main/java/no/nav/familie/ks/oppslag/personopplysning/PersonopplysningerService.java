package no.nav.familie.ks.oppslag.personopplysning;

import no.nav.familie.ks.oppslag.personopplysning.domene.AktørId;
import no.nav.familie.ks.oppslag.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.oppslag.personopplysning.domene.Personinfo;
import no.nav.familie.ks.oppslag.personopplysning.domene.TpsOversetter;
import no.nav.familie.ks.oppslag.personopplysning.internal.PersonConsumer;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Service
@ApplicationScope
public class PersonopplysningerService {

    private final PersonConsumer personConsumer;
    private TpsOversetter oversetter;

    private static final Logger LOG = LoggerFactory.getLogger(PersonopplysningerService.class);

    public PersonopplysningerService(PersonConsumer personConsumer, TpsOversetter oversetter) {
        this.personConsumer = personConsumer;
        this.oversetter = oversetter;
    }


    public PersonhistorikkInfo hentHistorikkFor(AktørId aktørId, LocalDate fom, LocalDate tom) {
        Objects.requireNonNull(aktørId, "aktørId");
        Objects.requireNonNull(fom, "fom");
        Objects.requireNonNull(tom, "tom");
        var request = new HentPersonhistorikkRequest();
        request.setAktoer(new AktoerId().withAktoerId(aktørId.getId()));
        try {
            var response = personConsumer.hentPersonhistorikkResponse(request);
            return oversetter.tilPersonhistorikkInfo(aktørId.getId(), response);
        } catch (HentPersonhistorikkSikkerhetsbegrensning hentPersonhistorikkSikkerhetsbegrensning) {
            throw new IllegalArgumentException(hentPersonhistorikkSikkerhetsbegrensning);
        } catch (HentPersonhistorikkPersonIkkeFunnet hentPersonhistorikkPersonIkkeFunnet) {
            // Fant ikke personen returnerer tomt sett
            return PersonhistorikkInfo.builder().medAktørId(aktørId.getId()).build();
        }
    }

    public Personinfo hentPersoninfoFor(AktørId aktørId) {
        var request = new HentPersonRequest();
        request.setAktoer(new AktoerId().withAktoerId(aktørId.getId()));
        request.withInformasjonsbehov(List.of(Informasjonsbehov.FAMILIERELASJONER, Informasjonsbehov.ADRESSE));
        try {
            var response = personConsumer.hentPersonResponse(request);
            return oversetter.tilPersoninfo(aktørId, response);
        } catch (HentPersonSikkerhetsbegrensning hentPersonSikkerhetsbegrensning) {
            LOG.info("Ikke tilgang til å hente personinfo for person");
            throw new IllegalArgumentException(hentPersonSikkerhetsbegrensning);
        } catch (HentPersonPersonIkkeFunnet hentPersonPersonIkkeFunnet) {
            LOG.info("Hent Personinfo: Personen finnes ikke i TPS");
            throw new IllegalArgumentException(hentPersonPersonIkkeFunnet);
        }
    }
}
