package no.nav.familie.ks.oppslag.medlemskap;

import no.nav.familie.ks.oppslag.medlemskap.domain.MedlemskapsInfo;
import no.nav.familie.ks.oppslag.medlemskap.domain.MedlemskapsOversetter;
import no.nav.familie.ks.oppslag.medlemskap.internal.MedlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public ResponseEntity<List<MedlemskapsUnntakResponse>> hentMedlemskapsUnntak(String aktørId) {
        try {
            List<MedlemskapsUnntakResponse> response = medlClient.hentMedlemskapsUnntakResponse(aktørId);
            //MedlemskapsInfo medlemskapsInfo = medlemskapsOversetter.tilMedlemskapsInfo(medlClient.hentMedlemskapsUnntakResponse(aktørId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag mot MEDL2 for Aktør: {}", aktørId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
