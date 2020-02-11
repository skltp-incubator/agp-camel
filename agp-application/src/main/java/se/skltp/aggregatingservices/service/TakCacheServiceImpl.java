package se.skltp.aggregatingservices.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skl.tp.behorighet.BehorighetHandler;
import se.skl.tp.behorighet.BehorighetHandlerImpl;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.TakCacheLog;
import se.skltp.takcache.TakCacheLog.RefreshStatus;

@Service
public class TakCacheServiceImpl implements TakCacheService {

  TakCache takCache;

  BehorighetHandler behorighetHandler;


  TakCacheLog takCacheLog = null;

  Date lastResetDate;

  @Autowired
  public TakCacheServiceImpl(TakCache takCache) {
    this.takCache = takCache;
    behorighetHandler = new BehorighetHandlerImpl(takCache);
  }

  @Override
  public TakCacheLog refresh() {
    // TODO call refresh with filter on targetNamespaces
    // Update takcache to handle multiple targetNS
    takCacheLog = takCache.refresh();
    lastResetDate = new Date();
    return takCacheLog;
  }

  @Override
  public boolean isInitalized() {
    return takCacheLog != null && takCacheLog.getRefreshStatus() != RefreshStatus.REFRESH_FAILED;
  }

  @Override
  public boolean isAuthorized(String senderId, String servicecontractNamespace, String receiverId) {
    return behorighetHandler.isAuthorized(senderId, servicecontractNamespace, receiverId);
  }

  @Override
  public Date getLastResetDate() {
    return lastResetDate;
  }

  @Override
  public TakCacheLog getLastRefreshLog(){
    return takCacheLog;
  }

  @Override
  public boolean isAuthorizedConsumer(Authority authority) {
    return behorighetHandler.isAuthorized(authority.getSenderId(), authority.getServicecontractNamespace(), authority.receiverId) ||
        behorighetHandler.isAuthorized(authority.getOriginalSenderId(), authority.getServicecontractNamespace(), authority.receiverId);
  }


}
