package se.skltp.aggregatingservices.processors;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import se.skltp.aggregatingservices.data.ProducerTestDataGenerator;

@Log4j2
public class ProducerResponseProcessor implements Processor {

  ProducerTestDataGenerator producerData;

  public ProducerResponseProcessor(ProducerTestDataGenerator producerData){
    this.producerData = producerData;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    MessageContentsList messageContentsList = exchange.getIn().getBody(MessageContentsList.class);
    Object response = producerData.processRequest(messageContentsList);
    exchange.getIn().setBody(response);
  }
}
