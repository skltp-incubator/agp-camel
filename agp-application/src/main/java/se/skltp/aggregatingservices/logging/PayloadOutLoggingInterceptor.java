package se.skltp.aggregatingservices.logging;

import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.ext.logging.event.LogEventSender;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;

public class PayloadOutLoggingInterceptor extends LoggingOutInterceptor  {
  public PayloadOutLoggingInterceptor(LogEventSender sender) {
    super(sender);
  }
  @Override
  public void handleMessage(Message message) throws Fault {
    super.handleMessage(message);
  }
}
