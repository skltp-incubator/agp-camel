package se.skltp.aggregatingservices.api;

import java.util.List;
import org.apache.cxf.message.MessageContentsList;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;

public interface AgpServiceFactory<E, T> {

  public FindContentType createFindContent(MessageContentsList queryObject);
  public List<MessageContentsList> createRequestList(MessageContentsList queryObject, FindContentResponseType src);
  public T createAggregatedResponseObject(MessageContentsList queryObject, List<MessageContentsList> aggregatedResponseList);

  default public List<MessageContentsList> createRequestList(MessageContentsList queryObject, List<String> logicalAddresses){
    return null;
  }

}
