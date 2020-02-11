package se.skltp.aggregatingservices;

import java.util.EventObject;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.management.event.CamelContextStartedEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    // filter out unwanted events
    setIgnoreExchangeSentEvents(true);
    setIgnoreExchangeCompletedEvent(true);
    setIgnoreExchangeFailedEvents(true);
    setIgnoreServiceEvents(true);
    setIgnoreRouteEvents(true);
    setIgnoreExchangeCreatedEvent(true);
    setIgnoreExchangeRedeliveryEvents(true);
  }

  @Override
  public void notify(EventObject event) {
    if (event instanceof CamelContextStartedEvent) {
      initTakCache();
    }
  }

  private void initTakCache() {
    takCacheService.refresh();
  }

}
