package no.nav.familie.ks.oppslag.felles.ws;

import no.nav.familie.ks.oppslag.felles.MDCOperations;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class CallIdInInterceptor extends AbstractSoapInterceptor {

    public CallIdInInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage message) {
        if (message.hasHeader(MDCOperations.CALLID_QNAME)) {
            Header header = message.getHeader(MDCOperations.CALLID_QNAME);
            MDCOperations.putCallId(((Element) header.getObject()).getTextContent());
        }
    }
}
