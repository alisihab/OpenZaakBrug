# RequestHandler

## Doel
De requesthandler is in het leven geroepen als een wrapper voor de Converter. Door middel van de requesthandler kan er meer centrale logica toegepast worden alvorens de vertaling te doen, mocht dat nodig zijn. De requesthandler kent 1 abstracte methode: execute. Er bestaan nu twee implementaties van de requesthandler. De BasicRequestHandler en de ReplicationRequestHandler. 

## BasicRequestHandler
In deze implementatie zit geen extra logica.

## ReplicationRequestHanlder
### Logging
In deze implementatie zit extra logging logica. De inkomende en uitgaande berichten van openzaakbrug worden in een database opgeslagen. Dit wordt opgeslagen in de tabel REQUEST_RESPONSE_CYCLE. Ook derequests en responses naar OpenZaak worden gelogd in de tabel INTERIM_REQUEST_RESPONSE_CYCLE.

### Replicatie
Naar aanleiding van de configuratie van de replicatie in het config.json bestand, kunnen berichten zowel naar het nieuwe zakenmagazijn als het oude gestuurd worden.