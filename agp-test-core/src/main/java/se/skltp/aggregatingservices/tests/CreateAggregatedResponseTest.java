package se.skltp.aggregatingservices.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.message.MessageContentsList;
import org.junit.Test;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;
import se.skltp.aggregatingservices.data.TestDataGenerator;
import se.skltp.aggregatingservices.utility.RequestListUtil;

public abstract class CreateAggregatedResponseTest {

  private AgpServiceFactory agpServiceFactory;
  private TestDataGenerator testDataGenerator ;

  private static String patientId1 = "121212121212";
  private static String patientId2 = "198611062384";

  private String producer4 = "HSA-ID-4";
  private String producer5 = "HSA-ID-5";
  private String producer6 = "HSA-ID-6";

  private String producer2 = "HSA-ID-2";

  public CreateAggregatedResponseTest(TestDataGenerator testDataGenerator, AgpServiceFactory agpServiceFactory, AgpServiceConfiguration configuration){
    this.testDataGenerator = testDataGenerator;
    this.agpServiceFactory = agpServiceFactory;
    this.agpServiceFactory.setAgpServiceConfiguration(configuration);
  }

  @Test
  public void testCreateAggregatedResponse(){

    List<MessageContentsList> listOfResponsesFromAllProducers = new ArrayList<>();
    listOfResponsesFromAllProducers.add(RequestListUtil.createRequest(testDataGenerator
        .retrieveFromDb(producer4, patientId1)));
    listOfResponsesFromAllProducers.add(RequestListUtil.createRequest(testDataGenerator
        .retrieveFromDb(producer5, patientId1)));
    listOfResponsesFromAllProducers.add(RequestListUtil.createRequest(testDataGenerator
        .retrieveFromDb(producer6, patientId1)));

    MessageContentsList originalRequest = RequestListUtil.createRequest("logiskAdress", testDataGenerator
        .createRequest(patientId1, null));

    int responseSize = getResponseSize(agpServiceFactory.createAggregatedResponseObject(originalRequest, listOfResponsesFromAllProducers));

    assertEquals(3, responseSize);
  }

  @Test
  public void testCreateAggregatedResponseWithMultipleResponseFromOneProducer(){
    List<MessageContentsList> listOfResponsesFromAllProducers = new ArrayList<>();
    listOfResponsesFromAllProducers.add(RequestListUtil.createRequest(testDataGenerator
        .retrieveFromDb(producer2, patientId2)));


    MessageContentsList originalRequest = RequestListUtil.createRequest("logiskAdress", testDataGenerator
        .createRequest(patientId2, null));


    int responseSize = getResponseSize(agpServiceFactory.createAggregatedResponseObject(originalRequest, listOfResponsesFromAllProducers));
    assertEquals(2, responseSize);
  }

  public abstract int getResponseSize(Object response);

}
