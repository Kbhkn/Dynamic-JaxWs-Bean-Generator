package com.kbhkn.dynamicjaxwsbeangenerator.ws.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Common web service client's logger.
 *
 * @author Hakan KABASAKAL, 08-Aug-21
 */
@Slf4j
public class WsLoggingUtils {
    private static final String REQUEST_PREFIX = "req:";
    private static final String RESPONSE_PREFIX = "res:";
    private static final String FAULT_PREFIX = "fault:";
    private static final String MASK_MESSAGE = "[Logging Disabled]";

    /**
     * It contains key names in the header that it should mask.
     * TODO hakan; This list can manage dynamically, but I didn't need it.
     */
    private static final List<String> NAMES_TO_BE_MASKED = List.of("contentAsStr", "content", "Password");

    private WsLoggingUtils() {
        throw new AssertionError("You should access the UtilityClass methods statically.");
    }

    /**
     * write formatted log.
     *
     * @param msg     soap message.
     * @param request type.
     * @param fault   error.
     * @param marker  writer.
     */
    public static void logMessage(SOAPMessage msg, boolean request, boolean fault, Marker marker) {
        StringBuilder sb = new StringBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            msg.writeTo(baos);
            String rawMessage = baos.toString(StandardCharsets.UTF_8);

            if (fault) {
                sb.append(FAULT_PREFIX);
                sb.append(rawMessage);
                log.error(marker, sb.toString());
            } else {
                sb.append(request ? REQUEST_PREFIX : RESPONSE_PREFIX);
                List<String> nodesToMask = new ArrayList<>();

                for (String prop : NAMES_TO_BE_MASKED) {
                    if (rawMessage.contains(prop)) {
                        nodesToMask.add(prop);
                    }
                }

                sb.append(maskMessage(rawMessage, nodesToMask).replaceAll("(\\t|\\r?\\n)+", ""));
                log.info(marker, sb.toString());
            }
        } catch (Exception e) {
            log.error(marker, "Error while logging ws call", e);
        }
    }

    private static String maskMessage(String xmlMsg, List<String> tagsToMask)
        throws SAXException, IOException, ParserConfigurationException,
        XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {

        if (tagsToMask.isEmpty()) {
            return xmlMsg;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlMsg.getBytes(StandardCharsets.UTF_8)));

        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath path = pathFactory.newXPath();

        for (String tagValue : tagsToMask) {
            XPathExpression expression = path.compile("//*[local-name()='" + tagValue + "']");
            NodeList nodesToMask = (NodeList) expression.evaluate(document, javax.xml.xpath.XPathConstants.NODESET);
            if (nodesToMask != null) {
                for (int i = 0; i < nodesToMask.getLength(); i++) {
                    Node nodeToMask = nodesToMask.item(i);
                    if (nodeToMask != null) {
                        nodeToMask.setTextContent(MASK_MESSAGE);
                    }
                }
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        Transformer tf = transformerFactory.newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");

        Writer out = new StringWriter();
        tf.transform(new DOMSource(document), new StreamResult(out));

        return out.toString();
    }
}

