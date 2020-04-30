package se.skltp.aggregatingservices.route;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_COMPONENT_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_TAK_CONTRACT_NAME;

import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;

@Component
public class AgpServiceRoutes extends RouteBuilder {

  public static final String INBOUND_SERVICE_CONFIGURATION = "cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s"
      + "&beanId=%s"
      + "&properties.ComponentId=%s"
      + "&features=#loggingFeatures";

  public static final String OUTBOUND_SERVICE_CONFIGURATION = "cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s"
      + "&beanId=%s"
      + "&features=#loggingFeatures";


  List<AgpServiceConfiguration> serviceConfigurations;

  @Autowired
  public AgpServiceRoutes(List<AgpServiceConfiguration> serviceConfigurations) {
    this.serviceConfigurations = serviceConfigurations;
  }

  @Override
  public void configure() throws Exception {

    for (AgpServiceConfiguration serviceConfiguration : serviceConfigurations) {
      createServiceRoute(serviceConfiguration);
    }
  }

  private void createServiceRoute(AgpServiceConfiguration serviceConfiguration) throws Exception {

    // Set inbound props
    String inboundServiceAddress = String.format(INBOUND_SERVICE_CONFIGURATION
        , serviceConfiguration.getInboundServiceURL()
        , serviceConfiguration.getInboundServiceWsdl()
        , serviceConfiguration.getInboundServiceClass()
        , serviceConfiguration.getServiceName()
        , serviceConfiguration.getServiceName());
    String inRouteName = String.format("%s.in.route", serviceConfiguration.getServiceName());

    // Set outbound props
    String outboundServiceAddress = String.format(OUTBOUND_SERVICE_CONFIGURATION
        , serviceConfiguration.getOutboundServiceURL()
        , serviceConfiguration.getOutboundServiceWsdl()
        , serviceConfiguration.getOutboundServiceClass()
        , serviceConfiguration.getServiceName());
    String outRouteName = String.format("%s.out.route", serviceConfiguration.getServiceName());
    String directRouteToProducer = serviceConfiguration.getServiceName();

    AgpServiceFactory agpServiceFactory = getServiceFactory(serviceConfiguration);

    from(inboundServiceAddress).id(inRouteName).streamCaching()
        .setProperty(AGP_SERVICE_HANDLER).exchange(ex -> agpServiceFactory)
        .setProperty(AGP_SERVICE_COMPONENT_ID, simple(directRouteToProducer))
        .setProperty(AGP_TAK_CONTRACT_NAME, simple(serviceConfiguration.getTakContract()))
        .setProperty("serviceAddress", simple(outboundServiceAddress))
        .to("direct:agproute");

    from("direct:" + directRouteToProducer).id(outRouteName)
        .to(outboundServiceAddress);
  }

  private AgpServiceFactory getServiceFactory(AgpServiceConfiguration configuration)
      throws Exception {
    final AgpServiceFactory agpServiceFactory = (AgpServiceFactory) Class.forName(configuration.getServiceFactoryClass())
        .getConstructor().newInstance();
    agpServiceFactory.setAgpServiceConfiguration(configuration);
    return agpServiceFactory;
  }


}