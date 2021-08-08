
## Bean generator for the JaxWSPortProxyFactory

#### Abstract
* It doesn't need anymore to bean definition each one of Soap Services.
* It creates the beans and can mask sensitive data during logs.

* It would be best to add the web service classes as a dependency before using them. Otherwise, it should be created from WSDL.
* I have provided an example service definition:

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
      anotherservice:
        portName: AnotherServicePort
        serviceName: AnotherService
        namespaceUri: http://anothertest.com/AnotherTestService/Service/V1
        endpointAddress: https://anothertest.com/services/AnotherTestService
        serviceInterface: com.kbhkn.ws.anothertestservice.service.v1.AnotherTestService
        properties:
          username: Kbhkn
          password: 123456
          keyValues:
            - exampleKey1: exampleValue1
            - exampleKey2: exampleValue2
```

>You can inject the **com.kbhkn.ws.testservice.service.v1.TestServicePort** interface. </br>
>You can inject the **com.kbhkn.ws.anothertestservice.service.v1.AnotherTestService** interface.

### Guides
The following guides illustrate how to use some features:

* [Docs-1 of Auto Configuration](https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/using-boot-auto-configuration.html)
* [Docs-2 of Auto Configuration](https://www.baeldung.com/spring-boot-custom-auto-configuration)
* [Docs of JaxWsPortProxyFactoryBean](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/remoting/jaxws/JaxWsPortProxyFactoryBean.html)
* [Generate Classes from WSDL](https://www.baeldung.com/maven-wsdl-stubs)
