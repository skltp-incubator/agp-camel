package se.skltp.aggregatingservices.service;

import java.util.Date;
import java.util.List;
import se.skltp.takcache.TakCacheLog;

public interface TakCacheService {

  TakCacheLog refresh();

  boolean isInitalized();

  boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId);

  Date getLastResetDate();

  TakCacheLog getLastRefreshLog();

  List<String> getReceivers(String senderId, String originalServiceConsumerId, String servicecontractNamespace);

  boolean isAuthorizedConsumer(String senderId, String originalServiceConsumerId, String receiverId,
      String servicecontractNamespace);
}
