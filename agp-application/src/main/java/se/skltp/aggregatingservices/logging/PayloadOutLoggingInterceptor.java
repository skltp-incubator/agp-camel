package se.skltp.aggregatingservices.logging;

import static se.skltp.aggregatingservices.constants.AgpProperties.CORRELATION_ID;

import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.ext.logging.event.LogEventSender;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.logging.log4j.ThreadContext;
import se.skltp.aggregatingservices.constants.AgpHeaders;

@Log4j2
public class PayloadOutLoggingInterceptor extends LoggingOutInterceptor {

  public PayloadOutLoggingInterceptor(LogEventSender sender) {
    super(sender);
  }

  @Override
  public void handleMessage(Message message) throws Fault {
    setCorrelationId(message);
    super.handleMessage(message);
  }


  private void setCorrelationId(Message message) {
    String corrId = (String) message.getExchange().get(CORRELATION_ID);
    if (corrId != null) {
      log.debug("set corrid from exchange: {}", corrId);
      ThreadContext.put("corr.id", String.format("[%s]", corrId));
    } else {
      setCorrIdFromInHeader(message);
    }
  }

  private void setCorrIdFromInHeader(Message message) {
    final Map headers = (Map) message.get(Message.PROTOCOL_HEADERS);
    if (headers != null) {
      List corrIdList = (List) headers.get(AgpHeaders.X_SKLTP_CORRELATION_ID);
      if (corrIdList != null && !corrIdList.isEmpty()) {
        String corrId = (String) corrIdList.get(0);
        log.debug("set corrid from inheader: {}", corrId);
        message.getExchange().put(CORRELATION_ID, corrId);
        ThreadContext.put("corr.id", String.format("[%s]", corrId));
      }
    }
  }
}