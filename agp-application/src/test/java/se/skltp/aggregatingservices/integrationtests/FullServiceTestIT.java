package se.skltp.aggregatingservices.integrationtests;

import static org.apache.camel.test.junit4.TestSupport.assertStringContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_ID_FAULT_INVALID_ID_IN_EI;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_ID_FAULT_TIMEOUT_IN_EI;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_EJ_SAMVERKAN_I_TAK;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_FAULT_INVALID_ID;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_ONE_HIT;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_ZERO_HITS;
import static se.skltp.aggregatingservices.utils.AssertLoggingUtil.LOGGER_NAME_ERROR_OUT;
import static se.skltp.aggregatingservices.utils.AssertLoggingUtil.assertEventMessageCommon;
import static se.skltp.aggregatingservices.utils.AssertLoggingUtil.assertLogging;
import static se.skltp.aggregatingservices.utils.AssertUtil.assertExpectedProcessingStatus;
import static se.skltp.aggregatingservices.utils.AssertUtil.assertExpectedResponse;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.cxf.binding.soap.SoapFault;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeResponseType;
import se.skltp.aggregatingservices.AgpApplication;
import se.skltp.aggregatingservices.consumer.ConsumerService;
import se.skltp.aggregatingservices.route.ProducerBaseRoute;
import se.skltp.aggregatingservices.utils.ExpectedResponse;
import se.skltp.aggregatingservices.utils.ServiceResponse;
import se.skltp.aggregatingservices.utils.TestLogAppender;
import se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = AgpApplication.class)
public class FullServiceTestIT {

  @Autowired
  ProducerBaseRoute producerBaseRoute;

  @Autowired
  ConsumerService consumerService;

  @Autowired
  TestLogAppender testLogAppender;

  @Before
  public void before() {

  }

  //
  // TC1 - Get data from 3 producers
  //
  @Test
  public void testThreeHitsDifferentProducersNoErrors() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-4", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-5", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-6", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService
        .callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_MANY_HITS_NO_ERRORS);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
    assertLogging(testLogAppender, expectedResponse);
  }

  //
  // TC2 - Test person has no engagements
  //
  @Test
  public void testNoEngagementForTestPerson() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService
        .callService(TEST_RR_ID_ZERO_HITS);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_ZERO_HITS);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
    assertLogging(testLogAppender, expectedResponse);
  }


  //
  // TC3 - Empty response from one system, and one response from one system
  //
  @Test
  public void testOneEmptyResponseAndOneResponse() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-1", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-2", 0, StatusCodeEnum.DATA_FROM_SOURCE, "");

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService.callService(TEST_RR_ID_ONE_HIT);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_ONE_HIT);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
    assertLogging(testLogAppender, expectedResponse);
  }

  //
  // TC4 - Response from two source systems, timeout from one source system
  //
  @Test
  public void testOneProducerReturnsTwoHitsNoErrors() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-1", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-2", 2, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-3", 0, StatusCodeEnum.NO_DATA_SYNCH_FAILED, "Read timed out");

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService.callService(TEST_RR_ID_MANY_HITS);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_MANY_HITS);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
    assertLogging(testLogAppender, expectedResponse);
  }


  //
  // TC5 - Patient that causes an exception in the source system
  //
  @Test
  public void testOneProducerReturnsSoapFault() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-1", 0, StatusCodeEnum.NO_DATA_SYNCH_FAILED, "Invalid Id: " + TEST_RR_ID_FAULT_INVALID_ID, 500);

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService
        .callService(TEST_RR_ID_FAULT_INVALID_ID);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_FAULT_INVALID_ID);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
    assertLogging(testLogAppender, expectedResponse);
  }

  //
  // TC6 - Authorization missing for LogicalAddress
  //
  @Test
  public void testAuthorizationMissingForOneLogicalAddress() throws Exception {
    // A OK response with no engagements expected
    ExpectedResponse expectedResponse = new ExpectedResponse();

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService
        .callService(TEST_RR_ID_EJ_SAMVERKAN_I_TAK);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_EJ_SAMVERKAN_I_TAK);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
    assertLogging(testLogAppender, expectedResponse);
  }

  //
  // FindContent timeout should give a soap fault
  //
  @Test
  public void testFindContentTimeoutShouldReturnSoapFault() throws Exception {

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService
        .callService(TEST_ID_FAULT_TIMEOUT_IN_EI);

    assertEquals("Not expected response code", 500, response.getResponseCode());

    final SoapFault soapFault = response.getSoapFault();
    assertNotNull("Expected a SoapFault", soapFault);
    assertEquals("Read timed out", soapFault.getReason());

    final String eventMessage = testLogAppender.getEventMessage(LOGGER_NAME_ERROR_OUT, 0);
    assertEventMessageCommon(eventMessage, "error-out");
    assertStringContains(eventMessage, "-responseCode=500");
  }

  //
  // FindContent error should give a soap fault
  //
  @Test
  public void testFindContentErrorShouldReturnSoapFault() throws Exception {
    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService
        .callService(TEST_ID_FAULT_INVALID_ID_IN_EI);

    assertEquals("Not expected response code", 500, response.getResponseCode());

    final SoapFault soapFault = response.getSoapFault();
    assertNotNull("Expected a SoapFault", soapFault);
    assertEquals("Invalid Id: EI:INV_ID", soapFault.getReason());

    final String eventMessage = testLogAppender.getEventMessage(LOGGER_NAME_ERROR_OUT, 0);
    assertEventMessageCommon(eventMessage, "error-out");
    assertStringContains(eventMessage, "-responseCode=500");
  }

}


