package no.nav.familie.ks.oppslag.medlemskap;

import no.nav.familie.ks.oppslag.medlemskap.domain.MedlemskapsInfo;
import no.nav.familie.ks.oppslag.medlemskap.domain.MedlemskapsOversetter;
import no.nav.familie.ks.oppslag.medlemskap.internal.MedlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedlemskapService {

    private static final Logger LOG = LoggerFactory.getLogger(MedlemskapService.class);

    private MedlClient medlClient;
    private MedlemskapsOversetter medlemskapsOversetter;

    @Autowired
    public MedlemskapService(MedlClient medlClient, MedlemskapsOversetter medlemskapsOversetter) {
        this.medlClient = medlClient;
        this.medlemskapsOversetter = medlemskapsOversetter;
    }

    public MedlemskapsInfo hentMedlemskapsUnntak(String aktørId) {
        return medlemskapsOversetter.tilMedlemskapsInfo(medlClient.hentMedlemskapsUnntakResponse(aktørId));
    }

}
