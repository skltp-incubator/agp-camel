package se.skltp.aggregatingservices;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcome.v4.rivtabp21.GetLaboratoryOrderOutcomeResponderInterface;

@Service
public class GetLaboratoryOrderOutcomeRoute  extends RouteBuilder {

  public static final String WSDL_PATH = "schemas/clinicalprocess-healthcond-actoutcome/interactions/GetLaboratoryOrderOutcomeInteraction/GetLaboratoryOrderOutcomeInteraction_4.0_RIVTABP21.wsdl";
  public static final String INBOUND_ADDRESS = "http://localhost:8083/vp";
  public static final String SERVICE_CLASS = GetLaboratoryOrderOutcomeResponderInterface.class.getName();
  private static String SERVICE_CONFIGURATION="cxf:%s"
      + "?wsdlURL=%s"
      + "&serviceClass=%s"
      + "&loggingFeatureEnabled=true";

  protected String serviceAddress;

  @Autowired
  CreateResponseProcessor createResponseProcessor;

  public GetLaboratoryOrderOutcomeRoute() {
    serviceAddress = String.format(SERVICE_CONFIGURATION, INBOUND_ADDRESS,
        WSDL_PATH, SERVICE_CLASS);
  }

  @Override
  public void configure() throws Exception {
    from(serviceAddress).id("GetLaboratoryOrderOutcomeTestStub.route")
        .process(createResponseProcessor)
        .log("Calling agp route");
  }

}
