# StUF ZDS to ZGW API translator 

[![Build Status](https://travis-ci.org/Haarlem/zds-stuf-to-zgw-api-translator.svg?branch=master)](https://travis-ci.org/Haarlem/zds-stuf-to-zgw-api-translator)

This project is a proof of concept of a service that can translate StUF ZDS 1.2 SOAP messages to their corresponding ZGW 1.0 API calls.

The service can be used to connect legacy applications that do not yet support ZGW 1.0 to an ZGW 1.0 ZRC. This makes it possible for organisations to migrate to
ZGW 1.0 without having to wait until all vendors support it.

### Prerequisites

### Java
Java 11 or higher is needed to run the project. The project is tested with OpenJDK 11

### Lombok 
This project uses [Lombok](https://projectlombok.org/). No setup is required to run the application after building with Maven.
If compilation in an IDE is needed, setup may me applicable. 
Lombok plugins are available for all major Java IDE's ([examples](https://www.baeldung.com/lombok-ide))

### Installation ###

Clone the repo and build and run the service with Maven

```
$ git clone https://github.com/Haarlem/zds-stuf-to-zgw-api-translator/
$ cd zds-stuf-to-zgw-api-translator 
```

Use configuration from examples or provide local settings
```
$ mv src/main/resources/application.properties_example src/main/resources/application.properties
$ mv src/main/resources/config.json_example src/main/resources/config.json
```
Start the service
```
$ mvn spring-boot:run
```
The service is now available on port 8080 or the port provided in application.properties

#### Setup ZRC
This services depends on an implementation of [API standaard zaakgericht werken](https://www.vngrealisatie.nl/producten/api-standaarden-zaakgericht-werken)
The current version of this service is (partially) tested with [OpenZaak](https://github.com/open-zaak/open-zaak) as backend.

### Configuration
The application uses two configuration files, located in the resources folder. Examples are provided.

| File        | Usage          | 
| ------------- | ------------- | 
| application.properties | Server settings like portnumbers and ZGW api url | 
| config.json | Runtime configuration like zaaktypes, organisaties etc |

### Using the service
The service can receive and translate StUF ZDS 1.2 SOAP messages.
 
Currenlty these SOAP actions and operations are partly implemented. Some properties are set with default values and are 
not (yet) mapped from ZDS to ZGW.

|SOAP action |Operation| Endpoint |
| ----- | ----- |------ |
| BeantwoordVraag | geefZaakDetails | http://localhost:8080/BeantwoordVraag | 
| |geefLijstZaakdocumenten  |
| OntvangAsynchroon | creeerZaak | http://localhost:8080/OntvangAsynchroon |
| | voegZaakdocumentToe | |

Example SOAP messages are provied in the /examples/soap folder
 
 
 
### JWT enpoint
The project contains an enpoint that emits a valid JWT based on the secret provided in the properties file.
This can be used when using the ZGW API in tools like Postman. Only use this in test environments.
The enpoint is dissabled by default and can be enabled by setting:

```
nl.haarlem.translations.zdstozgw.enableJWTEntpoint = true
``` 
