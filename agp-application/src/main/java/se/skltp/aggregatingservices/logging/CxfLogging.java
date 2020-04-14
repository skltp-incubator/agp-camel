package se.skltp.aggregatingservices.logging;

import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.DefaultCxfBinding;


@Log4j2
public class CxfLogging extends DefaultCxfBinding {

  @Override
  public void populateExchangeFromCxfResponse(Exchange camelExchange,
      org.apache.cxf.message.Exchange cxfExchange,
      Map<String, Object> responseContext) {
    super.populateExchangeFromCxfResponse(camelExchange, cxfExchange, responseContext);

    MessageInfoLogger.logRespIn(camelExchange);
  }

  public void populateCxfRequestFromExchange(org.apache.cxf.message.Exchange cxfExchange, Exchange camelExchange,
      Map<String, Object> requestContext) {
    super.populateCxfRequestFromExchange(cxfExchange, camelExchange, requestContext);

    MessageInfoLogger.logReqOut(camelExchange);
  }

//  public void populateExchangeFromCxfRequest(org.apache.cxf.message.Exchange cxfExchange, Exchange camelExchange) {
//    super.populateExchangeFromCxfRequest(cxfExchange, camelExchange);
//
//    MessageInfoLogger.logReqIn(camelExchange);
//  }
//
//  public void populateCxfResponseFromExchange(Exchange camelExchange, org.apache.cxf.message.Exchange cxfExchange) {
//    super.populateCxfResponseFromExchange(camelExchange, cxfExchange);
//
//    MessageInfoLogger.logRespOut(camelExchange);
//  }
}


