package se.skltp.aggregatingservices.route;

import static se.skltp.aggregatingservices.constants.AgpHeaders.X_VP_SENDER_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.LOGICAL_ADDRESS;
import static se.skltp.aggregatingservices.constants.AgpProperties.INCOMMING_VP_SENDER_ID;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.aggregate.AgpAggregationStrategy;
import se.skltp.aggregatingservices.logging.MessageInfoLogger;
import se.skltp.aggregatingservices.processors.CheckInboundHeadersProcessor;
import se.skltp.aggregatingservices.processors.CreateFindContentProcessor;
import se.skltp.aggregatingservices.processors.CreateRequestListProcessor;
import se.skltp.aggregatingservices.processors.CreateResponseProcessor;
import se.skltp.aggregatingservices.processors.PrepareServiceRequestProcessor;

@Component
public class AgpRoute extends RouteBuilder {

//  public static final String LOG_RESP_OUT = "logRespOut(*)";
//  public static final String LOG_REQ_IN = "logReqIn(*)";
//  public static final String LOG_REQ_OUT = "logReqOut(*)";
//  public static final String LOG_RESP_IN = "logRespIn(*)";


  public static final String EI_FINDCONTENT_URI = "cxf://{{ei.findContentUrl}}"
      + "?wsdlURL=/schemas/TD_ENGAGEMENTINDEX_1_0_R/interactions/FindContentInteraction/FindContentInteraction_1.0_RIVTABP21.wsdl"
      + "&serviceClass=se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontent.v1.rivtabp21.FindContentResponderInterface"
      + "&dataFormat=POJO"
      + "&cxfBinding=#messageLogger"
      + "&features=#loggingFeatures";

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
  CheckInboundHeadersProcessor checkInboundHeadersProcessor;
  
  @Override
  public void configure() throws Exception {


	from("direct:agproute").id("agp-service-route").streamCaching()
        .errorHandler(noErrorHandler())
        .process(checkInboundHeadersProcessor)
        .setProperty(AGP_ORIGINAL_QUERY, body())
        .setProperty(INCOMMING_VP_SENDER_ID, header(X_VP_SENDER_ID))
        .process(createFindContentProcessor)
        .to(EI_FINDCONTENT_URI).id("to.findcontent")
        .process(createRequestListProcessor)
        .removeHeader("SoapAction")
        .setHeader(X_VP_SENDER_ID, exchangeProperty(INCOMMING_VP_SENDER_ID))
        .split(body()).parallelProcessing(true).aggregationStrategy(agpAggregationStrategy)
            .setProperty(LOGICAL_ADDRESS).exchange(ex -> ex.getIn().getBody(MessageContentsList.class).get(0))
            .toD("direct:${property.AgpServiceComponentId}")
//            .toD("${property.serviceAddress}")
        .end()
        .process(createResponseProcessor)
        .removeProperty(LOGICAL_ADDRESS);

  }



}