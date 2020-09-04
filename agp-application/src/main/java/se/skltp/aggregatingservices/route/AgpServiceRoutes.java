package se.skltp.aggregatingservices.route;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static se.skltp.aggregatingservices.constants.AgpHeaders.X_VP_SENDER_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_COMPONENT_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_TAK_CONTRACT_NAME;
import static se.skltp.aggregatingservices.constants.AgpProperties.INCOMMING_VP_SENDER_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.LOGICAL_ADDRESS;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.AgpCxfEndpointConfigurer;
import se.skltp.aggregatingservices.aggregate.AgpAggregationStrategy;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.config.EiConfig;
import se.skltp.aggregatingservices.config.VpConfig;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;
import se.skltp.aggregatingservices.processors.CheckInboundHeadersProcessor;
import se.skltp.aggregatingservices.processors.CreateFindContentProcessor;
import se.skltp.aggregatingservices.processors.CreateRequestListProcessor;
import se.skltp.aggregatingservices.processors.CreateResponseProcessor;
import se.skltp.aggregatingservices.processors.FilterFindContentResponseProcessor;

@Component
@Log4j2
public class AgpServiceRoutes extends RouteBuilder {

  @Value("${aggregate.timeout:29000}")
  Long aggregationTimeout;

  @Value("${validate.soapAction:false}")
  Boolean validateSoapAction;

  @Autowired
  GenericApplicationContext applicationContext;

  @Autowired
  VpConfig vpConfig;

  @Autowired
  EiConfig eiConfig;

  @Autowired
  CreateRequestListProcessor createRequestListProcessor;

  @Autowired
  CreateFindContentProcessor createFindContentProcessor;

  @Autowired
  FilterFindContentResponseProcessor filterFindContentResponseProcessor;

  @Autowired
  CreateResponseProcessor createResponseProcessor;

  @Autowired
  AgpAggregationStrategy agpAggregationStrategy;

  @Autowired
  CheckInboundHeadersProcessor checkInboundHeadersProcessor;

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
      + "&cxfEndpointConfigurer=#%s"
      + "&properties.use.async.http.conduit=%s";

  /* ----------------------------------------------------------------
  * beanId konfigureringen nedan är till för att tvinga Camel skapa en unik
  * böna för varje route som skapas. Detta för att vi sett att Camel
  * får problem vid hög last när alla routes delar på samma cxf komponent,
  *  troligen ngt problem med trådhanteringen i Camel.
  -------------------------------------------------------------------*/
  public static final String EI_FINDCONTENT_URI = "cxf://{{ei.findContentUrl}}"
      + "?wsdlURL=/schemas/TD_ENGAGEMENTINDEX_1_0_R/interactions/FindContentInteraction/FindContentInteraction_1.0_RIVTABP21.wsdl"
      + "&serviceClass=se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontent.v1.rivtabp21.FindContentResponderInterface"
      + "&portName={urn:riv:itintegration:engagementindex:FindContent:1:rivtabp21}FindContentResponderPort"
      + "&dataFormat=POJO"
      + "&cxfEndpointConfigurer=#eiEndpointConfBean"
      + "&beanId=findContent.%s"
      + "&properties.use.async.http.conduit=%s";

  List<AgpServiceConfiguration> serviceConfigurations;

  @Autowired
  public AgpServiceRoutes(List<AgpServiceConfiguration> serviceConfigurations) {
    this.serviceConfigurations = serviceConfigurations;
  }

  @Override
  public void configure() throws Exception {

    applicationContext.registerBean("eiEndpointConfBean", AgpCxfEndpointConfigurer.class,
        ()->new AgpCxfEndpointConfigurer(eiConfig.getReceiveTimeout(), eiConfig.getConnectTimeout(), false, true));

    for (AgpServiceConfiguration serviceConfiguration : serviceConfigurations) {
      createServiceRoute(serviceConfiguration);
    }
  }

  private void createServiceRoute(AgpServiceConfiguration serviceConfiguration) throws Exception {

    registerConfigurationBean(serviceConfiguration);

    String inboundServiceAddress = getInboundServiceAddress(serviceConfiguration);
    String outboundServiceAddress = getOutboundServiceAddress(serviceConfiguration);
    String findContentServiceAddress = String.format(EI_FINDCONTENT_URI,
        serviceConfiguration.getServiceName(),
        eiConfig.getUseAyncHttpConduit() );
    log.debug("inboundServiceAddress: {}", inboundServiceAddress);
    log.debug("outboundServiceAddress: {}", outboundServiceAddress);
    log.debug("findContentServiceAddress: {}", findContentServiceAddress);

    AgpServiceFactory agpServiceFactory = getServiceFactory(serviceConfiguration);

    String routeName = String.format("%s.route", serviceConfiguration.getServiceName());
    from(inboundServiceAddress).id(routeName).streamCaching()
        .setProperty(AGP_SERVICE_HANDLER).exchange(ex -> agpServiceFactory)
        .setProperty(AGP_SERVICE_COMPONENT_ID, simple( serviceConfiguration.getServiceName()))
        .setProperty(AGP_TAK_CONTRACT_NAME, simple(serviceConfiguration.getTakContract()))
        .setProperty("serviceAddress", simple(outboundServiceAddress))
        .process(checkInboundHeadersProcessor)
        .setProperty(AGP_ORIGINAL_QUERY, body())
        .setProperty(INCOMMING_VP_SENDER_ID, header(X_VP_SENDER_ID))
        .process(createFindContentProcessor)
          .to(findContentServiceAddress).id("to.findContent.for."+serviceConfiguration.getServiceName())
        .setHeader(X_VP_SENDER_ID, exchangeProperty(INCOMMING_VP_SENDER_ID))
        .process(filterFindContentResponseProcessor)
        .process(createRequestListProcessor)
        .removeHeaders("{{headers.request.filter}}")
        .split(body()).timeout(aggregationTimeout).parallelProcessing(true).aggregationStrategy(agpAggregationStrategy)
          .setProperty(LOGICAL_ADDRESS).exchange(ex -> ex.getIn().getBody(MessageContentsList.class).get(0))
          .removeHeader("breadcrumbId")
          .to(outboundServiceAddress).id("to.service."+serviceConfiguration.getServiceName())
        .end()
        .process(createResponseProcessor)
        .removeHeaders("{{headers.response.filter}}")
        .removeProperty(LOGICAL_ADDRESS);
  }

  private String getOutboundServiceAddress(AgpServiceConfiguration serviceConfiguration) {
    final String outboundServiceURL = getOutboundServiceURL(serviceConfiguration);
    String outboundServiceAddress = String.format(OUTBOUND_SERVICE_CONFIGURATION
        , outboundServiceURL
        , serviceConfiguration.getOutboundServiceWsdl()
        , serviceConfiguration.getOutboundServiceClass()
        , serviceConfiguration.getServiceName()
        , serviceConfiguration.getServiceName()
        , vpConfig.getUseAyncHttpConduit());
    if (serviceConfiguration.getOutboundPortName() != null) {
      return outboundServiceAddress + "&portName=" + serviceConfiguration.getOutboundPortName();
    }
    return outboundServiceAddress;
  }

  private String getInboundServiceAddress(AgpServiceConfiguration serviceConfiguration) {
    String inboundServiceAddress = String.format(INBOUND_SERVICE_CONFIGURATION
        , serviceConfiguration.getInboundServiceURL()
        , serviceConfiguration.getInboundServiceWsdl()
        , serviceConfiguration.getInboundServiceClass()
        , serviceConfiguration.getServiceName()
        , serviceConfiguration.getServiceName()
        , serviceConfiguration.getServiceName());
    if (serviceConfiguration.getInboundPortName() != null) {
     return inboundServiceAddress + "&portName=" + serviceConfiguration.getInboundPortName();
    }
    return inboundServiceAddress;
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
    boolean enableSchemaValidation = serviceConfiguration.isEnableSchemaValidation();
    log.info("Setting receiveTimeout={}, connectTimeout={} and schemaValidation={} for {}", receiveTimeout, connectTimeout
        , enableSchemaValidation, serviceConfiguration.getServiceName());

    applicationContext.registerBean(serviceConfiguration.getServiceName(), AgpCxfEndpointConfigurer.class,
        () -> new AgpCxfEndpointConfigurer(receiveTimeout, connectTimeout, enableSchemaValidation, validateSoapAction));
  }

}