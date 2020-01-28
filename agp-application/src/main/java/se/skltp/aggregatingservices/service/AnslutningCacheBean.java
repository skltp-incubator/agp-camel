package se.skltp.aggregatingservices.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skl.tp.behorighet.BehorighetHandler;
import se.skl.tp.behorighet.BehorighetHandlerImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.util.XmlGregorianCalendarUtil;

@Service
public class AnslutningCacheBean {
    private String agpHsaId;
    private String servicecontractNamespace;


    private BehorighetHandler behorighetHandler;
    private TakCache takCache;

    @Autowired
    public AnslutningCacheBean(TakCache takCache) {
        this.behorighetHandler = new BehorighetHandlerImpl(takCache);
        this.takCache = takCache;
    }

    public String getAgpHsaId() {
        return agpHsaId;
    }

    public void setAgpHsaId(String agpHsaId) {
        this.agpHsaId = agpHsaId;
    }

    public String getServicecontractNamespace() {
        return servicecontractNamespace;
    }

    public void setServicecontractNamespace(String servicecontractNamespace) {
        this.servicecontractNamespace = servicecontractNamespace;
    }

    public List<String> getReceivers(String senderId, String originalServiceConsumerId) {
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

    public boolean isAuthorizedConsumer(String senderId, String originalServiceConsumerId, String receiverId) {
        return behorighetHandler.isAuthorized(senderId, servicecontractNamespace, receiverId) ||
                behorighetHandler.isAuthorized(originalServiceConsumerId, servicecontractNamespace, receiverId);
    }

}