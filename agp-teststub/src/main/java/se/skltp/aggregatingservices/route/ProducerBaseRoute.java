package se.skltp.aggregatingservices.route;

import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.config.TestProducerConfiguration;
import se.skltp.aggregatingservices.data.ProducerTestDataGenerator;
import se.skltp.aggregatingservices.processors.ProducerResponseProcessor;

@Component
public class ProducerBaseRoute extends RouteBuilder {
   private static String SERVICE_CONFIGURATION="cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s";

  List<TestProducerConfiguration> testProducerConfigurations;

  @EndpointInject(uri="mock:producer:input")
  MockEndpoint mock;

  @Autowired
  public ProducerBaseRoute(List<TestProducerConfiguration> testProducerConfigurations) {
    this.testProducerConfigurations = testProducerConfigurations;
  }

  @Override
  public void configure() throws Exception {
    for(TestProducerConfiguration testProducerConfiguration : testProducerConfigurations){
      createProducerRoute(testProducerConfiguration);
    }
  }

  private void createProducerRoute(TestProducerConfiguration configuration){
    String serviceAddress = String.format(SERVICE_CONFIGURATION, configuration.getProducerAddress(),
        configuration.getWsdlPath(), configuration.getServiceClass());

    try {
      final ProducerTestDataGenerator testDataGenerator = createTestDataGenerator(
          configuration.getTestDataGeneratorClass());
      ProducerResponseProcessor producerResponseProcessor = new ProducerResponseProcessor(testDataGenerator);

      from(serviceAddress)
          .to("mock:producer:input")
          .process(producerResponseProcessor);

    } catch (Exception e) {
      log.error("Couldn't create teststub route for {}", configuration.getServiceClass(), e);
    }
  }


  private ProducerTestDataGenerator createTestDataGenerator(String className)
      throws Exception {
    return (ProducerTestDataGenerator) Class.forName(className).getConstructor().newInstance();
  }

  public MockEndpoint getMock() {
    return mock;
  }

}


