package com.kbhkn.dynamicjaxwsbeangenerator.factorypostprocessor;

import com.kbhkn.dynamicjaxwsbeangenerator.reader.WebServiceClientPropertiesReader;
import com.kbhkn.dynamicjaxwsbeangenerator.ws.header.ExternalWebServiceLoggerHandler;
import com.kbhkn.dynamicjaxwsbeangenerator.ws.header.HeaderHandler;
import com.kbhkn.dynamicjaxwsbeangenerator.ws.header.MiddlewareHandlerResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.ws.handler.Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;

/**
 * Creates JaxWS Port Proxy Factory beans.
 *
 * @author Hakan KABASAKAL, 08-Aug-21
 */
@Slf4j
@Configuration
public class DynamicBeanGeneratorForJaxWSPortProxyFactoryBean {
    /**
     * Creates JaxWsPortProxyFactory beans.
     *
     * @param env Environment.
     * @return created beans.
     */
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor(Environment env) {
        return configurableListableBeanFactory -> {
            WebServiceClientPropertiesReader propReader = Binder.get(env)
                .bind("ws.jaxws", WebServiceClientPropertiesReader.class)
                .orElseThrow(() -> new RuntimeException("Can't fetched definitions for ws.jaxws.definitions."));

            for (Map.Entry<String, WebServiceClientPropertiesReader.WebServiceDefinitions> ws : propReader.getDefinitions().entrySet()) {
                WebServiceClientPropertiesReader.WebServiceDefinitions definition = ws.getValue();
                String webServiceName = ws.getKey();

                boolean canAdd = true;
                try {
                    //Check that the web service has been created.
                    Class.forName(definition.getServiceInterface());
                } catch (ClassNotFoundException e) {
                    canAdd = false;
                    log.error(
                        "{} could not be created. The web-service classes are either not generated from wsdl or serviceInterface is incorrect.",
                        webServiceName);
                }

                if (canAdd) {
                    BeanDefinition jaxWsPortProxyFactoryBean = createJaxWsPortProxyFactoryBean(definition);

                    ((BeanDefinitionRegistry) configurableListableBeanFactory)
                        .registerBeanDefinition(webServiceName, jaxWsPortProxyFactoryBean);

                    log.info("Added WebService Definition, BeanName: {}", webServiceName);
                }
            }
        };
    }

    private static BeanDefinition createJaxWsPortProxyFactoryBean(WebServiceClientPropertiesReader.WebServiceDefinitions def) {
        return BeanDefinitionBuilder.rootBeanDefinition(JaxWsPortProxyFactoryBean.class)
            .setLazyInit(true)
            .addPropertyValue("serviceInterface", def.getServiceInterface())
            .addPropertyValue("serviceName", def.getServiceName())
            .addPropertyValue("portName", def.getPortName())
            .addPropertyValue("namespaceUri", def.getNamespaceUri())
            .addPropertyValue("endpointAddress", def.getEndpointAddress())
            .addPropertyValue("lookupServiceOnStartup", false)
            .addPropertyValue("handlerResolver", getHandlerResolver(def.getProperties())).getBeanDefinition();
    }

    @SuppressWarnings("rawtypes")
    private static MiddlewareHandlerResolver getHandlerResolver(WebServiceClientPropertiesReader.WebServiceHeaderPropertiesDefinitions properties) {
        MiddlewareHandlerResolver resolver = new MiddlewareHandlerResolver();

        List<Handler> handlerList = new ArrayList<>();
        handlerList.add(new ExternalWebServiceLoggerHandler());

        if (Objects.nonNull(properties)) {
            handlerList.add(new HeaderHandler(properties));
        }

        resolver.setHandlerList(handlerList);

        return resolver;
    }
}
