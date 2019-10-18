package no.nav.familie.ks.oppslag.felles.ws.sikkerhet.domene;

import javax.security.auth.Destroyable;
import java.security.Principal;

import static no.nav.familie.ks.oppslag.felles.ws.client.SecurityConstants.SYSTEMUSER_USERNAME;

public final class ConsumerId implements Principal, Destroyable {

    private String consumerIdString;
    private boolean destroyed;

    public ConsumerId(String consumerId) {
        this.consumerIdString = consumerId;
    }

    public ConsumerId() {
        consumerIdString = getProperty(SYSTEMUSER_USERNAME);

        if (consumerIdString == null) {
            throw new IllegalStateException(
                    SYSTEMUSER_USERNAME + " is not set, failed to set " + ConsumerId.class.getName());
        }
    }

    @Override
    public void destroy() {
        consumerIdString = null;
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public String getName() {
        return consumerIdString;
    }

    public String getConsumerId() {
        return consumerIdString;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
                (destroyed ? "destroyed" : consumerIdString) +
                "]";
    }

    public static String getProperty(String key) {
        String val = System.getProperty(key);
        if (val == null) {
            val = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        return val;
    }
}
