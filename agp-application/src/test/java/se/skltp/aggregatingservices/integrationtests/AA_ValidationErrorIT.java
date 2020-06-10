package se.skltp.aggregatingservices.integrationtests;

import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_ONE_FORMAT_ERROR;
import static se.skltp.aggregatingservices.utils.AssertUtil.assertExpectedProcessingStatus;
import static se.skltp.aggregatingservices.utils.AssertUtil.assertExpectedResponse;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeResponseType;
import se.skltp.aggregatingservices.AgpApplication;
import se.skltp.aggregatingservices.consumer.ConsumerService;
import se.skltp.aggregatingservices.utils.ExpectedResponse;
import se.skltp.aggregatingservices.utils.ServiceResponse;
import se.skltp.aggregatingservices.utils.TestLogAppender;
import se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = AgpApplication.class, properties = {
    "getaggregatedlaboratoryorderoutcome.v4.enableSchemaValidation=true"
   })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AA_ValidationErrorIT {

  @Autowired
  ConsumerService consumerService;

  @Autowired
  TestLogAppender testLogAppender;

  @Before
  public void before() {

  }

  //
  // TC8 - One ok response, second response contains field with format error
  //      Should fail Ok when schema validation is enabled
  //
  @Test
  public void testFormatErrorNotAccepted() throws Exception {
    ExpectedResponse expectedResponse = new ExpectedResponse();
    expectedResponse.add("HSA-ID-4", 1, StatusCodeEnum.DATA_FROM_SOURCE, "");
    expectedResponse.add("HSA-ID-5", 0, StatusCodeEnum.NO_DATA_SYNCH_FAILED, ": Value '1895' is not facet-valid with respect to pattern");

    final ServiceResponse<GetLaboratoryOrderOutcomeResponseType> response = consumerService
        .callService(TEST_RR_ID_ONE_FORMAT_ERROR);

    assertExpectedResponse(response, expectedResponse, TEST_RR_ID_ONE_FORMAT_ERROR);
    assertExpectedProcessingStatus(response.getProcessingStatus(), expectedResponse);
  }
}
