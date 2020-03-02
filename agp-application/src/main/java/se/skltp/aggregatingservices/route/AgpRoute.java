package se.skltp.aggregatingservices.route;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_RIVTA_ORIGINAL_CONSUMER_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_VP_SENDER_ID;

import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.aggregate.AgpAggregationStrategy;
import se.skltp.aggregatingservices.processors.CreateFindContentProcessor;
import se.skltp.aggregatingservices.processors.CreateRequestListProcessor;
import se.skltp.aggregatingservices.processors.CreateResponseProcessor;
import se.skltp.aggregatingservices.processors.HandleHttpHeadersProcessor;
import se.skltp.aggregatingservices.processors.PrepareServiceRequestProcessor;

@Component
public class AgpRoute extends RouteBuilder {

  public static final String EI_FINDCONTENT_URI = "cxf://http://localhost:8082/findcontent"
      + "?wsdlURL=/schemas/TD_ENGAGEMENTINDEX_1_0_R/interactions/FindContentInteraction/FindContentInteraction_1.0_RIVTABP21.wsdl"
      + "&serviceClass=se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontent.v1.rivtabp21.FindContentResponderInterface"
      + "&dataFormat=POJO";

  @Autowired
  CreateRequestListProcessor createRequestListProcessor;

  @Autowired
  PrepareServiceRequestProcessor prepareRequestProcessor;

  @Autowired
  CreateFindContentProcessor createFindContentProcessor;

  @Autowired
  CreateResponseProcessor createResponseProcessor;

  @Autowired
  AgpAggregationStrategy agpAggregationStrategy;

  @Autowired
  HandleHttpHeadersProcessor handleHttpHeadersProcessor;
  
  @Override
  public void configure() throws Exception {

	from("direct:agproute").id("agp-service-route").streamCaching()
        .log("req-in")
        .setProperty(AGP_ORIGINAL_QUERY, body())
        .setProperty(AGP_VP_SENDER_ID, header(AGP_VP_SENDER_ID))
        .setProperty(AGP_RIVTA_ORIGINAL_CONSUMER_ID, header(AGP_RIVTA_ORIGINAL_CONSUMER_ID))
        .process(handleHttpHeadersProcessor)
        .process(createFindContentProcessor)
        .to(EI_FINDCONTENT_URI).id("to.findcontent")
        .process(createRequestListProcessor)
        .removeHeader("SoapAction")
        .setHeader(AGP_VP_SENDER_ID, exchangeProperty(AGP_VP_SENDER_ID))
        .split(body()).parallelProcessing(true).aggregationStrategy(agpAggregationStrategy)
            .setProperty("LogicalAddress").exchange(ex -> ex.getIn().getBody(MessageContentsList.class).get(0))
            .log("req-out")
            .recipientList(simple("direct:${property.ProducerRouteName}")).end()
            .log("resp-in")
        .end()
        .process(createResponseProcessor)
        .log("resp-out");

  }
}