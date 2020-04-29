package se.skltp.aggregatingservices.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_FAULT_INVALID_ID;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import riv.clinicalprocess.healthcond.actoutcome._4.LaboratoryOrderOutcomeType;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeResponseType;
import se.skltp.aggregatingservices.AgpApplication;
import se.skltp.aggregatingservices.consumer.ConsumerService;
import se.skltp.aggregatingservices.route.ProducerBaseRoute;
import se.skltp.aggregatingservices.utils.ExpectedResponse;
import se.skltp.aggregatingservices.utils.ServiceResponse;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = AgpApplication.class)
public class FullServiceTestIT {

  @Autowired
  ProducerBaseRoute producerBaseRoute;

  @Autowired
  ConsumerService consumerService;

  @Test
  public void testThreeHitsDifferentProducersNoErrors() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-4", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-5", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-6", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService.callService(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_MANY_HITS_NO_ERRORS);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
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
  }


  //
  // TC5 - Patient that causes an exception in the source system
  //
  @Test
  public void testOneProducerReturnsSoapFault() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-1", 0, StatusCodeEnum.NO_DATA_SYNCH_FAILED, "Invalid Id: " + TEST_RR_ID_FAULT_INVALID_ID);

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService.callService(TEST_RR_ID_FAULT_INVALID_ID);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_FAULT_INVALID_ID);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
  }

  private void assertExpectedResponse(ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response, ExpectedResponse expectedResponse,
      String patientId) {

    assertEquals("Not expected response code", expectedResponse.getResponseCode(), response.getResponseCode() );

    assertEquals("GetLaboratoryOrderOutcome does not have expected size", expectedResponse.numResponses(),
        response.getObject().getLaboratoryOrderOutcome().size());

    for (LaboratoryOrderOutcomeType responseElement : response.getObject().getLaboratoryOrderOutcome()) {
      String systemId = responseElement.getLaboratoryOrderOutcomeHeader().getSource().getSystemId().getRoot();
      assertTrue(String.format("%s wasn't expected in response", systemId), expectedResponse.contains(systemId));
      assertEquals(patientId,
          responseElement.getLaboratoryOrderOutcomeHeader().getAccessControlHeader().getPatient().getId().get(0).getRoot());
    }
  }

  private void assertExpectedProcessingStatus(ProcessingStatusType processingStatusType, ExpectedResponse expectedResponse) {

    assertEquals("ProcessingStatus does not have expected size", expectedResponse.numProducers(),
        processingStatusType.getProcessingStatusList().size());

    for (ProcessingStatusRecordType processingStatus : processingStatusType.getProcessingStatusList()) {
      String logicalAddress = processingStatus.getLogicalAddress();

      assertTrue(String.format("%s wasn't expected in ProcessingStatus", logicalAddress),
          expectedResponse.contains(logicalAddress));

      assertEquals(expectedResponse.getStatusCode(logicalAddress), processingStatus.getStatusCode());
      if (processingStatus.getStatusCode() == StatusCodeEnum.NO_DATA_SYNCH_FAILED) {
        final String errTxtPart = expectedResponse.getErrTxtPart(logicalAddress);
        String errTxt = processingStatus.getLastUnsuccessfulSynchError().getText();
        String errCode = processingStatus.getLastUnsuccessfulSynchError().getCode();

        assertTrue(String.format("Error txt: %s\n Does not contain:\n  %s ", errTxt, errTxtPart),
            errTxt.contains(errTxtPart));

        assertNotNull("errorCode should not be null", errCode);
      }

    }
  }

}
