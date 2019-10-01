package no.nav.familie.ks.oppslag.medlemskap;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class MedlemskapsUnntakResponse {

    @JsonProperty("dekning")
    private String dekning = null;

    @JsonProperty("fraOgMed")
    private Date fraOgMed = null;

    @JsonProperty("grunnlag")
    private String grunnlag = null;

    @JsonProperty("helsedel")
    private Boolean helsedel = null;

    @JsonProperty("ident")
    private String ident = null;

    @JsonProperty("lovvalg")
    private String lovvalg = null;

    @JsonProperty("lovvalgsland")
    private String lovvalgsland = null;

    @JsonProperty("medlem")
    private Boolean medlem = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("statusaarsak")
    private String statusaarsak = null;

    @JsonProperty("tilOgMed")
    private Date tilOgMed = null;

    @JsonProperty("unntakId")
    private Long unntakId = null;

    /**
     * Dekningsgraden for dette medlemskapsunntaket.&lt;br/&gt;Kodeverk: &lt;a href&#x3D;\&quot;https://kodeverk-web.nais.adeo.no/kodeverksoversikt/kodeverk/DekningMedl\&quot; target&#x3D;\&quot;_blank\&quot;&gt;DekningMedl&lt;/a&gt;
     *
     * @return dekning
     **/
    @JsonProperty("dekning")
    public String getDekning() {
        return dekning;
    }

    public void setDekning(String dekning) {
        this.dekning = dekning;
    }

    /**
     * Startdatoen for perioden til medlemskapsunntaket, på ISO-8601 format.
     *
     * @return fraOgMed
     **/
    @JsonProperty("fraOgMed")
    @NotNull
    public Date getFraOgMed() {
        return fraOgMed;
    }

    public void setFraOgMed(Date fraOgMed) {
        this.fraOgMed = fraOgMed;
    }


    /**
     * Grunnlaget for dette medlemskapsunntaket.&lt;br/&gt;Kodeverk: &lt;a href&#x3D;\&quot;https://kodeverk-web.nais.adeo.no/kodeverksoversikt/kodeverk/GrunnlagMedl\&quot; target&#x3D;\&quot;_blank\&quot;&gt;GrunnlagMedl&lt;/a&gt;
     *
     * @return grunnlag
     **/
    @JsonProperty("grunnlag")
    @NotNull
    public String getGrunnlag() {
        return grunnlag;
    }

    public void setGrunnlag(String grunnlag) {
        this.grunnlag = grunnlag;
    }


    /**
     * Hvorvidt dekningen for medlemskapsunntaket har en helsedel.
     *
     * @return helsedel
     **/
    @JsonProperty("helsedel")
    @NotNull
    public Boolean isHelsedel() {
        return helsedel;
    }

    public void setHelsedel(Boolean helsedel) {
        this.helsedel = helsedel;
    }


    /**
     * Den naturlige identen som medlemskapsunntaket er lagret på.
     *
     * @return ident
     **/
    @JsonProperty("ident")
    @NotNull
    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }


    /**
     * Lovvalget for dette medlemskapsunntaket.&lt;br/&gt;Kodeverk: &lt;a href&#x3D;\&quot;https://kodeverk-web.nais.adeo.no/kodeverksoversikt/kodeverk/LovvalgMedl\&quot; target&#x3D;\&quot;_blank\&quot;&gt;LovvalgMedl&lt;/a&gt;
     *
     * @return lovvalg
     **/
    @JsonProperty("lovvalg")
    @NotNull
    public String getLovvalg() {
        return lovvalg;
    }

    public void setLovvalg(String lovvalg) {
        this.lovvalg = lovvalg;
    }


    /**
     * Landet dette medlemskapsunntaket gjelder for.&lt;br/&gt;Kodeverk: &lt;a href&#x3D;\&quot;https://kodeverk-web.nais.adeo.no/kodeverksoversikt/kodeverk/Landkoder\&quot; target&#x3D;\&quot;_blank\&quot;&gt;Landkoder&lt;/a&gt;
     *
     * @return lovvalgsland
     **/
    @JsonProperty("lovvalgsland")
    public String getLovvalgsland() {
        return lovvalgsland;
    }

    public void setLovvalgsland(String lovvalgsland) {
        this.lovvalgsland = lovvalgsland;
    }

    /**
     * Beskriver hvorvidt dette medlemskapsunntaket handler om et medlemskap i folketrygden eller ikke.
     *
     * @return medlem
     **/
    @JsonProperty("medlem")
    @NotNull
    public Boolean isMedlem() {
        return medlem;
    }

    public void setMedlem(Boolean medlem) {
        this.medlem = medlem;
    }


    /**
     * Sluttdatoen for perioden til medlemskapsunntaket.&lt;br/&gt;Kodeverk: &lt;a href&#x3D;\&quot;https://kodeverk-web.nais.adeo.no/kodeverksoversikt/kodeverk/PeriodestatusMedl\&quot; target&#x3D;\&quot;_blank\&quot;&gt;PeriodestatusMedl&lt;/a&gt;
     *
     * @return status
     **/
    @JsonProperty("status")
    @NotNull
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Dersom statusen på medlemskapsunntaket ikke er gyldig vil dette feltet beskrive hvorfor.&lt;br/&gt;Kodeverk: &lt;a href&#x3D;\&quot;https://kodeverk-web.nais.adeo.no/kodeverksoversikt/kodeverk/StatusaarsakMedl\&quot; target&#x3D;\&quot;_blank\&quot;&gt;StatusaarsakMedl&lt;/a&gt;
     *
     * @return statusaarsak
     **/
    @JsonProperty("statusaarsak")
    public String getStatusaarsak() {
        return statusaarsak;
    }

    public void setStatusaarsak(String statusaarsak) {
        this.statusaarsak = statusaarsak;
    }

    /**
     * Sluttdatoen for perioden til medlemskapsunntaket, på ISO-8601 format..
     *
     * @return tilOgMed
     **/
    @JsonProperty("tilOgMed")
    @NotNull
    public Date getTilOgMed() {
        return tilOgMed;
    }

    public void setTilOgMed(Date tilOgMed) {
        this.tilOgMed = tilOgMed;
    }

    /**
     * Den funksjonelle ID&#39;en til et medlemskapsunntak.
     *
     * @return unntakId
     **/
    @JsonProperty("unntakId")
    @NotNull
    public Long getUnntakId() {
        return unntakId;
    }

    public void setUnntakId(Long unntakId) {
        this.unntakId = unntakId;
    }

}


