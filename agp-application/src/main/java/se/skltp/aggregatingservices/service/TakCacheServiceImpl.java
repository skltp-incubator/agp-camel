package se.skltp.aggregatingservices.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skl.tp.behorighet.BehorighetHandler;
import se.skl.tp.behorighet.BehorighetHandlerImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.TakCacheLog;
import se.skltp.takcache.TakCacheLog.RefreshStatus;
import se.skltp.takcache.util.XmlGregorianCalendarUtil;

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
  public List<String> getReceivers(String senderId, String originalServiceConsumerId, String servicecontractNamespace) {
    // TODO where get this
    String agpHsaId ="";

    List<AnropsBehorighetsInfoType> anropsBehorigheter = takCache.getAnropsBehorighetsInfos();
    return anropsBehorigheter.stream().
        filter(anropsBehorighet -> anropsBehorighet.getTjansteKontrakt().equals(servicecontractNamespace)).
        filter(anropsBehorighet -> senderId.equals(anropsBehorighet.getSenderId()) || originalServiceConsumerId.equals(anropsBehorighet.getSenderId())).
        filter(anropsBehorighet -> !anropsBehorighet.getReceiverId().equals(HsaCache.DEFAUL_ROOTNODE)).
        filter(anropsBehorighet -> !anropsBehorighet.getReceiverId().equals(agpHsaId)).
        filter(anropsBehorighet -> !anropsBehorighet.getReceiverId().equals(BehorighetHandlerImpl.DEFAULT_RECEIVER_ADDRESS)).
        filter(anropsBehorighet -> XmlGregorianCalendarUtil.isTimeWithinInterval(XmlGregorianCalendarUtil.getNowAsXMLGregorianCalendar(),
            anropsBehorighet.getFromTidpunkt(), anropsBehorighet.getTomTidpunkt())).
        map(AnropsBehorighetsInfoType::getReceiverId).collect(Collectors.toList());
  }

  @Override
  public boolean isAuthorizedConsumer(String senderId, String originalServiceConsumerId, String receiverId, String servicecontractNamespace) {
    return behorighetHandler.isAuthorized(senderId, servicecontractNamespace, receiverId) ||
        behorighetHandler.isAuthorized(originalServiceConsumerId, servicecontractNamespace, receiverId);
  }


}
