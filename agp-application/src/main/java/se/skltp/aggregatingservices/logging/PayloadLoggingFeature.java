package se.skltp.aggregatingservices.logging;

import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.ext.logging.event.LogEventSender;
import org.apache.cxf.ext.logging.event.PrettyLoggingFilter;
import org.apache.cxf.ext.logging.slf4j.Slf4jEventSender;
import org.apache.cxf.ext.logging.slf4j.Slf4jVerboseEventSender;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PayloadLoggingFeature extends LoggingFeature {

  private LoggingInInterceptor in;
  private LoggingOutInterceptor out;
  private PrettyLoggingFilter inPrettyFilter;
  private PrettyLoggingFilter outPrettyFilter;

  public PayloadLoggingFeature( @Value("${log.max.payload.size}") int maxPayloadSize) {
    LogEventSender sender = new PayloadLogEventSender();
    this.inPrettyFilter = new PrettyLoggingFilter(sender);
    this.outPrettyFilter = new PrettyLoggingFilter(sender);
    this.in = new PayloadInLoggingInterceptor(this.inPrettyFilter);
    this.out = new PayloadOutLoggingInterceptor(this.outPrettyFilter);
    setLimit(maxPayloadSize);
  }

  protected void initializeProvider(InterceptorProvider provider, Bus bus) {
    provider.getInInterceptors().add(this.in);
    provider.getInFaultInterceptors().add(this.in);
    provider.getOutInterceptors().add(this.out);
    provider.getOutFaultInterceptors().add(this.out);
  }

  public void setLimit(int limit) {
    this.in.setLimit(limit);
    this.out.setLimit(limit);
  }

  public void setInMemThreshold(long inMemThreshold) {
    this.in.setInMemThreshold(inMemThreshold);
    this.out.setInMemThreshold(inMemThreshold);
  }

  public void setSender(LogEventSender sender) {
    this.inPrettyFilter.setNext(sender);
    this.outPrettyFilter.setNext(sender);
  }

  public void setInSender(LogEventSender s) {
    this.inPrettyFilter.setNext(s);
  }

  public void setOutSender(LogEventSender s) {
    this.outPrettyFilter.setNext(s);
  }

  public void setPrettyLogging(boolean prettyLogging) {
    this.inPrettyFilter.setPrettyLogging(prettyLogging);
    this.outPrettyFilter.setPrettyLogging(prettyLogging);
  }

  public void setLogBinary(boolean logBinary) {
    this.in.setLogBinary(logBinary);
    this.out.setLogBinary(logBinary);
  }

  public void setLogMultipart(boolean logMultipart) {
    this.in.setLogMultipart(logMultipart);
    this.out.setLogMultipart(logMultipart);
  }

  public void setVerbose(boolean verbose) {
    this.setSender((LogEventSender) (verbose ? new Slf4jVerboseEventSender() : new Slf4jEventSender()));
  }
}
