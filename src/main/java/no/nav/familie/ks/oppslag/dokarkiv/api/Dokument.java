package no.nav.familie.ks.oppslag.dokarkiv.api;

import javax.validation.constraints.NotEmpty;

public class Dokument {
    @NotEmpty
    private byte[] dokument;
    @NotEmpty
    private FilType filType;

    public Dokument(byte[] dokument, FilType filType) {
        this.dokument = dokument;
        this.filType = filType;
    }

    public byte[] getDokument() {
        return dokument;
    }

    public FilType getFilType() {
        return filType;
    }

    @Override
    public String toString() {
        return "Dokument{" +
                "filType=" + filType +
                '}';
    }


}
