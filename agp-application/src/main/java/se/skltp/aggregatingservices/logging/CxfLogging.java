package se.skltp.aggregatingservices.logging;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_COMPONENT_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.MESSAGE_EXCHANGE_ID;

import java.util.Map;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DefaultCxfBinding;


@Log4j2
public class CxfLogging extends DefaultCxfBinding {

  @Override
  public void populateExchangeFromCxfResponse(Exchange camelExchange,
      org.apache.cxf.message.Exchange cxfExchange,
      Map<String, Object> responseContext) {
    super.populateExchangeFromCxfResponse(camelExchange, cxfExchange, responseContext);

    populateExchangeIdToCamel(cxfExchange, camelExchange);
    MessageInfoLogger.logRespIn(camelExchange);
  }

  public void populateCxfRequestFromExchange(org.apache.cxf.message.Exchange cxfExchange, Exchange camelExchange,
      Map<String, Object> requestContext) {
    super.populateCxfRequestFromExchange(cxfExchange, camelExchange, requestContext);

    createExchangeId(camelExchange, requestContext);
    MessageInfoLogger.logReqOut(camelExchange);
  }

  public void populateExchangeFromCxfRequest(org.apache.cxf.message.Exchange cxfExchange, Exchange camelExchange) {
    super.populateExchangeFromCxfRequest(cxfExchange, camelExchange);

    populateExchangeIdToCamel(cxfExchange, camelExchange);
    camelExchange.setProperty(AGP_SERVICE_COMPONENT_ID, ((CxfEndpoint)camelExchange.getFromEndpoint()).getBeanId() );
    MessageInfoLogger.logReqIn(camelExchange);
  }

  public void populateCxfResponseFromExchange(Exchange camelExchange, org.apache.cxf.message.Exchange cxfExchange) {
    super.populateCxfResponseFromExchange(camelExchange, cxfExchange);

    MessageInfoLogger.logRespOut(camelExchange);
  }

  private void populateExchangeIdToCamel(org.apache.cxf.message.Exchange cxfExchange, Exchange camelExchange) {
    final String exchangeId = (String)cxfExchange.get(MESSAGE_EXCHANGE_ID);
    camelExchange.setProperty(MESSAGE_EXCHANGE_ID, exchangeId);
  }

  private void createExchangeId( Exchange camelExchange, Map<String, Object> requestContext) {
    String exchangeId = UUID.randomUUID().toString();
    camelExchange.setProperty(MESSAGE_EXCHANGE_ID, exchangeId);
    requestContext.put(MESSAGE_EXCHANGE_ID, exchangeId);
  }





}


