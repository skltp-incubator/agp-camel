package se.skltp.aggregatingservices.integrationtests;

import static org.apache.camel.test.junit4.TestSupport.assertStringContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.skltp.aggregatingservices.data.TestDataDefines.SAMPLE_SENDER_ID;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_LOGICAL_ADDRESS_1;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.skltp.aggregatingservices.AgpApplication;
import se.skltp.aggregatingservices.data.BehorighetTestData;
import se.skltp.aggregatingservices.service.TakCacheService;
import se.skltp.takcache.TakCacheLog;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = AgpApplication.class)
public class TakCacheResetIT {

  @Produce
  protected ProducerTemplate template;

  @Autowired
  BehorighetTestData behorighetTestData;

  @Autowired
  TakCacheService takCacheService;

  @Before
  public void setUp(){
    // See agp-teststub/readme.md for information about the TAK data generated.
    behorighetTestData.resetAnropsBehorigheterResponse();
    behorighetTestData.generateBehorighetDefaultStubData("test.namespace.1");
    behorighetTestData.generateBehorighetDefaultStubData("test.namespace.2");
  }

  @Test
  public void resetCacheShouldWork() throws Exception {

    // Call reset cache route
    String result =   template.requestBody("jetty://{{reset.cache.url}}", "body", String.class);
    assertStringContains(result, "Init done, was successful: true");

    assertEquals( true, takCacheService.isInitalized() );

    TakCacheLog takCacheLog = takCacheService.getLastRefreshLog();
    assertEquals( 30, takCacheLog.getNumberBehorigheter());
  }

  @Test
  public void testAuthorization() throws Exception {

    // Call reset cache route
    String result =   template.requestBody("jetty://{{reset.cache.url}}", "body", String.class);

    // See agp-teststub/readme.md for information about the TAK data generated.
    assertTrue(takCacheService.isAuthorized(SAMPLE_SENDER_ID, "test.namespace.2", TEST_LOGICAL_ADDRESS_1));
    assertFalse(takCacheService.isAuthorized(SAMPLE_SENDER_ID, "test.namespace.3", TEST_LOGICAL_ADDRESS_1));
 }

}
