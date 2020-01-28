package se.skltp.aggregatingservices.processors;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.v1.EngagementType;

@Component
@Log4j2
public class FindContentResponseProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    FindContentResponseType findContentResponseType = new FindContentResponseType();
    findContentResponseType.getEngagement().add(createEngagementType("HSA-ID-4"));
    findContentResponseType.getEngagement().add(createEngagementType("HSA-ID-5"));

    exchange.getIn().setBody(findContentResponseType);
  }

  private EngagementType createEngagementType(String logicalAddress) {
    EngagementType engagementType = new EngagementType();
    engagementType.setLogicalAddress(logicalAddress);
    engagementType.setSourceSystem(logicalAddress);
    engagementType.setCategorization("CAT_1");
    engagementType.setServiceDomain("DOMAIN_1");
    return engagementType;
  }
}
