package se.skltp.aggregatingservices.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.processors.GetStatusProcessor;

@Component
public class GetStatusRoute extends RouteBuilder {

  public static final String JETTY_HTTP_FROM_GET_STATUS = "jetty://{{agp.status.url}}";
  public static final String GET_STATUS_ROUTE = "get-status"; //+ "?chunkedMaxContentLength={{vp.max.receive.length}}";

  @Autowired
  GetStatusProcessor getStatusProcessor;

  @Override
  public void configure() throws Exception {
    from(JETTY_HTTP_FROM_GET_STATUS).routeId(GET_STATUS_ROUTE)
        .process(getStatusProcessor);
  }
}