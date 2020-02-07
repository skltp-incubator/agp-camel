package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;

import java.util.Iterator;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.aggregatingservices.service.TakCacheService;

@Service
@Log4j2
public class CreateRequestListProcessor implements Processor {

  @Autowired
  TakCacheService takCacheService;

  @Override
  public void process(Exchange exchange) throws Exception {

    MessageContentsList originalQuery = exchange.getProperty(AGP_ORIGINAL_QUERY, MessageContentsList.class);
    AgpServiceFactory agpServiceProcessor =  exchange.getProperty(AGP_SERVICE_HANDLER, AgpServiceFactory.class);

      MessageContentsList findContentMessageList = exchange.getIn().getBody(MessageContentsList.class);
      FindContentResponseType findContentResponseType = (FindContentResponseType)findContentMessageList.get(0);

      // TODO fix TakCache init and uncomment this line
//      filterFindContentResponseBasedOnAuthority( findContentResponseType, "sender-id",  "originalsender" );

      List<MessageContentsList> queryObjects = agpServiceProcessor.createRequestList(originalQuery, findContentResponseType);
      exchange.getIn().setBody(queryObjects);

  }

  protected void filterFindContentResponseBasedOnAuthority(FindContentResponseType eiResp, String senderId, String originalServiceConsumerId) {
    Iterator<EngagementType> iterator = eiResp.getEngagement().iterator();

    while (iterator.hasNext()) {
      EngagementType engagementType = iterator.next();

      // TODO Fix targetNamespace parameter
      if (!takCacheService.isAuthorizedConsumer(originalServiceConsumerId, senderId, engagementType.getLogicalAddress(), "todo")) {
        log.info(
            "Source system: senderId {} / originalServiceConsumerId {} is not authorized to access EngagementType:{} dispatched by FindContent",
            new Object[]{senderId, originalServiceConsumerId, engagementType.getLogicalAddress()});
        iterator.remove();
      }
    }
  }
}
