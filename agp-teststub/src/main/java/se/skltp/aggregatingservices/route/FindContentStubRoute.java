package se.skltp.aggregatingservices.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.processors.FindContentResponseProcessor;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontent.v1.rivtabp21.FindContentResponderInterface;

@Component
public class FindContentStubRoute extends RouteBuilder {

  public static final String FINDCONTENT_INBOUND_URL = "http://localhost:8082/findcontent";
  public static final String FINDCONTENT_WSDL_PATH = "/schemas/TD_ENGAGEMENTINDEX_1_0_R/interactions/FindContentInteraction/FindContentInteraction_1.0_RIVTABP21.wsdl";
  public static final String FINDCONTENT_SERVICECLASS = FindContentResponderInterface.class.getName();
  private static String SERVICE_CONFIGURATION = "cxf:%s?wsdlURL=%s&serviceClass=%s";

  protected String serviceAddress;

  @Autowired
  FindContentResponseProcessor findContentResponseProcessor;

  public FindContentStubRoute() {
    serviceAddress = String.format(SERVICE_CONFIGURATION, FINDCONTENT_INBOUND_URL, FINDCONTENT_WSDL_PATH, FINDCONTENT_SERVICECLASS);
  }

  @Override
  public void configure() throws Exception {
    from(serviceAddress).id("FindContent.route")
        .log(">> FindContent")
        .process(findContentResponseProcessor)
        .log("<< FindContent");
  }

}
