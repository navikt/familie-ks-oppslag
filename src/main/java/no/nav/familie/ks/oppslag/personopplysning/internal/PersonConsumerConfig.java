package no.nav.familie.ks.oppslag.personopplysning.internal;


import no.nav.familie.ks.oppslag.felles.ws.CallIdOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.xml.namespace.QName;

@Configuration
@EnableConfigurationProperties
public class PersonConsumerConfig {
    private static final String PERSON_V3_WSDL = "wsdl/no/nav/tjeneste/virksomhet/person/v3/Binding.wsdl";
    private static final String PERSON_V3_NAMESPACE = "http://nav.no/tjeneste/virksomhet/person/v3/Binding";
    private static final QName PERSON_V3_SERVICE = new QName(PERSON_V3_NAMESPACE, "Person_v3");
    private static final QName PERSON_V3_PORT = new QName(PERSON_V3_NAMESPACE, "Person_v3Port");

/*    static {
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
    }*/

    @Bean
    public PersonV3 getPort(@Value("${PERSON_V3_URL}") String personV3Url,
                            @Value("${SECURITYTOKENSERVICE_URL}") String stsUrl,
                            @Value("${CREDENTIAL_USERNAME}") String systemuserUsername,
                            @Value("${CREDENTIAL_PASSWORD}") String systemuserPwd) {

        System.setProperty("no.nav.modig.security.sts.url", stsUrl);
        System.setProperty("no.nav.modig.security.systemuser.username", systemuserUsername);
        System.setProperty("no.nav.modig.security.systemuser.password", systemuserPwd);

        /*
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(PERSON_V3_WSDL);
        factoryBean.setServiceName(PERSON_V3_SERVICE);
        factoryBean.setEndpointName(PERSON_V3_PORT);
        factoryBean.setServiceClass(PersonV3.class);
        factoryBean.setAddress(personV3Url);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getOutInterceptors().add(new CallIdOutInterceptor());
        return factoryBean.create(PersonV3.class);
         */
        return new CXFClient<>(PersonV3.class)
                .address(personV3Url)
                .configureStsForSystemUser()
                .build();
    }
}
