package no.nav.familie.ks.oppslag.helse;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.oppslag.aktør.internal.AktørregisterClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class AktørHelsesjekk implements HealthIndicator {

    private final Counter aktørOppe = Metrics.counter("helsesjekk.aktoer", "status", "oppe");
    private final Counter aktørNede = Metrics.counter("helsesjekk.aktoer", "status", "nede");

    private AktørregisterClient aktørregisterClient;

    public AktørHelsesjekk(@Autowired AktørregisterClient aktørregisterClient) {
        this.aktørregisterClient = aktørregisterClient;
    }

    @Override
    public Health health() {
        try {
            aktørregisterClient.ping();
            aktørOppe.increment();
            return Health.up().build();
        } catch (Exception e) {
            aktørNede.increment();
            return Health.down(e).build();
        }
    }
}
