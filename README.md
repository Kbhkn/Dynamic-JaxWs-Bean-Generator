## Bean generator for the JaxWSPortProxyFactory

#### Abstract
* It doesn't need anymore to bean definition each one of Soap Services.
* It creates the beans and can mask sensitive data during logs.

* It would be best to add the web service classes as a dependency before using them. Otherwise, it should be created from WSDL.
* Example service definition;

```yaml
ws:
  jaxws:
    definitions:
      testservice:
        portName: TestServicePort
        serviceName: TestService
        namespaceUri: http://test.com/TestService/Service/V1
        endpointAddress: https://test.com/services/Testervice
        serviceInterface: com.kbhkn.ws.testservice.service.v1.TestServicePort
        properties:
          username: Kbhkn
          password: 123456
          keyValues:
            - exampleKey1: exampleValue1
            - exampleKey2: exampleValue2
```
### Guides
The following guides illustrate how to use some features:

* [Docs-1 of Auto Configuration](https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/using-boot-auto-configuration.html)
* [Docs-2 of Auto Configuration](https://www.baeldung.com/spring-boot-custom-auto-configuration)
* [Docs-2 of JaxWsPortProxyFactoryBean](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/remoting/jaxws/JaxWsPortProxyFactoryBean.html)
* [Generate Classes from WSDL](https://www.baeldung.com/maven-wsdl-stubs)