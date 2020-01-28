package se.skltp.aggregatingservices.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.data.TakTestDataService;
import se.skltp.agp.riv.vagvalsinfo.v2.HamtaAllaAnropsBehorigheterResponseType;

@Component
public class BehorighetResponseProcessor implements Processor {

  @Autowired
  TakTestDataService takTestDataService;

  @Override
  public void process(Exchange exchange) throws Exception {
    final HamtaAllaAnropsBehorigheterResponseType behorigheterResponseType = takTestDataService.getAnropsBehorigheterResponse();
    exchange.getIn().setBody(behorigheterResponseType);
  }
}
