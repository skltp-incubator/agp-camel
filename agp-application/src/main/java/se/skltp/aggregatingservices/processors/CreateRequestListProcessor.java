package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_TAK_CONTRACT_NAME;

import java.util.Iterator;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.constants.AgpHeaders;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.aggregatingservices.service.Authority;
import se.skltp.aggregatingservices.service.TakCacheService;

@Service
@Log4j2
public class CreateRequestListProcessor implements Processor {

  @Autowired
  TakCacheService takCacheService;

  @Override
  public void process(Exchange exchange) throws Exception {

    MessageContentsList originalQuery = exchange.getProperty(AGP_ORIGINAL_QUERY, MessageContentsList.class);
    AgpServiceFactory agpServiceProcessor = exchange.getProperty(AGP_SERVICE_HANDLER, AgpServiceFactory.class);

    MessageContentsList findContentMessageList = exchange.getIn().getBody(MessageContentsList.class);
    final FindContentResponseType findContentResponse = (FindContentResponseType) findContentMessageList.get(0);

    filterFindContentResponseBasedOnAuthority(findContentResponse, createAuthorityFromExcange(exchange));

    List<MessageContentsList> queryObjects = agpServiceProcessor.createRequestList(originalQuery, findContentResponse);
    exchange.getIn().setBody(queryObjects);

  }


  protected void filterFindContentResponseBasedOnAuthority(FindContentResponseType eiResp, Authority authority) {
    Iterator<EngagementType> iterator = eiResp.getEngagement().iterator();

    while (iterator.hasNext()) {
      EngagementType engagementType = iterator.next();
      authority.setReceiverId(engagementType.getLogicalAddress());
       if (!takCacheService.isAuthorizedConsumer(authority)) {
        log.info(
            "Source system: senderId {} / originalServiceConsumerId {} is not authorized to access EngagementType:{} dispatched by FindContent",
            new Object[]{authority.getSenderId(), authority.getOriginalSenderId(), authority.getReceiverId()});
        iterator.remove();
      }
    }
  }

  protected Authority createAuthorityFromExcange(Exchange exchange){
    Authority authority = new Authority();
    final Message in = exchange.getIn();
    authority.setSenderId(in.getHeader(AgpHeaders.X_VP_SENDER_ID, String.class));
    authority.setOriginalSenderId(in.getHeader(AgpHeaders.X_RIVTA_ORIGINAL_SERVICE_CONSUMER_HSA_ID, String.class));
    authority.setServicecontractNamespace(exchange.getProperty(AGP_TAK_CONTRACT_NAME, String.class));
    return authority;
  }
}
