package se.skltp.aggregatingservices.route;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.aggregate.AgpAggregationStrategy;
import se.skltp.aggregatingservices.processors.CreateFindContentProcessor;
import se.skltp.aggregatingservices.processors.CreateRequestListFromEIProcessor;
import se.skltp.aggregatingservices.processors.CreateRequestListFromTAKProcessor;
import se.skltp.aggregatingservices.processors.CreateResponseProcessor;
import se.skltp.aggregatingservices.processors.PrepareServiceRequestProcessor;
import se.skltp.aggregatingservices.processors.UnmarshalQueryProcessor;

@Component
public class AgpRoute extends RouteBuilder {

  public static final String EI_FINDCONTENT_URI = "cxf://http://localhost:8082/findcontent"
      + "?wsdlURL=/schemas/TD_ENGAGEMENTINDEX_1_0_R/interactions/FindContentInteraction/FindContentInteraction_1.0_RIVTABP21.wsdl"
      + "&serviceClass=se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontent.v1.rivtabp21.FindContentResponderInterface"
      + "&dataFormat=POJO";

  @Autowired
  CreateRequestListFromEIProcessor createRequestListFromEIProcessor;

  @Autowired
  CreateRequestListFromTAKProcessor createRequestListFromTAKProcessor;

  @Autowired
  PrepareServiceRequestProcessor prepareRequestProcessor;

  @Autowired
  CreateFindContentProcessor createFindContentProcessor;

  @Autowired
  UnmarshalQueryProcessor unmarshalQueryProcessor;

  @Autowired
  CreateResponseProcessor createResponseProcessor;

  @Autowired
  AgpAggregationStrategy agpAggregationStrategy;


  @Override
  public void configure() throws Exception {
    from("direct:agproute").id("agp-service-route").streamCaching()
        .log("req-in")
        .setProperty(AGP_ORIGINAL_QUERY, body())
        .removeHeader(CxfConstants.OPERATION_NAME)
        .removeHeader(CxfConstants.OPERATION_NAMESPACE)
        .removeHeader("SoapAction")
        .process(createFindContentProcessor)
        .choice().when(body().isNotNull())
            .to(EI_FINDCONTENT_URI).id("to.findcontent")
            .process(createRequestListFromEIProcessor)
            .removeHeader("SoapAction")
        .otherwise()
            .process(createRequestListFromTAKProcessor)
        .end()
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