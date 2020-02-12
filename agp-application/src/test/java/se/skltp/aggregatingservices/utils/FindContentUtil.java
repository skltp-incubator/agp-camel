package se.skltp.aggregatingservices.utils;

import org.apache.cxf.message.MessageContentsList;
import se.skltp.aggregatingservices.data.FindContentTestData;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;

public class FindContentUtil {

  static FindContentTestData findContentTestData = new FindContentTestData();

  public static FindContentResponseType createFindContentResponse(String patient) {
    findContentTestData.generateResponseMap();
    return findContentTestData.getResponseForPatient(patient);
  }

  public static MessageContentsList createMessageContentsList(String patient) {
    MessageContentsList messageContentsList = new MessageContentsList();
    messageContentsList.set(0,  createFindContentResponse(patient));
    return messageContentsList;
  }

}
