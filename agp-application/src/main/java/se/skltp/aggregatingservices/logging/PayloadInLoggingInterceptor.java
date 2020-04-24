package se.skltp.aggregatingservices.logging;

import static se.skltp.aggregatingservices.constants.AgpProperties.CORRELATION_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.WireTapIn;
import org.apache.cxf.ext.logging.event.LogEventSender;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.logging.log4j.ThreadContext;
import se.skltp.aggregatingservices.constants.AgpHeaders;

@Log4j2
public class PayloadInLoggingInterceptor extends LoggingInInterceptor {

  public PayloadInLoggingInterceptor(LogEventSender sender) {
    super(sender);
  }

  @Override
  public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
    Collection<PhaseInterceptor<? extends Message>> ret = new ArrayList();
    ret.add(new WireTapIn(this.getWireTapLimit(), this.threshold));
    ret.add(new PayloadInLoggingInterceptor.PayloadLoggingInFaultInterceptor());
    return ret;
  }

  public int getWireTapLimit() {
    if (this.limit == -1 || this.limit == Integer.MAX_VALUE) {
      return this.limit ;
    }
    return this.limit+1;
  }

  @Override
  public void handleMessage(Message message) throws Fault {
    setCorrelationId(message);
    super.handleMessage(message);
  }


  class PayloadLoggingInFaultInterceptor extends AbstractPhaseInterceptor<Message> {
    PayloadLoggingInFaultInterceptor() {
      super("receive");
    }

    @Override
    public void handleMessage(Message message) throws Fault {
    }

    @Override
    public void handleFault(Message message) throws Fault {
      PayloadInLoggingInterceptor.this.handleMessage(message);
    }
  }


  private void setCorrelationId(Message message) {
    String corrId = (String) message.getExchange().get(CORRELATION_ID);
    if(corrId!=null){
      log.debug("set corrid from exchange: {}", corrId);
      ThreadContext.put("corr.id", String.format("[%s]", corrId));
    } else {
      setCorrIdFromInHeader(message);
    }
  }

  private void setCorrIdFromInHeader(Message message) {
    final Map headers = (Map)message.get(Message.PROTOCOL_HEADERS);
    if(headers!=null){
      List corrIdList = (List) headers.get(AgpHeaders.X_SKLTP_CORRELATION_ID);
      if(corrIdList!=null && !corrIdList.isEmpty()){
        String corrId = (String) corrIdList.get(0);
        log.debug("set corrid from inheader: {}", corrId);
        message.getExchange().put(CORRELATION_ID, corrId);
        ThreadContext.put("corr.id", String.format("[%s]", corrId));
      }
    }
  }
}
