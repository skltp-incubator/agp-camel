package se.skltp.aggregatingservices.route;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_COMPONENT_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_TAK_CONTRACT_NAME;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.AgpCxfEndpointConfigurer;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.config.VpConfig;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;

@Component
@Log4j2
public class AgpServiceRoutes extends RouteBuilder {

  @Autowired
  GenericApplicationContext applicationContext;

  @Autowired
  VpConfig vpConfig;

  public static final String INBOUND_SERVICE_CONFIGURATION = "cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s"
      + "&beanId=%s"
      + "&properties.ComponentId=%s"
      + "&cxfEndpointConfigurer=#%s";

  public static final String OUTBOUND_SERVICE_CONFIGURATION = "cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s"
      + "&beanId=%s"
      + "&cxfEndpointConfigurer=#%s";


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
    registerConfigurationBean(serviceConfiguration);

    // Set inbound props
    String inboundServiceAddress = String.format(INBOUND_SERVICE_CONFIGURATION
        , serviceConfiguration.getInboundServiceURL()
        , serviceConfiguration.getInboundServiceWsdl()
        , serviceConfiguration.getInboundServiceClass()
        , serviceConfiguration.getServiceName()
        , serviceConfiguration.getServiceName()
        , serviceConfiguration.getServiceName());
    String inRouteName = String.format("%s.in.route", serviceConfiguration.getServiceName());
    if (serviceConfiguration.getInboundPortName() != null) {
      inboundServiceAddress = inboundServiceAddress + "&portName=" + serviceConfiguration.getInboundPortName();
    }


    // Set outbound props
    final String outboundServiceURL = getOutboundServiceURL(serviceConfiguration);
    String outboundServiceAddress = String.format(OUTBOUND_SERVICE_CONFIGURATION
        , outboundServiceURL
        , serviceConfiguration.getOutboundServiceWsdl()
        , serviceConfiguration.getOutboundServiceClass()
        , serviceConfiguration.getServiceName()
        , serviceConfiguration.getServiceName());
    String outRouteName = String.format("%s.out.route", serviceConfiguration.getServiceName());
    String directRouteToProducer = serviceConfiguration.getServiceName();
    if (serviceConfiguration.getOutboundPortName() != null) {
      outboundServiceAddress = outboundServiceAddress + "&portName=" + serviceConfiguration.getOutboundPortName();
    }

    AgpServiceFactory agpServiceFactory = getServiceFactory(serviceConfiguration);

    log.debug("inboundServiceAddress: {}", inboundServiceAddress);
    from(inboundServiceAddress).id(inRouteName).streamCaching()
        .setProperty(AGP_SERVICE_HANDLER).exchange(ex -> agpServiceFactory)
        .setProperty(AGP_SERVICE_COMPONENT_ID, simple(directRouteToProducer))
        .setProperty(AGP_TAK_CONTRACT_NAME, simple(serviceConfiguration.getTakContract()))
        .setProperty("serviceAddress", simple(outboundServiceAddress))
        .to("direct:agproute");

    log.debug("outboundServiceAddress: {}", outboundServiceAddress);
    from("direct:" + directRouteToProducer).id(outRouteName)
        .to(outboundServiceAddress);

  }

  private String getOutboundServiceURL(AgpServiceConfiguration serviceConfiguration) {
    return isEmpty(serviceConfiguration.getOutboundServiceURL()) ?
        vpConfig.getDefaultServiceURL() :
        serviceConfiguration.getOutboundServiceURL();
  }

  private AgpServiceFactory getServiceFactory(AgpServiceConfiguration configuration)
      throws Exception {
    final AgpServiceFactory agpServiceFactory = (AgpServiceFactory) Class.forName(configuration.getServiceFactoryClass())
        .getConstructor().newInstance();
    agpServiceFactory.setAgpServiceConfiguration(configuration);
    return agpServiceFactory;
  }

  private void registerConfigurationBean(AgpServiceConfiguration serviceConfiguration) {
    int receiveTimeout = serviceConfiguration.getReceiveTimeout() >= 0 ? serviceConfiguration.getReceiveTimeout()
        : vpConfig.getDefaultReceiveTimeout();
    int connectTimeout = serviceConfiguration.getConnectTimeout() >= 0 ? serviceConfiguration.getConnectTimeout()
        : vpConfig.getDefaultConnectTimeout();

    log.info("Setting receiveTimeout={} and connectTimeout={} for {}", receiveTimeout, connectTimeout,
        serviceConfiguration.getServiceName());

    applicationContext.registerBean(serviceConfiguration.getServiceName(), AgpCxfEndpointConfigurer.class,
        () -> new AgpCxfEndpointConfigurer(receiveTimeout, connectTimeout));
  }

}