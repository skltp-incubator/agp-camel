package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UnmarshalQueryProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
     exchange.setProperty(AGP_ORIGINAL_QUERY, exchange.getIn().getBody());
  }


}
