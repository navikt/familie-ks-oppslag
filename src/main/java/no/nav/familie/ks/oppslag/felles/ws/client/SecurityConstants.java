package no.nav.familie.ks.oppslag.felles.ws.client;

public class SecurityConstants {

    public static final String STS_URL_KEY = "securityTokenService.url";
    public static final String SYSTEMUSER_USERNAME = "no.nav.modig.security.systemuser.username";
    public static final String SYSTEMUSER_PASSWORD = "no.nav.modig.security.systemuser.password";

    private SecurityConstants() {
        throw new IllegalAccessError("Skal ikke instansieres");
    }

}
