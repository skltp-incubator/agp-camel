package se.skltp.aggregatingservices.tests;

import static org.junit.Assert.assertEquals;

import org.apache.cxf.message.MessageContentsList;
import org.junit.Test;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;
import se.skltp.aggregatingservices.data.TestDataGenerator;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.aggregatingservices.utility.RequestListUtil;

public abstract class CreateFindContentTest {

  private static final String PATIENT_ID = "121212121212";

  private AgpServiceFactory agpServiceFactory;
  private AgpServiceConfiguration configuration;

  private TestDataGenerator testDataGenerator;

  public CreateFindContentTest(TestDataGenerator testDataGenerator, AgpServiceFactory agpServiceFactory, AgpServiceConfiguration configuration){
    this.testDataGenerator = testDataGenerator;
    this.agpServiceFactory = agpServiceFactory;
    this.agpServiceFactory.setAgpServiceConfiguration(configuration);
    this.configuration = configuration;
  }


  @Test
  public void testCreateFindContent(){
    MessageContentsList messageContentsList = RequestListUtil.createRequest("logiskAdress", testDataGenerator
        .createRequest(PATIENT_ID, null));

    FindContentType type = agpServiceFactory.createFindContent(messageContentsList);

    assertEquals(configuration.getEiCategorization(), type.getCategorization());
    assertEquals(configuration.getEiServiceDomain(), type.getServiceDomain());
    assertEquals(PATIENT_ID, type.getRegisteredResidentIdentification());
  }

}
