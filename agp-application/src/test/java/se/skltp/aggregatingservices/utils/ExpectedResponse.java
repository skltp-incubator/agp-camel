package se.skltp.aggregatingservices.utils;

import java.util.HashMap;
import java.util.Map;
import se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum;

public class ExpectedResponse {
  private Map<String, Object[]> map = new HashMap<>();
  int numResponses=0;

  public void add(String producer, int responseSize, StatusCodeEnum statusCode, String errTxtPart){
    map.put(producer, new Object[]{statusCode, responseSize, errTxtPart});
    numResponses+=responseSize;
  }

  public StatusCodeEnum getStatusCode(String producer){
    return (StatusCodeEnum) map.get(producer)[0];
  }

  public String getErrTxtPart(String producer){
    return (String) map.get(producer)[2];
  }

  public int numProducers(){
    return map.size();
  }

  public int numResponses(){
    return numResponses;
  }

  public int numResponses(String producer){
    return (int) map.get(producer)[1];
  }



  public boolean contains(String producer){
    return map.containsKey(producer);
  }

}
