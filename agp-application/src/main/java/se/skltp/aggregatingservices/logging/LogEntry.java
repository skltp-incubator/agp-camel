package se.skltp.aggregatingservices.logging;

import java.util.Map;
import lombok.Data;

@Data
public class LogData {

  String logMessage;
  String serviceImpl;
  String host;
  String componentId;
  String endpoint;
  String messageId;
  String businessCorrelationId;
  String payload;
  Map<String, Object> extrainfo;

  @Override
  public String toString() {
    return String.format(
        "LogMessage=%s\n"
            + "ServiceImpl=%s\n"
            + "Host=%s\n"
            + "ComponentId=%s\n"
            + "Endpoint=%s\n"
            + "MessageId=%s\n"
            + "BusinessCorrelationId=%s\n"
            + "ExtraInfo=\n%s\n"
            + "payload=%s"
        , logMessage
        , serviceImpl
        , host
        , componentId
        , endpoint
        , messageId
        , businessCorrelationId
        , getExtrainfoAsString()
        , payload
    );
  }

  private String getExtrainfoAsString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Map.Entry<String, Object> entry : extrainfo.entrySet()) {
      stringBuilder.append(String.format("-%s=%s\n",
          entry.getKey(),
          entry.getValue()));
    }
    return stringBuilder.toString();
  }
}





