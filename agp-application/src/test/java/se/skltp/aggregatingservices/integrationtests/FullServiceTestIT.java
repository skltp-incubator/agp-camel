package se.skltp.aggregatingservices.integrationtests;

import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_FAULT_INVALID_ID;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS;
import static se.skltp.aggregatingservices.utils.AssertLoggingUtil.assertLogging;
import static se.skltp.aggregatingservices.utils.AssertUtil.assertExpectedProcessingStatus;
import static se.skltp.aggregatingservices.utils.AssertUtil.assertExpectedResponse;

import org.apache.camel.test.spring.CamelSpringBootRunner;
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

  @Test
  public void testOneProducerReturnsTwoHitsNoErrors() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-1", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-2", 2, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-3", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");

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


}


