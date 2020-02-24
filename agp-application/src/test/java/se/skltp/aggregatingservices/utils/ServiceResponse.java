package se.skltp.aggregatingservices.utils;

import java.util.Map;
import lombok.Data;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;

@Data
public class ServiceResponse <T> {
  T object;
  ProcessingStatusType processingStatus;
  Map<String,Object> headers;
}
