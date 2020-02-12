package se.skltp.aggregatingservices.processors;

import static org.junit.Assert.assertEquals;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_RIVTA_ORIGINAL_CONSUMER_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_TAK_CONTRACT_NAME;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_VP_SENDER_ID;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS;

import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.cxf.message.MessageContentsList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.service.Authority;
import se.skltp.aggregatingservices.service.TakCacheServiceImpl;
import se.skltp.aggregatingservices.utils.AgpServiceFactoryImpl;
import se.skltp.aggregatingservices.utils.FindContentUtil;
import se.skltp.aggregatingservices.utils.RequestUtil;
import se.skltp.takcache.TakCache;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {CreateRequestListProcessor.class, TakCacheServiceImpl.class})
@TestPropertySource("classpath:application.properties")
@MockEndpoints("direct:end")
public class CreateRequestListProcessorTest {

  @MockBean(name = "takCache")
  private TakCache takCacheMock;

  @Autowired
  CreateRequestListProcessor createRequestListProcessor;

  @Test
  public void processWithAllEngagementsAuthorized() throws Exception {
    Exchange ex = createExchange();

    Mockito.when(takCacheMock.isAuthorized("sender1", "ns:1", "HSA-ID-4")).thenReturn(true);
    Mockito.when(takCacheMock.isAuthorized("org_sender1", "ns:1", "HSA-ID-5")).thenReturn(true);
    Mockito.when(takCacheMock.isAuthorized("sender1", "ns:1", "HSA-ID-6")).thenReturn(true);

   createRequestListProcessor.process(ex);

    List< MessageContentsList > list = (List<MessageContentsList>) ex.getIn().getBody();
    assertEquals(3, list.size());

  }

  @Test
  public void NoEngagementFiltered() {
    // Creates three engagements with HSA-ID-4, HSA-ID-5, HSA-ID-6
    final FindContentResponseType findContentResponse = FindContentUtil.createFindContentResponse(TEST_RR_ID_MANY_HITS_NO_ERRORS);
    assertEquals(3, findContentResponse.getEngagement().size());

    Mockito.when(takCacheMock.isAuthorized("sender1", "ns:1", "HSA-ID-4")).thenReturn(true);
    Mockito.when(takCacheMock.isAuthorized("org_sender1", "ns:1", "HSA-ID-5")).thenReturn(true);
    Mockito.when(takCacheMock.isAuthorized("sender1", "ns:1", "HSA-ID-6")).thenReturn(true);

    createRequestListProcessor.filterFindContentResponseBasedOnAuthority(findContentResponse, getAuthority());
    assertEquals(3, findContentResponse.getEngagement().size());

  }

  @Test
  public void TwoEngagementFiltered() {
    // Creates three engagements with HSA-ID-4, HSA-ID-5, HSA-ID-6
    final FindContentResponseType findContentResponse = FindContentUtil.createFindContentResponse(TEST_RR_ID_MANY_HITS_NO_ERRORS);
    assertEquals(3, findContentResponse.getEngagement().size());

    Mockito.when(takCacheMock.isAuthorized("sender1", "ns:1", "HSA-ID-4")).thenReturn(true);

    createRequestListProcessor.filterFindContentResponseBasedOnAuthority(findContentResponse, getAuthority());
    assertEquals(1, findContentResponse.getEngagement().size());
  }

  @Test
  public void AllEngagementFiltered() {
    // Creates three engagements with HSA-ID-4, HSA-ID-5, HSA-ID-6
    final FindContentResponseType findContentResponse = FindContentUtil.createFindContentResponse(TEST_RR_ID_MANY_HITS_NO_ERRORS);
    assertEquals(3, findContentResponse.getEngagement().size());

    Mockito.when(takCacheMock.isAuthorized("sender1", "ns:1", "HSA-ID-4")).thenReturn(false);

    createRequestListProcessor.filterFindContentResponseBasedOnAuthority(findContentResponse, getAuthority());
    assertEquals(0, findContentResponse.getEngagement().size());
  }

  private Authority getAuthority() {
    Authority authority = new Authority();
    authority.setSenderId("sender1");
    authority.setOriginalSenderId("org_sender1");
    authority.setServicecontractNamespace("ns:1");
    return authority;
  }

  private Exchange createExchange() {
    final Exchange ex = new DefaultExchange(new DefaultCamelContext());

    ex.setProperty(AGP_ORIGINAL_QUERY, RequestUtil.createTestMessageContentsList());
    ex.setProperty(AGP_SERVICE_HANDLER, AgpServiceFactoryImpl.createInstance("domain1", "cat1"));
    ex.setProperty(AGP_VP_SENDER_ID, "sender1");
    ex.setProperty(AGP_RIVTA_ORIGINAL_CONSUMER_ID, "org_sender1");
    ex.setProperty(AGP_TAK_CONTRACT_NAME,"ns:1" );
    ex.getIn().setBody(FindContentUtil.createMessageContentsList(TEST_RR_ID_MANY_HITS_NO_ERRORS));

    return ex;
  }


}
