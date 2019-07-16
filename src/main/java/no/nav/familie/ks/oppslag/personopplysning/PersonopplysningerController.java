package no.nav.familie.ks.oppslag.personopplysning;

import no.nav.familie.ks.oppslag.felles.ws.MDCOperations;
import no.nav.familie.ks.oppslag.personopplysning.domene.AktørId;
import no.nav.familie.ks.oppslag.personopplysning.domene.PersonhistorikkInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/personopplysning")
public class PersonopplysningerController {

    private PersonopplysningerTjeneste personopplysningerTjeneste;

    public PersonopplysningerController(PersonopplysningerTjeneste personopplysningerTjeneste) {
        this.personopplysningerTjeneste = personopplysningerTjeneste;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "historikk")
    public PersonhistorikkInfo historikk(@NotNull @RequestParam(name = "id") String aktørId) {
        MDCOperations.putCallId(); // FIXME: Midlertidig, bør settes generelt i et filter elns
        LocalDate idag = LocalDate.now();
        return personopplysningerTjeneste.hentHistorikkFor(new AktørId(aktørId), idag.minusYears(5), idag);
    }
}
