# OpenZaakBrug #

## Over OpenZaakBrug

De API-standaarden voor zaakgericht werken stellen gemeenten in staat om de informatievoorziening rondom zaakgericht werken in te richten volgens de informatiekundige visie Common Ground. Hiermee zetten ze een eerste stap in de richting van een modernisering van het ICT-landschap. Dit helpt bij het (ont)koppelen van processystemen en de opslagcomponenten waar documenten en overige informatie in zijn opgeslagen.

Wij willen niet meer investeren in de oude koppelingen van het zaaksgericht werken (ZDS standaard) en willen zo snel mogelijk de nieuwe Zaakgericht Werken-api&#39;s gebruiken. Daarom willen we zo snel mogelijk de nieuwe opslaginfrastructuur op basis van deze standaard aanbieden. Daarna kunnen alle nieuwe applicaties via deze nieuwe standaard aansluiten.

Omdat de bestaande applicaties niet de nieuwe standaard ondersteunen kunnen deze niet aangesloten worden op het nieuwe zaaksysteem. Door een component te ontwikkelen welke de communicatie omzet van de oude standaard (ZDS) naar de nieuwe (ZGW) hoeven de bestaande applicaties niet op hetzelfde moment te worden aangepast.

Het uiteindelijke doel is om alle applicaties op de nieuwe Zaakgericht Werken-api&#39;s te krijgen, met deze aanpak ontstaat er tijd om beheerst en onder regie dit uit te voeren.

Wij willen hiervoor een vertaal component maken die ervoor zorgt dat de bestaande ZDS- applicaties kunnen aansluiten op de nieuwe zaak en document opslagcomponenten.

Hierbij hebben we de volgende uitgangspunten:

- Het doel is het aansluiten van de bestaande ZDS-applicaties
- De oplossing hoeft niet aan de volledige ZDS-standaard te voldoen (minimaal vereiste functionele ondersteuning)
- De programmatuur moet makkelijk herbruikbaar zijn
- De broncode moet overdraagbaar zijn en naar wens aanpasbaar zijn door gemeenten
- Het betreft een tijdelijke oplossing, de leveranciers moeten over naar ZGW

## Technische informatie

informatie over de installatie kijk in : [INSTALL.md]()  

infomatie over de replicatie kijk in : [REPLICATION.md]()  

## Volwassenheid van het product

Deze applicatie is een proof-of-concept en wordt verder uitgewerkt zodat dit als voorbeeld kan dienen voor een software-leveranier.
Op deze manier is het duidelijk welke functionaliteiten de gemeente nodig heeft

Onderdelen en de stand van zaken:
- **USE_ZDS** : Beta en logt berichtenverkeer en fouten naar de tabel: RequestResponseCycle
- **USE_ZDS_AND_REPLICATE_2_ZGW** : Nog niet geimplementeerd
- **USE_ZGW_AND_REPLICATE_2_ZDS**:  Nog niet geimplementeerd
- **USE_ZGW**  : Berichtenverkeer vanuit SoapUI getest, work in progress