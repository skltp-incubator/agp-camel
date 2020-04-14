package se.skltp.aggregatingservices.processors;

import org.apache.camel.test.junit4.CamelTestSupport;

//@RunWith(CamelSpringRunner.class)
//@ContextConfiguration(classes = {HandleHttpHeadersProcessor.class, VpConfig.class})
//@TestPropertySource("classpath:application.properties")
//@MockEndpoints("direct:end")
public class HandleHttpHeadersProcessorTest extends CamelTestSupport {
	
//	@Autowired
//	HandleHttpHeadersProcessor handleHttpHeadersProcessor;
//
//    @EndpointInject(uri="mock:direct:end")
//    MockEndpoint mock;
//
//    @Produce(uri = "direct:start")
//    protected ProducerTemplate template;
//
//    @Test
//    public void receiveTest() throws Exception {
//        mock.expectedBodiesReceived("Hello");
//        mock.message(0).header(AGP_RIVTA_ORIGINAL_CONSUMER_ID).isEqualTo("consumer-id");
//        mock.message(0).header(AGP_VP_SENDER_ID).isEqualTo("SENDER1");
//        mock.message(0).header(AGP_VP_INSTANCE_ID).isEqualTo("dev_env");
//        mock.message(0).header(AGP_SKLTP_CORRELATION_ID).isEqualTo("corr-id");
//
//        Map<String, Object> headers = new HashMap<String, Object>();
//        headers.put(AGP_RIVTA_ORIGINAL_CONSUMER_ID, "consumer-id");
//        headers.put(AGP_SKLTP_CORRELATION_ID, "corr-id");
//
//        template.sendBodyAndHeaders("direct:start",
//        		ExchangePattern.InOut, "Hello", headers);
//        mock.assertIsSatisfied();
//    }
//
//    @Override
//    protected RoutesBuilder createRouteBuilder() {
//        return new RouteBuilder() {
//
//			@Override
//			public void configure() throws Exception {
//				from("direct:start").process(handleHttpHeadersProcessor).to("mock:direct:end");
//
//			}
//
//
//		};
//    }


}
