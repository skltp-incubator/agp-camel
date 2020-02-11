package se.skltp.aggregatingservices.service;

import java.util.Date;
import se.skltp.takcache.TakCacheLog;

public interface TakCacheService {

  TakCacheLog refresh();

  boolean isInitalized();

  boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId);

  Date getLastResetDate();

  TakCacheLog getLastRefreshLog();

  boolean isAuthorizedConsumer(Authority authority);
}
