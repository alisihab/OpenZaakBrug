package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/*	   "url":"https://fieldlab.westeurope.cloudapp.azure.com/documenten/api/v1/enkelvoudiginformatieobjecten/7d1835e4-0ae6-46d8-b6d3-7d0f14b5b0b7",
"identificatie":"19008",
"bronorganisatie":"823288444",
"creatiedatum":"2020-05-18",
"titel":"swf",
"vertrouwelijkheidaanduiding":"vertrouwelijk",
"auteur":"ISZF\\e.witteveen",
"status":"",
"formaat":"png",
"taal":"nld",
"versie":1,
"beginRegistratie":"2020-05-17T23:03:30.717976Z",
"bestandsnaam":"swf.png",
"inhoud":"https://fieldlab.westeurope.cloudapp.azure.com/documenten/api/v1/enkelvoudiginformatieobjecten/7d1835e4-0ae6-46d8-b6d3-7d0f14b5b0b7/download?versie=1",
"bestandsomvang":9182,
"link":"",
"beschrijving":"",
"ontvangstdatum":null,
"verzenddatum":null,
"indicatieGebruiksrecht":null,
"ondertekening":{
   "soort":"",
   "datum":null
},
"integriteit":{
   "algoritme":"",
   "waarde":"",
   "datum":null
},
"informatieobjecttype":"https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/informatieobjecttypen/05f2200b-b0c9-4151-88b0-363707f4d453",
"locked":false
*/

@Data
public class ZgwInformatieObject {
	@SerializedName("url")
	@Expose
	public String url;
	@SerializedName("uuid")
	@Expose
	public String uuid;
	
	@SerializedName("informatieobjecttype")
	@Expose
	public String informatieobjecttype;
	
	@SerializedName("identificatie")
	@Expose
	public String identificatie;

	@SerializedName("bronorganisatie")
	@Expose
	public String bronorganisatie;
	
	@SerializedName("creatiedatum")
	@Expose
	public String creatiedatum;

	@SerializedName("titel")
	@Expose
	public String titel;

	@SerializedName("vertrouwelijkheidaanduiding")
	@Expose
	public String vertrouwelijkheidaanduiding;

	@SerializedName("auteur")
	@Expose
	public String auteur;
	
	@SerializedName("status")
	@Expose
	public String status;
	
	@SerializedName("formaat")
	@Expose
	public String formaat;
		
	@SerializedName("taal")
	@Expose
	public String taal;

	@SerializedName("versie")
	@Expose
	public String versie;

	@SerializedName("beginRegistratie")
	@Expose
	public String beginRegistratie;

	@SerializedName("bestandsnaam")
	@Expose
	public String bestandsnaam;

	@SerializedName("inhoud")
	@Expose
	public String inhoud;

	@SerializedName("bestandsomvang")
	@Expose
	public String bestandsomvang;
	
	@SerializedName("link")
	@Expose
	public String link;

	@SerializedName("beschrijving")
	@Expose
	public String beschrijving;

	@SerializedName("ontvangstdatum")
	@Expose
	public String ontvangstdatum;

	@SerializedName("verzenddatum")
	@Expose
	public String verzenddatum;


	@SerializedName("indicatieGebruiksrecht")
	@Expose
	public String indicatieGebruiksrecht;

	/*
	"ondertekening":{
	   "soort":"",
	   "datum":null
	},
	"integriteit":{
	   "algoritme":"",
	   "waarde":"",
	   "datum":null
	},
	*/
	
	@SerializedName("locked")
	@Expose
	public String locked;

}