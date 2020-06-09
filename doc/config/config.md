# Konfiguration av AGP Camel

Konfigurering kan göras i filerna listade nedan. Vid respektive avsnitt finns information om funktion och vad som kan ändras.
  
 * application.properties  

För mer information om hur användare och lösenord för Hawtio konfigureras samt exempelfiler, se [Detaljerad konfiguration].
Loggning och hur det går till och kan konfigureras kan man läsa om här: [Loggning konfigurering]

### Application.properties ###
Spring-boot property fil som ligger under resources i jaren. Inställningarna kan överlagras enligt de sätt som Spring-boot föreskriver. 

|Nyckel|Defaultvärde/Exempel|Beskrivning|
|----|------------------|---------|
| server.port | 8881 | Port som servern ska starta på |
| agp.host | localhost | DNS/ip där servern finns |
| management.endpoints.web.exposure.include | hawtio,jolokia | Default aktivera övervakning via Hawwtio |
| hawtio.authentication.enabled | true | Ska autentiserng användas till HawtIO? |
| hawtio.external.loginfile | C:/Temp/hawtio_users.properties.properties | Extern fil med user/password till Hawtio |
| endpoints.camelroutes.enabled | true | Medger tillgång till information om de Camel-routes som finns |
| endpoints.camelroutes.read-only | true | Tillgång till endpoints bara i read-only mode |
| ei.logicalAddress | 556500000 | EI's logiska adress |
| ei.senderId | Sender1 | Porten som servern ska starta på |
| ei.findContentUrl | http://localhost:8082/findcontent | URL för att söka i EI |
| vp.instanceId | dev_env | VP's instans-ID |
| reset.cache.url | http://${agp.host}:8091/resetcache | URL för att ladda om cache |
| agp.status.url | http://${agp.host}:1080/status | URL till statusfunktionen i AGP |
| takcache.use.behorighet.cache | true | Ska behörigheter användas i TAK-cachen? |
| takcache.use.vagval.cache | false | Ska vägval användas i TAK-cachen? |
| takcache.endpoint.address | http://localhost:8085/tak/teststub/SokVagvalsInfo/v2 | Var ska cachen förnyas, dvs var finns den installerade TAK:en? |
| log.max.payload.size | 49152 | Max storlek på payloaden i meddelandet |

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [Loggning konfigurering]: <logging_configuration.md>
   [Detaljerad konfiguration]: <detail_config.md>