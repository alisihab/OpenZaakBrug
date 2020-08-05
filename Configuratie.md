# Configuratiebestand
In de resource map van het project staat het bestand config.json

## RequestHandlerImplementation
Hier wordt de klasse naam opgegeven van de requestHandlerImplementatie die gebruikt moet worden, bv: nl.haarlem.translations.zdstozgw.requesthandler.impl.BasicRequestHandler
Voor uitleg over de requesthandler, zie [ReplicationHandler.md](./ReplicationHandler.md).

## Replication
Wanneer de openzaakbrug in replicatiemodus moet draaien, moet gebruik gemaakt worden van de ReplicationRequestHandler implementatie. Tevens moeten het een en ander geconfigureerd worden, namelijk:
- enableZDS: als het inkomende bericht naar ZDS (oude zakenmagazijn) moet, dan moet deze op true
- enableZGW: als het inkomende bericht naar ZGW (nieuwe zakenmagazijn, openzaak) moet, dan moet deze op true
- responseType: wanneer zowel ZDS als ZGW enabled zijn, dan moet aangegeven worden welk van de twee antwoorden teruggestuurd moet worden naar de client

## Organisaties
In de stuurgegevens van het bericht bevinden zich de Zender en Ontvanger nodes. Daarin staan gemeentecodes, OpenZaak verwacht RSIN. Hier wordt de vertaling aangegeven.

## Translations
Per ZDS operatie worden een aantal gegevens gespecificeerd voor de vertaling, namelijk:
- translation: een naam die de vertaling kenmerkt, bv: ZDS 1.1 Generic actualiseerZaakstatus_Lk01
- soapAction: de soapActie waarvoor de vertaling geldt, bv: http://www.egem.nl/StUF/sector/zkn/0310/actualiseerZaakstatus_Lk01
- (Optioneel) applicatie: wanneer de vertaling alleen voor een specifieke applicatie geldt, bv GWS4all  
- template: wanneer een converter een xml template moet gebruiken dan kan hier het pad opgegeven worden naar de desbetreffende template, bv src/main/java/nl/haarlem/translations/zdstozgw/converter/impl/genereerDocumentIdentificatie_Du02.xml
- implementation: naam van de klasse van de Converter die gebruikt moet worden voor translatie, bv: nl.haarlem.translations.zdstozgw.converter.impl.ActualiseerZaakStatusConverter
- legacyservice: wanneer openzaakbrug in replicatie modus draait, de legacyservice is de endpoint naar het oude zakenmagazijn

## ZgwRolOmschrijving
ZDS 1.1 kent een zestal rollen bij het toevoegen van een nieuwe zaak, namelijk: 
- heeftAlsBelanghebbende
- heeftAlsInitiator
- heeftAlsUitvoerende
- heeftAlsVerantwoordelijke
- heeftAlsGemachtigde
- heeftAlsOverigBetrokkene

In OpenZaak kunnen rollen per zaaktype zelf benoemd worden. Hierdoor is het noodzakelijk om aan te geven hoe de ZDS rollen zich vertalen naar OpenZaak rollen. Voor het opzoeken van een roltype, gebruiken we de omschrijvingGeneriek. We gaan ervan uit dat in OpenZaak de zds rollen per zaaktype dezelfde omschrijving krijgt.