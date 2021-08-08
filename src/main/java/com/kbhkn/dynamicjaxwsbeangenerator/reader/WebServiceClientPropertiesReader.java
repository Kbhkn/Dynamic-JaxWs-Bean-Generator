package com.kbhkn.dynamicjaxwsbeangenerator.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * It contains properties definitions for the JaxWS Web Services.
 *
 * @author Hakan KABASAKAL, 08-Aug-21
 */
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "ws.jaxws")
public class WebServiceClientPropertiesReader {
    private final Map<String, WebServiceDefinitions> definitions = new HashMap<>();

    /**
     * Generic web service definition.
     */
    @Getter
    @Setter
    public static class WebServiceDefinitions {
        @NotBlank(message = "Web-Service portName isn't defined.")
        private String portName;

        @NotBlank(message = "Web-Service serviceName isn't defined.")
        private String serviceName;

        @NotBlank(message = "Web-Service namespaceUri isn't defined.")
        private String namespaceUri;

        @NotBlank(message = "Web-Service endpointAddress isn't defined.")
        private String endpointAddress;

        @NotBlank(message = "Web-Service serviceInterface(interface package address of service) isn't defined.")
        private String serviceInterface;

        private WebServiceHeaderPropertiesDefinitions properties;
    }

    /**
     * It contains key-value pairs that should add to the header.
     * It is not necessarily for every web service, and it can be added as needed.
     */
    @Getter
    @Setter
    public static class WebServiceHeaderPropertiesDefinitions {
        private List<KeyValuePair> keyValues;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class KeyValuePair {
        private String key;
        private String value;
    }
}
