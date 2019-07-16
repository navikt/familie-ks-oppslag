package no.nav.familie.ks.oppslag.felles.ws.sts;

public class SecurityConstants {

    public static final String STS_URL_KEY = "SECURITYTOKENSERVICE_URL";
    public static final String SYSTEMUSER_USERNAME = "CREDENTIAL_USERNAME";
    public static final String SYSTEMUSER_PASSWORD = "CREDENTIAL_PASSWORD";

    private SecurityConstants() {
        throw new IllegalAccessError("Skal ikke instansieres");
    }

}
