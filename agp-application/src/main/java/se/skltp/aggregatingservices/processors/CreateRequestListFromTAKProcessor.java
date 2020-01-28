package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.service.TakCacheService;

@Service
@Log4j2
public class CreateRequestListFromTAKProcessor implements Processor {

  @Autowired
  TakCacheService takCacheService;

  @Override
  public void process(Exchange exchange) throws Exception {

    MessageContentsList originalQuery = exchange.getProperty(AGP_ORIGINAL_QUERY, MessageContentsList.class);
    AgpServiceFactory agpServiceProcessor =  exchange.getProperty(AGP_SERVICE_HANDLER, AgpServiceFactory.class);

    // TODO FIX parameters
    final List<String> receivers = takCacheService.getReceivers("", "", "");

    List<MessageContentsList> queryObjects = agpServiceProcessor.createRequestList(originalQuery, receivers);
    exchange.getIn().setBody(queryObjects);

  }
}
