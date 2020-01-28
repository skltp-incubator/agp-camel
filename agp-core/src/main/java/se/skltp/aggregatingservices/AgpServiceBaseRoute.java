package se.skltp.aggregatingservices;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_PRODUCER_ROUTE_NAME;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;

import org.apache.camel.builder.RouteBuilder;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;

public class AgpServiceBaseRoute extends RouteBuilder {

  private static String INBOUND_SERVICE_CONFIGURATION ="cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s";

  private static String OUTBOUND_SERVICE_CONFIGURATION ="cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s";

  protected String inboundServiceAddress;
  protected String outboundServiceAddress;
  protected String inRouteName;
  protected String outRouteName;
  protected String directRouteToProducer;

  protected AgpServiceFactory agpServiceFactory;

  public AgpServiceBaseRoute(AgpServiceFactory agpServiceFactory,  AgpServiceConfiguration serviceConfiguration) {
    this.agpServiceFactory = agpServiceFactory;

    // Set inbound props
    inboundServiceAddress = String.format(INBOUND_SERVICE_CONFIGURATION
        , serviceConfiguration.getInboundServiceURL()
        , serviceConfiguration.getInboundServiceWsdl()
        , serviceConfiguration.getInboundServiceClass());
    inRouteName = String.format("%s.in.route", serviceConfiguration.getServiceName());

    // Set outbound props
    outboundServiceAddress = String.format(OUTBOUND_SERVICE_CONFIGURATION
        , serviceConfiguration.getOutboundServiceURL()
        , serviceConfiguration.getOutboundServiceWsdl()
        , serviceConfiguration.getOutboundServiceClass());
    outRouteName = String.format("%s.out.route", serviceConfiguration.getServiceName());
    directRouteToProducer = serviceConfiguration.getServiceName();
  }

  @Override
  public void configure() throws Exception {
    from(inboundServiceAddress).id(inRouteName).streamCaching()
        .setProperty(AGP_SERVICE_HANDLER).exchange(ex -> agpServiceFactory)
        .setProperty(AGP_PRODUCER_ROUTE_NAME).exchange(ex -> directRouteToProducer)
        .to("direct:agproute");

    from("direct:"+directRouteToProducer).id(outRouteName)
        .to(outboundServiceAddress);
  }
}
