package no.nav.familie.ks.oppslag.config;


import no.nav.familie.ks.oppslag.felles.ws.CallIdOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.behandleoppgave.v1.BehandleOppgaveV1;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.oppgave.v3.binding.OppgaveV3;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.familie.ks.oppslag.felles.ws.client.NAVSTSClient.StsClientType.SECURITYCONTEXT_TIL_SAML;
import static no.nav.familie.ks.oppslag.felles.ws.client.SecurityConstants.STS_URL_KEY;
import static no.nav.familie.ks.oppslag.felles.ws.client.StsConfigurationUtil.wrapWithSts;

@Configuration
public class ServiceConfig {

    @Bean
    public PersonV3 personV3Port(@Value("${PERSON_V3_URL}") String personV3Url,
                                 @Value("${SECURITYTOKENSERVICE_URL}") String stsUrl,
                                 @Value("${CREDENTIAL_USERNAME}") String systemuserUsername,
                                 @Value("${CREDENTIAL_PASSWORD}") String systemuserPwd) {

        setSystemProperties(stsUrl, systemuserUsername, systemuserPwd);

        return new CXFClient<>(PersonV3.class)
                .address(personV3Url)
                .configureStsForSystemUser()
                .build();
    }

    @Bean
    public BehandleOppgaveV1 behandleOppgaveV1Port(@Value("${BEHANDLE_OPPGAVE_V1_URL}") String behandleOppgaveV1Url,
                                                   @Value("${SECURITYTOKENSERVICE_URL}") String stsUrl,
                                                   @Value("${CREDENTIAL_USERNAME}") String systemuserUsername,
                                                   @Value("${CREDENTIAL_PASSWORD}") String systemuserPwd) {

        setSystemProperties(stsUrl, systemuserUsername, systemuserPwd);

        return new CXFClient<>(BehandleOppgaveV1.class)
                .address(behandleOppgaveV1Url)
                .configureStsForSystemUser()
                .build();
    }

    @Bean
    public InnsynJournalV2 innsynJournalV2port(@Value("${INNSYN_JOURNAL_V2_URL}") String innsynJournalUrl,
                                               @Value("${SECURITYTOKENSERVICE_URL}") String stsUrl,
                                               @Value("${CREDENTIAL_USERNAME}") String systemuserUsername,
                                               @Value("${CREDENTIAL_PASSWORD}") String systemuserPwd) {

        setSystemProperties(stsUrl, systemuserUsername, systemuserPwd);
        return new CXFClient<>(InnsynJournalV2.class)
                .address(innsynJournalUrl)
                .configureStsForSystemUser()
                .build();
    }

    @Bean
    public OppgaveV3 oppgaveV3Port(@Value("${OPPGAVE_V3_URL}") String oppgaveV3Url,
                                   @Value("${STS_URL}") String stsUrl,
                                   @Value("${CREDENTIAL_USERNAME}") String systemuserUsername,
                                   @Value("${CREDENTIAL_PASSWORD}") String systemuserPwd) {

        setSystemProperties(stsUrl, systemuserUsername, systemuserPwd);
        System.setProperty(STS_URL_KEY, stsUrl);


        String WSDL = "wsdl/no/nav/tjeneste/virksomhet/oppgave/v3/Binding.wsdl";
        String NAMESPACE = "http://nav.no/tjeneste/virksomhet/oppgave/v3/Binding";
        QName SERVICE = new QName(NAMESPACE, "Oppgave_v3");
        QName PORT = new QName(NAMESPACE, "Oppgave_v3Port");

        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(WSDL);
        factoryBean.setServiceName(SERVICE);
        factoryBean.setEndpointName(PORT);
        factoryBean.setServiceClass(OppgaveV3.class);
        factoryBean.setAddress(oppgaveV3Url);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getOutInterceptors().add(new CallIdOutInterceptor());
        OppgaveV3 oppgaveV3 = factoryBean.create(OppgaveV3.class);

        return wrapWithSts(oppgaveV3, SECURITYCONTEXT_TIL_SAML);
    }


    @Bean
    public LoggingOutInterceptor loggingOutInterceptor() {
        LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
        loggingOutInterceptor.setPrettyLogging(true);
        return loggingOutInterceptor;
    }

    @Bean
    public LoggingInInterceptor loggingInInterceptor() {
        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
        loggingInInterceptor.setPrettyLogging(true);
        return loggingInInterceptor;
    }

    private void setSystemProperties(String stsUrl, String systemuserUsername, String systemuserPwd) {
        System.setProperty("no.nav.modig.security.sts.url", stsUrl);
        System.setProperty("no.nav.modig.security.systemuser.username", systemuserUsername);
        System.setProperty("no.nav.modig.security.systemuser.password", systemuserPwd);
    }
}
