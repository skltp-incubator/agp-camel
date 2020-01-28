package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;

@Service
@Log4j2
public class CreateFindContentProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    final MessageContentsList originalQueryMessageList = exchange.getIn().getBody(MessageContentsList.class);

    MessageContentsList findContent = createFindContentMessageList(exchange, originalQueryMessageList);
    exchange.getIn().setBody(findContent);
  }

  private MessageContentsList createFindContentMessageList(Exchange exchange, MessageContentsList originalQueryMessageList) {
    AgpServiceFactory agpServiceProcessor =  exchange.getProperty(AGP_SERVICE_HANDLER, AgpServiceFactory.class);
    FindContentType findContent = agpServiceProcessor.createFindContent(originalQueryMessageList);
    MessageContentsList findContentMessageList = new MessageContentsList();
    // TODO läs in logisk address från config
    findContentMessageList.add("Logisk address");
    findContentMessageList.add(findContent);
    return findContentMessageList;
  }

}
