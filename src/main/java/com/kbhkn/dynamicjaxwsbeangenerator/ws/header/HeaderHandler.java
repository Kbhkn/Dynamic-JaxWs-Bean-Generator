package com.kbhkn.dynamicjaxwsbeangenerator.ws.header;

import com.kbhkn.dynamicjaxwsbeangenerator.reader.WebServiceClientPropertiesReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

/**
 * Puts key-value pairs that should add to the header.
 *
 * @author Hakan KABASAKAL, 08-Aug-21
 */
@Slf4j
public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {
    private static final String SOAP_PREFIX = "soap";

    private final WebServiceClientPropertiesReader.WebServiceHeaderPropertiesDefinitions headerProperties;

    public HeaderHandler(WebServiceClientPropertiesReader.WebServiceHeaderPropertiesDefinitions headerProperties) {
        this.headerProperties = headerProperties;
    }

    /**
     * Add header to the soap message.
     *
     * @param soapMessageContext soap message.
     * @return result.
     */
    public boolean handleMessage(SOAPMessageContext soapMessageContext) {

        Boolean outboundProperty = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (Boolean.TRUE.equals(outboundProperty)) {
            try {

                SOAPEnvelope envelope = soapMessageContext.getMessage().getSOAPPart().getEnvelope();
                envelope.setPrefix(SOAP_PREFIX);

                SOAPHeader header = envelope.getHeader();
                if (Objects.isNull(header)) {
                    header = envelope.addHeader();
                }

                header.setPrefix(SOAP_PREFIX);

                if (!ObjectUtils.isEmpty(headerProperties.getUsername())) {
                    setSecurity(header);
                }

                for (WebServiceClientPropertiesReader.KeyValuePair keyValue : headerProperties.getKeyValues()) {
                    SOAPElement element = header.addChildElement(keyValue.getKey());
                    element.addTextNode(keyValue.getValue());
                }
            } catch (Exception e) {
                log.error("Header Handler Exc", e);
            }

        }

        return outboundProperty;

    }

    private void setSecurity(SOAPHeader header) throws SOAPException {
        SOAPElement security = header.addChildElement("Security", "wsse", "http://tempuri.org/UsernameText");

        SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
        SOAPElement u = usernameToken.addChildElement("Username", "wsse");
        u.addTextNode(headerProperties.getUsername());

        SOAPElement pw = usernameToken.addChildElement("Password", "wsse");
        pw.setAttribute("Type", "http://tempuri.org/#PasswordText");
        pw.addTextNode(headerProperties.getPassword());
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {
        //there is no need to do anything
    }

    @Override
    public Set<QName> getHeaders() {
        return new HashSet<>();
    }
}

