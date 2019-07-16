package no.nav.familie.ks.oppslag.felles.ws.sts;

public class SecurityConstants {

    public static final String STS_URL_KEY = "SECURITYTOKENSERVICE_URL";
    public static final String SYSTEMUSER_USERNAME = "credential.username";
    public static final String SYSTEMUSER_PASSWORD = "credential.password";

    private SecurityConstants() {
        throw new IllegalAccessError("Skal ikke instansieres");
    }

}
