package se.skltp.aggregatingservices.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.processors.BehorighetResponseProcessor;
import se.skltp.agp.riv.vagvalsinfo.v2.SokVagvalsInfoInterface;

@Component
public class SokVagValInfoStubRoute extends RouteBuilder {

  public static final String SOKVAGVAL_INBOUND_URL = "http://localhost:8085/tak/teststub/SokVagvalsInfo/v2";
  public static final String SOKVAGVAL_WSDL_PATH = "/schemas/TD_SOKVAGVAL_2/sokvagval-info-v2.wsdl";
  public static final String SOKVAGVAL_SERVICECLASS = SokVagvalsInfoInterface.class.getName();
  private static String SERVICE_CONFIGURATION = "cxf:%s?wsdlURL=%s&serviceClass=%s";

  protected String serviceAddress;

  @Autowired
  BehorighetResponseProcessor behorighetResponseProcessor;

  public SokVagValInfoStubRoute() {
    serviceAddress = String.format(SERVICE_CONFIGURATION, SOKVAGVAL_INBOUND_URL, SOKVAGVAL_WSDL_PATH, SOKVAGVAL_SERVICECLASS) ;
  }

  @Override
  public void configure() throws Exception {
    from(serviceAddress).id("SokVagval.route")
        .log(">> SokVagval")
        .choice()
            .when(header("operationName").isEqualTo("hamtaAllaAnropsBehorigheter"))
                .process(behorighetResponseProcessor)
            .when(header("operationName").isEqualTo("hamtaAllaVirtualiseringar"))
                .log("Not implemented!")
        .end()
        .log("<< SokVagval");
  }

}
