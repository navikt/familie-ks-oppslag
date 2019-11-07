package no.nav.familie.ks.oppslag.dokarkiv.client.domene;

import javax.validation.constraints.NotNull;

public class Sak {

    @NotNull(message = "Sak mangler fagsaksystem")
    private String fagsaksystem;

    @NotNull(message = "Sak mangler sakstype")
    private String sakstype;

    @NotNull(message = "Sak mangler fagsakId")
    private String fagsakId;

    public Sak(String fagsakId) {
        this.fagsaksystem = "AO11"; // TODO: Bytt til riktig verdi for kontantstøtte når vi vet hva den er
        this.sakstype = "FAGSAK";
        this.fagsakId = fagsakId;
    }
}
