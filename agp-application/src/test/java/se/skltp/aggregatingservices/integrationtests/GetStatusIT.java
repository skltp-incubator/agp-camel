package se.skltp.aggregatingservices.integrationtests;

import static org.junit.Assert.assertTrue;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import se.skltp.aggregatingservices.AgpApplication;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = AgpApplication.class)
@TestPropertySource("classpath:application.properties")
public class GetStatusIT {

  @Produce
  protected ProducerTemplate producerTemplate;

  @Autowired
  BuildProperties buildProperties;

  @Test
  public void getStatusResponseTest() {

    String name = buildProperties.getName();
    String version = buildProperties.getVersion();

    String statusResponse = producerTemplate.requestBody("jetty://{{agp.status.url}}", "body", String.class);
    assertTrue (statusResponse .startsWith("{") && statusResponse .endsWith("}"));
    assertTrue (statusResponse.contains("Name\" : \"" + name));
    assertTrue (statusResponse.contains("Version\" : \"" + version));
    assertTrue (statusResponse.contains("ServiceStatus\" : \"Started"));
  }

}

