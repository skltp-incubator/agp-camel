package se.skltp.aggregatingservices.consumer;

import se.skltp.aggregatingservices.utils.ServiceResponse;

public interface ConsumerService {


  public ServiceResponse callService(String patientId);

  public ServiceResponse callService(String logicalAddress, String patientId);

  public ServiceResponse callService(String senderId, String originalId,  String logicalAddress, String patientId  );

  public ServiceResponse callServiceWithWrongContract();


}
