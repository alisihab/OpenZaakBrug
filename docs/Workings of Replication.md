**Replicatie**

Als je als gemeente wil over schakelen van huidige naar nieuwe wereld en wil ik mijn risico&#39;s minimaliseren.

Dit door het volgende proces te doorlopen:

1. Plaatst de OpenZaakBrug tussen het bestaande zaaksysteem en de TSA's (taak specifieke applicaties), waarbij de replicatie-modus staat op: **USE_ZDS**. 

   Waarom: Hiermee wordt getest of het berichtenverkeer blijft lopen de OpenZaakBrug fungeert als een doorgeef luik.

2. Converteer de bestaande zaaktype-definities en laad deze in de ZTC van OpenZaak. Dit kan met bijvoorbeeld de INavigator-import-tool of met de hand. 

   Waarom: Met deze inrichting komen eventuele problemen met de bestaande zaaktype definities aan het licht, of deze inderdaad passen in de nieuwe structuur

3. In de volgende stap laten we alle communicatie tussen het bestaande zaaksysteem en de TSA's een afschrift naar OpenZaak gaan. Hierdoor wordt er dus gerepliceerd naar OpenZaak en ontstaat hier ook vulling, wel blijft de bron nog het "oude"-zaaksysteem. Hierbij moet de replicatie-modus staan op: **USE_ZDS_AND_REPLICATE_2_ZGW**

   Waarom: Hiermee wordt getest of het berichtenverkeer ook past in OpenZaak en tevens wordt er al vulling opgebouwd in OpenZaak, zodat ook bekend is of de performance goed is.

4. Wanneer er genoeg vertrouwen is opgebouwd in OpenZaak, kan deze de bron worden. Dit betekend dat de OpenZaakBrug al volledig volledig draait. Wel doet het systeem nog een bevraging op het "oude"-zaaksysteem wanneer een zaak of document niet wordt gevonden in OpenZaak. Op deze manier is het niet nodig om alle gegevens gemigreerd te hebben en toch al op OpenZaak te draaien. Voor de zekerheid wordt het bericht van de TSA ook nog doorgestuurd naar het "oude"-zaaksysteem, zodat deze nog steeds volledig meeloopt. Ook worden de id's nog door het "oude"-zaaksysteem aangemaakt. Hierbij moet de replicatie-modus staan op: **USE_ZGW_AND_REPLICATE_2_ZDS**
   Waarom: Hiermee wordt aangetoond dat de translatie naar OpenZaak werkt, zonder dat het oude zaaksysteem niet meer synchroon loopt.

5. Deze stap is de migratie van alle gegevens uit het oude-zaaksysteem naar OpenZaak, dit kan door alle zaken en documenten te bevragen op de OpenZaakBrug of via scripts.

6. Wanneer alle zaken en documenten in het zaaksysteem staan, dan moeten in de database de ZaakIdentificatieHuidige en DocumentIdentificatieHuidige aangepast worden naar de laatst uitgegevens nummers (en de betreffende prefix), waarna de replicatie-modus gezet kan worden op: **USE_ZGW**. De OpenZaakBrug draait daamee volledig op OpenZaak het het "oude"-zaaksysteem kan worden uitgezet.

![openzaakbrug-replication](media/openzaakbrug-replication.png)

Op dit moment wordt AL het berichtenverkeer in de database gelogd met de eventuele fouten voor nadere inspectie, zodra de koppeling robuust werkt kan dit worden aangepast.

Verhaal met plaatje:

_Replicatie standen:_

- **USE_ZDS** (proxyZds) : Stuur door naar het legacy zaaksysteem en logt berichtenverkeer en fouten naar de tabel: RequestResponseCycle
- **USE_ZDS_AND_REPLICATE_2_ZGW** (proxyZdsAndReplicateToZgw) : Stuur door naar het legacy zaaksysteem en logt berichtenverkeer en fouten naar de tabel: RequestResponseCycle, daarnaast nog een afschrift naar OpenZaak
- **USE_ZGW_AND_REPLICATE_2_ZDS** (convertToZgwAndReplicateToZds) : Converteer de berichten naar OpenZaak logt berichtenverkeer en fouten naar de tabel: RequestResponseCycle, daarnaast nog een afschrift naar het legacy zaaksysteem voor de zekerheid
- **USE_ZGW** (convertToZgw) : Converteer de berichten naar OpenZaak logt berichtenverkeer en fouten naar de tabel: RequestResponseCycle

Hoe werkt de replicatie in de stand USE_ZGW_AND_REPLICATE_2_ZDS:

- Wanneer de informatie nog niet in OpenZaak staat:

1. _Er wordt een zds-client-bericht  vanuit de zds-client naar de overbrenger verstuurd_
2. _De zaak/document informatie is niet bekend in ZGW registratie_
3. _De zaak/document informatie wordt opgehaald uit het bestaande zaaksysteem_
4. _De zaak/document informatie wordt opgeslagen in de ZGW registratie_
5. _Het zds-client-bericht wordt uitgevoerd op de ZGW registratie_
6. Voor de volledigheid en om in sync te houden wordt het zds-client-bericht doorgestuurd naar het bestaande zaaksysteem
7. Al het Soap berichtenverkeer is gelogd, inclusief eventuele foutmeldingen

- Wanneer de informatie WEL in OpenZaak staat: 

1. _Er wordt een zds-client-bericht  vanuit de zds-client naar de overbrenger verstuurd_
2. _De zaak/document informatie is bekend_
3. ~~_De zaak/document informatie wordt opgehaald uit het bestaande zaaksysteem_~~
4. ~~_De zaak/document informatie wordt opgeslagen in de ZGW registratie_~~
5. _Het zds-client-bericht wordt uitgevoerd op de ZGW registratie_
6. _Voor de volledigheid en om in sync te houden wordt het zds-client-bericht doorgestuurd naar het bestaande zaaksysteem_
7. Al het Soap berichtenverkeer is gelogd, inclusief eventuele foutmeldingen

- Bij fouten: blijf werken, zodat de gebruikers geen last hebben:

1. _Bij optreden fout wordt de fout meteen gelogd_
  2. _Het orginele bericht wordt door gestuurd naar het bestaande zaaksysteem_
  3. _De reactie van het bestaande zaaksysteem wordt door gestuurd naar de ZDS client
