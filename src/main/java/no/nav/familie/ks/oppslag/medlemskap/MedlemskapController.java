package no.nav.familie.ks.oppslag.medlemskap;

import no.nav.familie.ks.oppslag.medlemskap.domain.MedlemskapsInfo;
import no.nav.security.oidc.api.Unprotected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/medlemskap")
public class MedlemskapController {

    private MedlemskapService medlemskapService;

    public MedlemskapController(MedlemskapService service) {
        this.medlemskapService = service;
    }

    @GetMapping
    @Unprotected
    public ResponseEntity<List<MedlemskapsUnntakResponse>> hentMedlemskapsUnntak(@RequestParam("id") String aktørId) {
        return medlemskapService.hentMedlemskapsUnntak(aktørId);
    }
}
