package se.skltp.aggregatingservices.integrationtests;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_RIVTA_ORIGINAL_CONSUMER_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SKLTP_CORRELATION_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_VP_INSTANCE_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_VP_SENDER_ID;
import static se.skltp.aggregatingservices.data.TestDataDefines.SAMPLE_SENDER_ID;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.skltp.aggregatingservices.AgpApplication;
import se.skltp.aggregatingservices.config.VpConfig;
import se.skltp.aggregatingservices.consumer.ConsumerService;
import se.skltp.aggregatingservices.route.FindContentStubRoute;
import se.skltp.aggregatingservices.route.ProducerBaseRoute;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = AgpApplication.class)
public class FullServiceHeadersIT {

  @Autowired
  ProducerBaseRoute producerBaseRoute;

  @Autowired
  FindContentStubRoute findContentStubRoute;

  @Autowired
  ConsumerService consumerService;

  @Autowired
  VpConfig vpConfig;

  @Before
  public void beforeTest() {
    producerBaseRoute.getMock().reset();
    findContentStubRoute.getMock().reset();
  }

  @Test
  public void senderIdForwardedToProducer() throws Exception {

    final MockEndpoint mock = producerBaseRoute.getMock();
    mock.expectedHeaderValuesReceivedInAnyOrder(AGP_VP_SENDER_ID, SAMPLE_SENDER_ID, SAMPLE_SENDER_ID, SAMPLE_SENDER_ID);
    mock.expectedMessageCount(3);

    consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    mock.assertIsSatisfied();
  }

  @Test
  public void originalConsumerIdForwardedToProducer() throws Exception {

    final MockEndpoint mock = producerBaseRoute.getMock();
    mock.expectedHeaderValuesReceivedInAnyOrder(AGP_RIVTA_ORIGINAL_CONSUMER_ID, SAMPLE_SENDER_ID, SAMPLE_SENDER_ID,
        SAMPLE_SENDER_ID);
    mock.expectedMessageCount(3);

    consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    mock.assertIsSatisfied();
  }

  @Test
  public void intanceIdSentToProducer() throws Exception {

    final MockEndpoint mock = producerBaseRoute.getMock();
    mock.expectedHeaderReceived(AGP_VP_INSTANCE_ID, vpConfig.getInstanceId());
    mock.expectedMessageCount(3);

    consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    mock.assertIsSatisfied();
  }


  @Test
  public void correlationIdForwardedToProducer() throws Exception {

    final MockEndpoint mock = producerBaseRoute.getMock();
    mock.expectedHeaderReceived(AGP_SKLTP_CORRELATION_ID, "corr-id");
    mock.expectedMessageCount(3);

    consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    mock.assertIsSatisfied();
  }

  @Test
  public void correlationIdForwardedToEI() throws Exception {

    final MockEndpoint mock = findContentStubRoute.getMock();
    mock.expectedHeaderReceived(AGP_SKLTP_CORRELATION_ID, "corr-id");
    mock.expectedMessageCount(1);

    consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    mock.assertIsSatisfied();
  }

  @Test
  public void instanceIdSentToEI() throws Exception {

    final MockEndpoint mock = findContentStubRoute.getMock();
    mock.expectedHeaderReceived(AGP_VP_INSTANCE_ID, vpConfig.getInstanceId());
    mock.expectedMessageCount(1);

    consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    mock.assertIsSatisfied();
  }

  @Test
  public void platformIdUsedAsSenderIdToEI() throws Exception {

    final MockEndpoint mock = findContentStubRoute.getMock();
    mock.expectedHeaderReceived(AGP_VP_SENDER_ID, vpConfig.getSenderId());
    mock.expectedMessageCount(1);

    consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    mock.assertIsSatisfied();
  }
}