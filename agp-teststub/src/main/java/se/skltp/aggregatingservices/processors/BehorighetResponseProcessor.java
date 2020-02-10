package se.skltp.aggregatingservices.processors;

import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.config.TestProducerConfiguration;
import se.skltp.aggregatingservices.data.BehorighetTestData;
import se.skltp.agp.riv.vagvalsinfo.v2.HamtaAllaAnropsBehorigheterResponseType;

@Component
public class BehorighetResponseProcessor implements Processor {

  BehorighetTestData behorighetTestData;

  @Autowired
  public BehorighetResponseProcessor(List<TestProducerConfiguration> configurations, BehorighetTestData behorighetTestData) {
    this.behorighetTestData = behorighetTestData;

    for(TestProducerConfiguration configuration : configurations){
      behorighetTestData.generateBehorighetDefaultStubData(configuration.getServiceNamespace());
    }
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    final HamtaAllaAnropsBehorigheterResponseType behorigheterResponseType = behorighetTestData.getAnropsBehorigheterResponse();
    exchange.getIn().setBody(behorigheterResponseType);
  }
}
