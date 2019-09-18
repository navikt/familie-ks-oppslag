package no.nav.familie.ks.oppslag.dokarkiv;

import no.nav.familie.ks.oppslag.dokarkiv.api.*;
import no.nav.familie.ks.oppslag.dokarkiv.client.DokarkivClient;
import no.nav.familie.ks.oppslag.dokarkiv.client.domene.*;
import no.nav.familie.ks.oppslag.dokarkiv.client.domene.Dokument;
import no.nav.familie.ks.oppslag.dokarkiv.metadata.AbstractDokumentMetadata;
import no.nav.familie.ks.oppslag.dokarkiv.metadata.DokumentMetadata;
import no.nav.familie.ks.oppslag.dokarkiv.metadata.KontanstøtteSøknadMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DokarkivService {
    private static final Map<String, ? extends AbstractDokumentMetadata> METADATA = Map.of(
            DokumentType.KONTANTSTØTTE_SØKNAD.name(), new KontanstøtteSøknadMetadata()
    );

    private final DokarkivClient dokarkivClient;

    @Autowired
    public DokarkivService(DokarkivClient dokarkivClient) {
        this.dokarkivClient = dokarkivClient;
    }

    public ArkiverDokumentResponse lagInngåendeJournalpost(ArkiverDokumentRequest arkiverDokumentRequest) {
        String dokumentType = arkiverDokumentRequest.getDokumentType().name();
        String fnr = arkiverDokumentRequest.getFnr();
        String navn = arkiverDokumentRequest.getNavn();

        Optional<? extends AbstractDokumentMetadata> dokumentMetadata = Optional.ofNullable(METADATA.get(dokumentType));

        var request = dokumentMetadata
                .map(metadata -> mapTilOpprettJournalpostRequest(metadata, fnr, navn, arkiverDokumentRequest.getDokumenter()))
                .orElseThrow(() -> new IllegalArgumentException("Ukjent dokumenttype" + dokumentType));

        Optional<OpprettJournalpostResponse> response = Optional.ofNullable(dokarkivClient.lagJournalpost(request, arkiverDokumentRequest.isForsøkFerdigstill(), fnr));
        return response.map(this::mapTilArkiverDokumentResponse).orElse(null);
    }

    private OpprettJournalpostRequest mapTilOpprettJournalpostRequest(AbstractDokumentMetadata metadata, String fnr, String navn, List<no.nav.familie.ks.oppslag.dokarkiv.api.Dokument> dokumenter) {

        List<Dokument> dokumentRequest = dokumenter.stream()
                .map(s -> mapTilDokument(s, metadata))
                .collect(Collectors.toList());


        return new OpprettJournalpostRequest.OpprettJournalpostRequestBuilder().builder()
                .medJournalpostType(JournalpostType.INNGAAENDE)
                .medBehandlingstema(metadata.getBehandlingstema())
                .medKanal(metadata.getKanal())
                .medTema(metadata.getTema())
                .medJournalfoerendeEnhet(DokumentMetadata.JOURNALFØRENDE_ENHET)
                .medAvsenderMottaker(new AvsenderMottaker(fnr, IdType.FNR, navn))
                .medBruker(new Bruker(IdType.FNR, fnr))
                .medDokumenter(dokumentRequest)
//                .medSak() Når vi tar over fagsak, så må dennne settes til vår. For BRUT001 behandling, så kan ikke denne settes
                .build();

    }

    private Dokument mapTilDokument(no.nav.familie.ks.oppslag.dokarkiv.api.Dokument dokument, AbstractDokumentMetadata metadata) {
        String variantFormat;
        if (dokument.getFilType().equals(FilType.PDFA)) {
            variantFormat = "ARKIV"; //ustrukturert dokumentDto
        } else {
            variantFormat = "ORIGINAL"; //strukturert dokumentDto
        }

        return Dokument.DokumentBuilder.aDokument()
                .medBrevkode(metadata.getBrevkode())
                .medDokumentKategori(metadata.getDokumentKategori())
                .medTittel(metadata.getTittel())
                .medDokumentvarianter(List.of(
                        new DokumentVariant(dokument.getFilType().name(),
                                variantFormat,
                                dokument.getDokument())))
                .build();
    }

    private ArkiverDokumentResponse mapTilArkiverDokumentResponse(OpprettJournalpostResponse response) {
        return new ArkiverDokumentResponse(response.getJournalpostId(), response.getJournalpostferdigstilt());
    }
}
