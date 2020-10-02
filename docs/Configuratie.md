# Configuratiebestand
In de resource map van het project staat het bestand config.json

## RequestHandlerImplementation
Hier wordt de klasse naam opgegeven van de requestHandlerImplementatie die gebruikt moet worden, bv: nl.haarlem.translations.zdstozgw.requesthandler.impl.BasicRequestHandler
Voor uitleg over de requesthandler, zie [ReplicationHandler.md](ReplicationHandler.md).

## Organisaties
In de stuurgegevens van het bericht bevinden zich de Zender en Ontvanger nodes. Daarin staan gemeentecodes, OpenZaak verwacht RSIN. Hier wordt de vertaling aangegeven.

## ZgwRolOmschrijving

Binnen de ZDS standaard zijn een aantal vast gedefinieerde rollen binnen de standaard gedefinieerd, binnden de ZWG standaard is dit niet het geval. De betreffende zds-rollen worden gekoppeld  aan zgw-rollen op basis van de hier omgeschreven waarden. 

ZDS 1.1 kent een zevental rollen bij het toevoegen van een nieuwe zaak, namelijk: 

- heeftBetrekkingOp: "BetrekkingOp"
- heeftAlsBelanghebbende: "Belanghebbende"
- heeftAlsInitiator: "Initiator"
- heeftAlsUitvoerende: "Uitvoerende"
- heeftAlsVerantwoordelijke: "Verantwoordelijke"
- heeftAlsGemachtigde: "Gemachtigde"
- heeftAlsOverigBetrokkene: "OverigeBetrokkene"

In OpenZaak kunnen rollen per zaaktype zelf benoemd worden. Hierdoor is het noodzakelijk om aan te geven hoe de ZDS rollen zich vertalen naar OpenZaak rollen. Voor het opzoeken van een roltype, gebruiken we de omschrijvingGeneriek. We gaan ervan uit dat in OpenZaak de zds rollen per zaaktype dezelfde omschrijving krijgt.

## Replication

Wanneer de openzaakbrug in replicatiemodus moet draaien, heeft deze extra informatie nodig over waar het oude legacy systeem is. Voor dit doel worden de volgende : geefZaakdetails, geefLijstZaakdocumenten en geefZaakdocumentLezen, zodat bij het ontbreken van deze informatie in het ZGW zaaksysteem deze vooraf gecopieerd kan worden. Deze service wordt dan aangeroepen met de betreffende soapaction en url. 

## Translations

Per ZDS operatie worden een aantal gegevens gespecificeerd voor de vertaling, namelijk:
- translation: een naam die de vertaling kenmerkt, bv: Translate ZDS 1.1 Generic actualiseerZaakstatus_Lk01
- path: het endpoint waarop deze service beschikbaar is, bijvoorbeeld: translate/generic/zds/OntvangAsynchroon
- soapAction: de soapActie waarvoor de vertaling geldt, bijvoorbeeld: http://www.egem.nl/StUF/sector/zkn/0310/actualiseerZaakstatus_Lk01
- implementation: naam van de klasse van de Converter die gebruikt moet worden voor translatie, bijvoorbeeld: nl.haarlem.translations.zdstozgw.converter.impl.translate.ActualiseerZaakStatusTranslator
- legacyservice: wanneer openzaakbrug in replicatie modus draait, de legacyservice is de endpoint naar het oude zakenmagazijn
- template: wanneer een converter een xml template moet gebruiken dan kan hier het pad opgegeven worden naar de desbetreffende template, bv src/main/java/nl/haarlem/translations/zdstozgw/converter/impl/genereerDocumentIdentificatie_Du02.xml
