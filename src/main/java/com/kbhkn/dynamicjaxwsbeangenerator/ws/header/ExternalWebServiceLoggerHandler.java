package com.kbhkn.dynamicjaxwsbeangenerator.ws.header;

import com.kbhkn.dynamicjaxwsbeangenerator.ws.util.WsLoggingUtils;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;

/**
 * Works like an interceptor for soap services.
 * Also, it puts service-name, operation-name, and the endpoint-url to the MDC.
 *
 * @author Hakan KABASAKAL, 08-Aug-21
 */
public class ExternalWebServiceLoggerHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext ctx) {
        boolean request = (Boolean) ctx.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (request) {
            MDC.put("EXT_SERVICE_NAME", ((QName) ctx.get(SOAPMessageContext.WSDL_SERVICE)).getLocalPart());
            MDC.put("EXT_OPERATION_NAME", ((QName) ctx.get(SOAPMessageContext.WSDL_OPERATION)).getLocalPart());
            MDC.put("EXT_ENDPOINT_URL", String.valueOf(ctx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY)));
        }

        WsLoggingUtils.logMessage(ctx.getMessage(), request, false, MarkerFactory.getMarker("externalWs"));

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        WsLoggingUtils.logMessage(context.getMessage(), false, true, MarkerFactory.getMarker("externalWs"));

        return true;
    }

    @Override
    public void close(MessageContext context) {
        MDC.remove("EXT_SERVICE_NAME");
        MDC.remove("EXT_OPERATION_NAME");
        MDC.remove("EXT_ENDPOINT_URL");
    }

    @Override
    public Set<QName> getHeaders() {
        return new HashSet<>();
    }
}

