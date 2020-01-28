package se.skltp.aggregatingservices;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;

@Log4j2
public abstract class AgServiceFactoryBase<E,T> implements AgpServiceFactory<E,T> {

  public abstract FindContentType createFindContent(E queryObject);

  public abstract T getXmlFromAggregatedResponse(E query, List<MessageContentsList> aggregatedResponseList);


  @Override
  public FindContentType createFindContent(MessageContentsList messageContentsList) {
    return createFindContent((E)messageContentsList.get(1));
  }

  @Override
  public T createAggregatedResponseObject(MessageContentsList originalQery, List<MessageContentsList> aggregatedResponseList) {
    return getXmlFromAggregatedResponse((E)originalQery.get(1), aggregatedResponseList);
  }


}
