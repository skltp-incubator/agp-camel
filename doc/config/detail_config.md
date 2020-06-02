# Detaljerad konfiguration

I det här dokumentet finns detaljerad information om hur man konfigurerar AGP Camel.
Längre ner finns också exempelfiler som visar hur AGP Camel kan konfigureras.
Huvudsidan för konfigurering finns här: [AGP Camel konfigurering]
 
### Hawtio

##### Ställa in autentisering för Hawtio
I application-custom.properties kan man sätta propertyn:
`hawtio.authentication.enabled=true` 
till true eller false. Default är den false. Men det rekommenderas att ha autentisering på eftersom man kan ändra funktionalitet i AGP via Hawtio. Att sätta på autentisering innebär att man behöver konfigurera en user och password enligt nedan. 
##### Konfigurera ny user och password 
För att ändra/lägga till användare/lösenord för Hawtio: Generera en md5-hash av det password ni valt, till exempel med (på Linux): `printf  '%s' "<password>" | md5sum`.
     Skapa en ny login-fil, t.ex. realm-custom.properties. Den ska innehålla namn på user och hashat pw enligt:
     `<user>: MD5:<password hash>, user, admin`     
     Placera filen lämpligen tillsammans med övriga konfigurations-filer.
     I `application-custom.properties`: Lägg till sökväg till filen enligt:
     `hawtio.external.loginfile=<path>/realm-custom.properties`
     Installera om VP och starta. Kontrollera vilken port AGP är konfigurerad att starta på. Det ska nu gå att surfa till http://\<server\>:\<port\>/actuator/hawtio/ och logga in med vald user och password.
     
     
### Konfigurera loggning
Se anvisningar på sidan [Loggning konfigurering]

### Exempel på application-custom.properties
```
server.port=8888
agp.host=localhost
#spring.profiles.include=GetLaboratoryOrderOutcome

# Hawtio
management.endpoints.web.exposure.include=hawtio,jolokia
# Not set defaults to false
hawtio.authentication.enabled=true
hawtio.external.loginfile=C:/Temp/realm-custom.properties

# All access to actuator endpoints without security
#management.security.enabled=false
## Turn on actuator health check
#endpoints.health.enabled=true

# Allow to obtain basic information about camel routes (read only mode)
endpoints.camelroutes.enabled=true
endpoints.camelroutes.read-only=true

# Outgoing communication properties
#vp.host=localhost
#vp.port=8083
#findcontent.port=8081
#
#vp.address=http://${vp.host}:${vp.port}/vp


# Outgoing parameters for calling EI-FindContent
ei.logicalAddress=556500000
ei.senderId=SENDER1
ei.findContentUrl=http://localhost:8082/findcontent

# VP instance id used when calling VP with http
vp.instanceId=dev_env

reset.cache.url=http://${agp.host}:8091/resetcache
agp.status.url=http://${agp.host}:1080/status

#Takcache configuration
takcache.use.behorighet.cache=true
takcache.use.vagval.cache=false
takcache.endpoint.address=http://localhost:8085/tak/teststub/SokVagvalsInfo/v2

log.max.payload.size=49152

```

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [Loggning konfigurering]: <logging_configuration.md>
   [AGP Camel konfigurering]: <config.md>