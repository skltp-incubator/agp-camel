package se.skltp.aggregatingservices.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.apache.cxf.message.MessageContentsList;
import org.junit.Test;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;
import se.skltp.aggregatingservices.data.FindContentTestData;
import se.skltp.aggregatingservices.data.TestDataGenerator;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.utility.RequestListUtil;

public abstract class CreateRequestListTest {

  private FindContentTestData eiResponseDataHelper  = new FindContentTestData();

  private TestDataGenerator testDataGenerator ;
  private AgpServiceFactory agpServiceFactory;

  public static final String LOGISK_ADRESS = "logiskAdress";
  private static final String PATIENT_ID_1 = "121212121212";
  private static final String PATIENT_ID_2 = "198611062384";
  private static final String HSA_ID_2 = "HSA-ID-2";

  public CreateRequestListTest(TestDataGenerator testDataGenerator, AgpServiceFactory agpServiceFactory, AgpServiceConfiguration configuration){
    this.testDataGenerator = testDataGenerator;
    this.agpServiceFactory = agpServiceFactory;
    this.agpServiceFactory.setAgpServiceConfiguration(configuration);
  }

  @Test
  public void testCreateRequestListAllProducers(){
    MessageContentsList messageContentsList = RequestListUtil
        .createRequest(LOGISK_ADRESS, testDataGenerator.createRequest(PATIENT_ID_1, null));
    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(PATIENT_ID_1);


    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);
    assertEquals(3, requestList.size());
  }

  @Test
  public void testCreateRequestListConcreteProducer(){
    MessageContentsList messageContentsList = RequestListUtil.createRequest(LOGISK_ADRESS, testDataGenerator.createRequest(
        PATIENT_ID_2, HSA_ID_2));
    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(PATIENT_ID_2);


    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);

    assertEquals(1, requestList.size());
  }

  @Test
  public void testCreateRequestListNoDataInEIForThisProducer(){
    MessageContentsList messageContentsList = RequestListUtil.createRequest(LOGISK_ADRESS, testDataGenerator.createRequest(
        PATIENT_ID_1, HSA_ID_2));

    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(PATIENT_ID_1);

    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);

    assertEquals(0, requestList.size());
  }

}
