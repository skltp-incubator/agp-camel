package se.skltp.aggregatingservices.processors;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import se.skltp.aggregatingservices.service.TakCacheServiceImpl;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.TakCacheLog;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {ResetTakCacheProcessor.class, TakCacheServiceImpl.class})
@TestPropertySource("classpath:application.properties")
@MockEndpoints("direct:end")
public class ResetTakCacheProcessorTest extends CamelTestSupport {

  @MockBean(name = "takCache")
  private TakCache takCacheMock;

  @Autowired
  ResetTakCacheProcessor resetTakCacheProcessor;

  @Produce(uri = "direct:start")
  protected ProducerTemplate template;

  @Before
  public void beforeTest() {
    List<String> testLog = new ArrayList<>();
    testLog.add("Test log1");
    testLog.add("Test log2");

    TakCacheLog takCacheLog = mock(TakCacheLog.class);
    Mockito.when(takCacheLog.getLog()).thenReturn(testLog);
    Mockito.when(takCacheMock.refresh()).thenReturn(takCacheLog);
  }
  @Test
  public void process() {
    Exchange exchange = template.send(resetTakCacheProcessor);
    assertEquals("<br>Test log1<br>Test log2", exchange.getOut().getBody(String.class) );
    assertEquals("text/html;", exchange.getOut().getHeader("Content-Type", String.class) );
    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class).intValue() );
  }


}