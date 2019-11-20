package no.nav.familie.ks.oppslag;

import no.nav.familie.ks.oppslag.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@Import({ApplicationConfig.class})
public class E2ELauncher {

    public static void main(String... args) {
        SpringApplication app = new SpringApplicationBuilder(ApplicationConfig.class)
                .profiles(
                    "e2e",
                    "mock-aktor",
                    "mock-dokarkiv",
                    "mock-egenansatt",
                    "mock-innsyn",
                    "mock-medlemskap",
                    "mock-oppgave",
                    "mock-personopplysninger",
                    "mock-saf",
                    "mock-sts"
                ).build();
        app.run(args);
    }
}