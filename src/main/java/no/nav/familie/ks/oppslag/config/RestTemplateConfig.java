package no.nav.familie.ks.oppslag.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("!dev")
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplateMedProxy() {
        return new RestTemplateBuilder()
                .additionalCustomizers(new NaisProxyCustomizer())
                .build();
    }
}
