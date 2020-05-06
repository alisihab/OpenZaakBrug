package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwZaakType {
	/*
      {
         "url":"https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/zaaktypen/8a0ecb65-c3fd-4110-8599-297f84236ae5",
         "identificatie":"B1026",
         "omschrijving":"Minimaregeling aanvraag",
         "omschrijvingGeneriek":"",
         "vertrouwelijkheidaanduiding":"vertrouwelijk",
         "doel":"Dit werkproces betreft het beoordelen van een aanvraag voor een minimaregeling. Een minimaregeling is een inkomensondersteunende voorziening voor mensen met een inkomen dat niet hoger is dan 110% van het sociaal minimum. De gemeente kan deze (gedeeltelijke) vergoeding toekennen voor kosten van witgoedapparatuur, kosten voor kinderen en kosten in verband met ouderdom, een handicap of chronische ziekte. Zie voor de minimaregeling voor de kosten in verband met een ziektekostenverzekering werkproces B1261 \"Collectieve aanvullende zorgverzekering\".",
         "aanleiding":"Aanvraag",
         "toelichting":"",
         "indicatieInternOfExtern":"intern",
         "handelingInitiator":"aanvragen",
         "onderwerp":"onderwerp",
         "handelingBehandelaar":"handelingBehandelaar",
         "doorlooptijd":"P30D",
         "servicenorm":null,
         "opschortingEnAanhoudingMogelijk":true,
         "verlengingMogelijk":false,
         "verlengingstermijn":null,
         "trefwoorden":[

         ],
         "publicatieIndicatie":true,
         "publicatietekst":"",
         "verantwoordingsrelatie":[

         ],
         "productenOfDiensten":[

         ],
         "selectielijstProcestype":"https://referentielijsten-api.vng.cloud/api/v1/procestypen/e1b73b12-b2f6-4c4e-8929-94f84dd2a57d",
         "referentieproces":{
            "naam":"Verzoeken behandelen",
            "link":"https://referentielijsten-api.vng.cloud/api/v1/procestypen/3030daa1-d516-4cd9-8276-ef0977e32b20"
         },
         "catalogus":"https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/catalogussen/782ae7eb-503f-4235-aac0-015d28381934",
         "statustypen":[
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/9a11d084-605c-4f3e-8401-2658f6c44dc2",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/8a47915c-6ff2-455d-9f7a-7d976f9628af",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/11a7c5ab-9b39-47f4-bd10-dadedc3938d4",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/1c3124fa-db88-459d-bee7-f1948d3fc780",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/92988b9a-b051-4c83-a6fd-ec515da5d73a",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/c8572fcb-5dc9-4d08-82e0-5d858a79fbac",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/afd7999b-d304-4acd-b1e2-a2fe714669f1",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/statustypen/0af4c8a5-3bb6-4211-b99f-d31c106dec40"
         ],
         "resultaattypen":[
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/resultaattypen/610b1259-2c7b-413d-b93b-8a861963b69c",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/resultaattypen/3e387504-0ce3-4021-abba-b0f0748ea6be",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/resultaattypen/6626a4f2-2ef3-4f15-bdee-8c61e204bddc",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/resultaattypen/13e1c201-4277-416a-8d47-fa17f0a8bec3"
         ],
         "eigenschappen":[

         ],
         "informatieobjecttypen":[
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/84a3aca4-58ca-465a-9f66-dfb5bbb84ccc",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/065dc0d7-1afa-4e80-b9bf-281680fda6fd",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/a7eceae7-2eda-4564-a721-785494a9929a",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/ad5714b7-caf6-45f5-841f-7bca9ee0d586",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/280adf17-d5ab-4525-b2f9-b10b33c8a7d7",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/5ffd0e49-ddb7-4289-9365-a1de4b998b2a",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/6a35f1cb-c6ea-4f03-b126-4e7138431c68",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/e208d29e-8a19-4e40-b8a9-78a57b334c80",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/4208ac80-7587-4a42-8fc7-6ca25e5493e0",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/532c7e0c-e3a9-496b-b713-2001ac17dae1",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/d7da83c3-9847-495e-ac00-5df71e49f547",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/7ae962cd-3328-49b2-a31d-16512aaed379",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/2460a8d9-966c-4dd5-b141-4ea1717b65db",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/63ec67b5-4ea3-4034-8b61-a3e67499736d",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/05f2200b-b0c9-4151-88b0-363707f4d453",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/88d5d73f-5a29-4f89-9d82-ee8fc627b991",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/a1871ae1-d159-4670-b693-d325da44b3d4"
         ],
         "roltypen":[
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/roltypen/55fa4bf9-8c77-4721-aa7f-ae4c9ab2b290",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/roltypen/bb7ad44f-5aef-41a0-a9ec-b493c701addd",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/roltypen/717860d7-6503-4c99-965b-b38d9a889d24",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/roltypen/c462620a-0c9c-46de-82a0-53867d6c91ef",
            "https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/roltypen/07ee713a-d315-45ff-9456-58a8d96e6dff"
         ],
         "besluittypen":[

         ],
         "deelzaaktypen":[

         ],
         "gerelateerdeZaaktypen":[

         ],
         "beginGeldigheid":"2020-03-17",
         "eindeGeldigheid":null,
         "versiedatum":"2020-03-17",
         "concept":false
      }
   ]
*/
	
	@SerializedName("url")
	@Expose
	public String url;
	@SerializedName("uuid")
	@Expose
	public String uuid;
	@SerializedName("identificatie")
	@Expose
	public String identificatie;
	@SerializedName("omschrijving")
	@Expose
	public String omschrijving;
}
