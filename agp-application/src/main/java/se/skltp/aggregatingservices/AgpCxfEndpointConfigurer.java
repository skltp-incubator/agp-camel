package se.skltp.aggregatingservices;

import org.apache.camel.component.cxf.CxfEndpointConfigurer;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import se.skltp.aggregatingservices.logging.MessageLoggingFeature;

public  class AgpCxfEndpointConfigurer implements CxfEndpointConfigurer {

  @Autowired
  MessageLoggingFeature messageLoggingFeature;

  private int receiveTimeout = 30000;

  private int connectTimeout = 5000;


  public AgpCxfEndpointConfigurer(int receiveTimeout) {
    this.receiveTimeout = receiveTimeout;
  }

  public AgpCxfEndpointConfigurer(int receiveTimeout, int connectTimeout) {
    this.receiveTimeout = receiveTimeout;
    this.connectTimeout = connectTimeout;
  }

  @Override
  public void configure(AbstractWSDLBasedEndpointFactory factoryBean) {
    addMessageLoggingFeature(factoryBean);
  }

  @Override
  public void configureClient(Client client) {
    setClientTimeouts(client);
  }

  private void setClientTimeouts(Client client) {
    HTTPConduit conduit = (HTTPConduit) client.getConduit();
    HTTPClientPolicy policy = new HTTPClientPolicy();
    policy.setReceiveTimeout(receiveTimeout);
    policy.setConnectionTimeout(connectTimeout);
    conduit.setClient(policy);
  }

  private boolean addMessageLoggingFeature(AbstractWSDLBasedEndpointFactory factoryBean) {
    return factoryBean.getFeatures().add(messageLoggingFeature);
  }

  @Override
  public void configureServer(Server server) {
    // Do nothing here
  }

}