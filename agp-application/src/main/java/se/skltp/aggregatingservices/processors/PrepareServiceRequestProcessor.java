package se.skltp.aggregatingservices.processors;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PrepareServiceRequestProcessor implements Processor {

  public static final String HEADER_LOGICAL_ADDRESS = "LogicalAddress";
  public static final String HEADER_NS = "urn:riv:itintegration:registry:1";

  @Override
  public void process(Exchange exchange) throws Exception {
    log.info(">> prepareRequest");
    MessageContentsList messageContentsList = exchange.getIn().getBody(MessageContentsList.class);
    exchange.setProperty("LogicalAddress", messageContentsList.get(0));
  }

}
