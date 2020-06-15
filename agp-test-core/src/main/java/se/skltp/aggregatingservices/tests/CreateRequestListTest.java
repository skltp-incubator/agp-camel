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

  private static String patientId1 = "121212121212";
  private static String patientId2 = "198611062384";
  private String producer2 = "HSA-ID-2";

  public CreateRequestListTest(TestDataGenerator testDataGenerator, AgpServiceFactory agpServiceFactory, AgpServiceConfiguration configuration){
    this.testDataGenerator = testDataGenerator;
    this.agpServiceFactory = agpServiceFactory;
    this.agpServiceFactory.setAgpServiceConfiguration(configuration);
  }

  @Test
  public void testCreateRequestList_allProducers(){
    MessageContentsList messageContentsList = RequestListUtil
        .createRequest("logiskAdress", testDataGenerator.createRequest(patientId1, null));
    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(patientId1);


    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);
    assertEquals(3, requestList.size());
  }

  @Test
  public void testCreateRequestList_concreteProducer(){
    MessageContentsList messageContentsList = RequestListUtil.createRequest("logiskAdress", testDataGenerator.createRequest(patientId2, producer2));
    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(patientId2);


    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);

    assertEquals(1, requestList.size());
  }

  @Test
  public void testCreateRequestList_noDataInEIForThisProducer(){
    MessageContentsList messageContentsList = RequestListUtil.createRequest("logiskAdress", testDataGenerator.createRequest(patientId1, producer2));

    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(patientId1);

    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);

    assertEquals(0, requestList.size());
  }

}
