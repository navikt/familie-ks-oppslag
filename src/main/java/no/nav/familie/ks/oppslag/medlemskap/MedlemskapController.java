package no.nav.familie.ks.oppslag.medlemskap;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/medlemskap")
public class MedlemskapController {

    private MedlemskapService medlemskapService;

    public MedlemskapController(MedlemskapService service) {
        this.medlemskapService = service;
    }

    @GetMapping
    public String hentMedlemskapsUnntak(@RequestParam("id") String aktørId) {
        String medlemskapsUnntak = medlemskapService.hentMedlemskapsUnntak(aktørId);
        return medlemskapsUnntak;
    }
}
