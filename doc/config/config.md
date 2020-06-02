# Konfiguration av AGP Camel

Konfigurering kan göras i filerna listade nedan. Vid respektive avsnitt finns information om funktion och vad som kan ändras.
  
 * application.properties  

För mer information om hur användare och lösenord för Hawtio konfigureras samt exempelfiler, se [Detaljerad konfiguration].
Loggning och hur det går till och kan konfigureras kan man läsa om här: [Loggning konfiguration]

### Application.properties ###
Spring-boot property fil som ligger under resources i jaren. Inställningarna kan överlagras enligt de sätt som Spring-boot föreskriver. 

|Nyckel|Defaultvärde/Exempel|Beskrivning|
|----|------------------|---------|
| server.port | 8881 | Porten som servern ska starta på |
| agp.host | localhost | DNS/ip där servern finns |
| management.endpoints.web.exposure.include | hawtio,jolokia | Porten som servern ska starta på |
| hawtio.authentication.enabled | true | Ska autentiserng användas till HawtIO? |
| hawtio.external.loginfile | C:/Temp/realm-custom.properties | Extern fil med password till Hawtio |
| endpoints.camelroutes.enabled | true | Porten som servern ska starta på |
| endpoints.camelroutes.read-only | true | Porten som servern ska starta på |
| ei.logicalAddress | 556500000 | Porten som servern ska starta på |
| ei.senderId | Sender1 | Porten som servern ska starta på |
| ei.findContentUrl | http://localhost:8082/findcontent | Porten som servern ska starta på |
| vp.instanceId | dev_env | Porten |
| reset.cache.url | http://${agp.host}:8091/resetcache | Porten |
| agp.status.url | http://${agp.host}:1080/status | Porten |
| takcache.use.behorighet.cache | true | Porten |
| takcache.use.vagval.cache | false | Porten |
| takcache.endpoint.address | http://localhost:8085/tak/teststub/SokVagvalsInfo/v2 | Porten |
| log.max.payload.size | 49152 | Porten |
