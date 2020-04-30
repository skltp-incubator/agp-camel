package se.skltp.aggregatingservices;

import java.util.EventObject;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.management.event.CamelContextStartedEvent;
import org.apache.camel.management.event.ExchangeCreatedEvent;
import org.apache.camel.management.event.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.constants.AgpHeaders;
import se.skltp.aggregatingservices.service.TakCacheService;

@Log4j2
@Component
public class StartupEventNotifier extends EventNotifierSupport {

  private final TakCacheService takCacheService;

  @Autowired
  public StartupEventNotifier(TakCacheService takCacheService) {
    this.takCacheService = takCacheService;
  }

  @Override
  public boolean isEnabled(EventObject event) {
    return true;
  }

  @Override
  protected void doStart() throws Exception {
    setIgnoreCamelContextEvents(false);
    setIgnoreExchangeCreatedEvent(false);

//    // filter out unwanted events
    setIgnoreExchangeSentEvents(true);
    setIgnoreExchangeCompletedEvent(true);
    setIgnoreExchangeFailedEvents(true);
    setIgnoreServiceEvents(true);
    setIgnoreRouteEvents(true);
    setIgnoreExchangeRedeliveryEvents(true);
  }

  @Override
  public void notify(EventObject event) {

    if (event instanceof CamelContextStartedEvent) {
      initTakCache();
    } else if (event instanceof ExchangeCreatedEvent ||
        event instanceof ExchangeSentEvent ) {
      final Object source = event.getSource();
      if (source instanceof Exchange) {
        final String correlationId = ((Exchange) source).getIn().getHeader(AgpHeaders.X_SKLTP_CORRELATION_ID, String.class);
        if (correlationId != null) {
          ThreadContext.put("corr.id", String.format("[%s]", correlationId));
        }
      }

    }
  }

  private void initTakCache() {
    takCacheService.refresh();
  }

}
