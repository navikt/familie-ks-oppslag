package no.nav.familie.ks.oppslag.helse;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.oppslag.medlemskap.internal.MedlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class MedlHelsesjekk implements HealthIndicator {

    private final Counter medlOppe = Metrics.counter("helsesjekk.medl", "status", "oppe");
    private final Counter medlNede = Metrics.counter("helsesjekk.medl", "status", "nede");

    private MedlClient medlClient;

    public MedlHelsesjekk(@Autowired MedlClient medlClient) {
        this.medlClient = medlClient;
    }

    @Override
    public Health health() {
        try {
            medlClient.ping();
            medlOppe.increment();
            return Health.up().build();
        } catch (Exception e) {
            medlNede.increment();
            return Health.down(e).build();
        }
    }
}
