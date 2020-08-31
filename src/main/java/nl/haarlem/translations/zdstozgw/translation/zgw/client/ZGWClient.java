package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.services.HttpService;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZGWClient {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${openzaak.baseUrl}")
    private String baseUrl;

    @Value("${zgw.endpoint.roltype:/catalogi/api/v1/roltypen}")
    private String endpointRolType;

    @Value("${zgw.endpoint.rol:/zaken/api/v1/rollen}")
    private String endpointRol;

    @Value("${zgw.endpoint.zaaktype:/catalogi/api/v1/zaaktypen}")
    private String endpointZaaktype;

    @Value("${zgw.endpoint.status:/zaken/api/v1/statussen}")
    private String endpointStatus;

    @Value("${zgw.endpoint.statustype:/catalogi/api/v1/statustypen}")
    private String endpointStatustype;

    @Value("${zgw.endpoint.zaakinformatieobject:/zaken/api/v1/zaakinformatieobjecten}")
    private String endpointZaakinformatieobject;

    @Value("${zgw.endpoint.enkelvoudiginformatieobject:/documenten/api/v1/enkelvoudiginformatieobjecten}")
    private String endpointEnkelvoudiginformatieobject;

    @Value("${zgw.endpoint.zaak:/zaken/api/v1/zaken}")
    private String endpointZaak;

    @Value("${zgw.endpoint.informatieobjecttype:/catalogi/api/v1/informatieobjecttypen}")
    private String endpointInformatieobjecttype;

    @Autowired
    HttpService httpService;

    @Autowired
    RestTemplateService restTemplateService;

    private String post(String url, String json)   {
        log.debug("POST: " + url + ", json: " + json);
        HttpEntity<String> entity = new HttpEntity<String>(json, restTemplateService.getHeaders());
        try {
        	var zgwResponse = restTemplateService.getRestTemplate().postForObject(url, entity, String.class);
        	log.debug("POST response: " + zgwResponse);
        	return zgwResponse;
		} catch (HttpStatusCodeException hsce) {
			json = json.replace("{", "{\n").replace("\",", "\",\n").replace("\"}", "\"\n}");
			var response = hsce.getResponseBodyAsString().replace("{", "{\n").replace("\",", "\",\n").replace("\"}", "\"\n}");
			var details = "--------------POST:\n" + url + "\n" + json + "\n--------------RESPONSE:\n" + response;			
			log.warn("POST naar OpenZaak: " + url + " gaf foutmelding:\n" + details, hsce);
			throw new ConverterException("POST naar OpenZaak: " + url + " gaf foutmelding:" + hsce.toString(), details, hsce);
		} catch (org.springframework.web.client.ResourceAccessException rae) {
			log.warn("POST naar OpenZaak: " + url + " niet geslaagd", rae);
			throw new ConverterException("POST naar OpenZaak: " + url + " niet geslaagd", rae);
		}
    }

    private String get(String url, Map<String, String> parameters)  {
        if (parameters != null) {
            url = getUrlWithParameters(url, parameters);
        }
        log.debug("GET: " + url);
        HttpEntity entity = new HttpEntity(restTemplateService.getHeaders());
        try {
        	ResponseEntity<String> response = restTemplateService.getRestTemplate().exchange(url, HttpMethod.GET, entity, String.class);
        	var zgwResponse = response.getBody();
        	log.debug("GET response: " + zgwResponse);
        	return zgwResponse;
		} catch (HttpStatusCodeException hsce) {
			var response = hsce.getResponseBodyAsString().replace("{", "{\n").replace("\",", "\",\n").replace("\"}", "\"\n}");
			var details = "--------------GET:\n" + url + "\n--------------RESPONSE:\n" + response; 
			log.warn("GET naar OpenZaak: " + url + " gaf foutmelding:\n" + details, hsce);
			throw new ConverterException("GET naar OpenZaak: " + url + " gaf foutmelding:" + hsce.toString(), details, hsce);
		} catch (org.springframework.web.client.ResourceAccessException rae) {
			log.warn("GET naar OpenZaak: " + url + " niet geslaagd", rae);
			throw new ConverterException("GET naar OpenZaak: " + url + " niet geslaagd", rae);
		}        
    }

    private String delete(String url)  {
        log.debug("DELETE: " + url);
        HttpEntity entity = new HttpEntity(restTemplateService.getHeaders());
        try {
        	ResponseEntity<String> response = restTemplateService.getRestTemplate().exchange(url, HttpMethod.DELETE, entity, String.class);
        	var zgwResponse = response.getBody();
        	log.debug("DELETE response: " + zgwResponse);
        	return zgwResponse;
		} catch (HttpStatusCodeException hsce) {
			var response = hsce.getResponseBodyAsString().replace("{", "{\n").replace("\",", "\",\n").replace("\"}", "\"\n}");
			var details = "--------------DELETE:\n" + url + "\n--------------RESPONSE:\n" + response; 
			log.warn("GET naar OpenZaak: " + url + " gaf foutmelding:\n" + details, hsce);
			throw new ConverterException("DELETE naar OpenZaak: " + url + " gaf foutmelding:" + hsce.toString(), details, hsce);
		} catch (org.springframework.web.client.ResourceAccessException rae) {
			log.warn("GET naar OpenZaak: " + url + " niet geslaagd", rae);
			throw new ConverterException("DELETE naar OpenZaak: " + url + " niet geslaagd", rae);
		}        
    }
    
    private String put(String url, String json)  {
        log.debug("PUT: " + url + ", json: " + json);
        HttpEntity<String> entity = new HttpEntity<String>(json, restTemplateService.getHeaders());
        try {
//        	var zgwResponse = restTemplateService.getRestTemplate().postForObject(url, entity, String.class);
        	ResponseEntity<String> response = restTemplateService.getRestTemplate().exchange(url, HttpMethod.PUT, entity, String.class);
        	var zgwResponse = response.getBody();
        	log.debug("PUT response: " + zgwResponse);
        	return zgwResponse;
//    	
//    	
//    	log.debug("PUT: " + url + ", json: " + json);
//        HttpEntity entity = new HttpEntity(restTemplateService.getHeaders());
//        try {
//        	ResponseEntity<String> response = restTemplateService.getRestTemplate().exchange(url, HttpMethod.PUT, entity, String.class);
//        	var zgwResponse = response.getBody();
//        	log.debug("PUT response: " + zgwResponse);
//        	return zgwResponse;
		} catch (HttpStatusCodeException hsce) {
			json = json.replace("{", "{\n").replace("\",", "\",\n").replace("\"}", "\"\n}");
			var response = hsce.getResponseBodyAsString().replace("{", "{\n").replace("\",", "\",\n").replace("\"}", "\"\n}");
			var details = "--------------PUT:\n" + url + "\n" + json + "\n--------------RESPONSE:\n" + response;			
			log.warn("PUT naar OpenZaak: " + url + " gaf foutmelding:\n" + details, hsce);
			throw new ConverterException("PUT naar OpenZaak: " + url + " gaf foutmelding:" + hsce.toString(), details, hsce);
		} catch (org.springframework.web.client.ResourceAccessException rae) {
			log.warn("PUT naar OpenZaak: " + url + " niet geslaagd", rae);
			throw new ConverterException("PUT naar OpenZaak: " + url + " niet geslaagd", rae);
		}
    }

    private String getUrlWithParameters(String url, Map<String, String> parameters) {
        var i = 0;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (i == 0) {
                url = url + "?" + key + "=" + value;
            } else {
                url = url + "&" + key + "=" + value;
            }
            i++;
        }
        return url;
    }

    public ZgwEnkelvoudigInformatieObject getZgwEnkelvoudigInformatieObjectByIdentiticatie(String identificatie) {
    	log.info("get zaakdocument #" + identificatie);
        var documentJson = get(baseUrl + endpointEnkelvoudiginformatieobject + "?identificatie=" + identificatie, null);
        Type type = new TypeToken<QueryResult<ZgwEnkelvoudigInformatieObject>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwEnkelvoudigInformatieObject> queryResult = gson.fromJson(documentJson, type);

        if (queryResult.getResults().size() == 1) {
            return queryResult.getResults().get(0);
        }
        log.info("zaakdocument #" + identificatie + " not found!");
        return null;
    }

    public ZgwRolType getRolTypeByUrl(String url) {
        var rolTypeJson = get(url, null);
        Gson gson = new Gson();
        ZgwRolType result = gson.fromJson(rolTypeJson, ZgwRolType.class);
        return result;
    }    
    
    public ZgwZaak getZaakByUrl(String url) {
        var zaakJson = get(url, null);
        Gson gson = new Gson();
        ZgwZaak result = gson.fromJson(zaakJson, ZgwZaak.class);
        return result;
    }

    public String getBas64Inhoud(String url)  {
        return httpService.downloadFile(url);
    }


    public ZgwZaak getZaak(Map<String, String> parameters) {
        ZgwZaak result = null;
        var zaakJson = get(baseUrl + endpointZaak, parameters);
        Type type = new TypeToken<QueryResult<ZgwZaak>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwZaak> queryResult = gson.fromJson(zaakJson, type);
        if (queryResult.getResults().size() == 1) {
            result = queryResult.getResults().get(0);
        }
        return result;
    }

    public ZgwZaak addZaak(ZgwZaak zgwZaak)  {
        Gson gson = new Gson();
        String json = gson.toJson(zgwZaak);
        String response = this.post(baseUrl + endpointZaak, json);
        return gson.fromJson(response, ZgwZaak.class);
    }

    public ZgwRol addZgwRol(ZgwRol zgwRol)  {
        Gson gson = new Gson();
        String json = gson.toJson(zgwRol);
        String response = this.post(baseUrl + endpointRol, json);
        return gson.fromJson(response, ZgwRol.class);
    }

    public ZgwEnkelvoudigInformatieObject addZaakDocument(ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject)   {
        Gson gson = new Gson();
        String json = gson.toJson(zgwEnkelvoudigInformatieObject);
        String response = this.post(baseUrl + endpointEnkelvoudiginformatieobject, json);
        return gson.fromJson(response, ZgwEnkelvoudigInformatieObject.class);
    }

    public ZgwZaakInformatieObject addDocumentToZaak(ZgwZaakInformatieObject zgwZaakInformatieObject)   {
        Gson gson = new Gson();
        String json = gson.toJson(zgwZaakInformatieObject);
        String response = this.post(baseUrl + endpointZaakinformatieobject, json);
        return gson.fromJson(response, ZgwZaakInformatieObject.class);
    }

    public List<ZgwZaakInformatieObject> getZgwZaakInformatieObjects(Map<String, String> parameters) {
        //Fetch EnkelvoudigInformatieObjects
        var zaakInformatieObjectJson = get(baseUrl + endpointZaakinformatieobject, parameters);

        Gson gson = new Gson();
        Type documentList = new TypeToken<ArrayList<ZgwZaakInformatieObject>>() {
        }.getType();
        return gson.fromJson(zaakInformatieObjectJson, documentList);
    }

    public ZgwEnkelvoudigInformatieObject getZaakDocument(String url) {
        var zaakInformatieObjectJson = get(url, null);
        Gson gson = new Gson();
        return gson.fromJson(zaakInformatieObjectJson, ZgwEnkelvoudigInformatieObject.class);
    }

    public List<ZgwStatusType> getStatusTypes(Map<String, String> parameters) {
        var statusTypeJson = get(baseUrl + endpointStatustype, parameters);
        Type type = new TypeToken<QueryResult<ZgwStatusType>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwStatusType> queryResult = gson.fromJson(statusTypeJson, type);
        return queryResult.getResults();
    }

    public List<ZgwStatus> getStatussen(Map<String, String> parameters) {
        var statusTypeJson = get(baseUrl + endpointStatus, parameters);
        Type type = new TypeToken<QueryResult<ZgwStatus>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwStatus> queryResult = gson.fromJson(statusTypeJson, type);
        return queryResult.getResults();
    }

    public <T> T getResource(String url, Class<T> resourceType) {
        Gson gson = new Gson();
        String response = get(url, null);
        return gson.fromJson(response, resourceType);
    }

    public ZgwStatus actualiseerZaakStatus(ZgwStatus zgwSatus)   {
        Gson gson = new Gson();
        String json = gson.toJson(zgwSatus);
        String response = this.post(baseUrl + endpointStatus, json);
        return gson.fromJson(response, ZgwStatus.class);
    }

    public List<ZgwZaakType> getZaakTypes(Map<String, String> parameters) {
        var zaakTypeJson = get(baseUrl + endpointZaaktype, parameters);
        Type type = new TypeToken<QueryResult<ZgwZaakType>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwZaakType> queryResult = gson.fromJson(zaakTypeJson, type);
        return queryResult.getResults();
    }

    public ZgwZaakType getZaakTypeByUrl(String url) {
        var zaakTypeJson = get(url, null);
        Gson gson = new Gson();
        ZgwZaakType result = gson.fromJson(zaakTypeJson, ZgwZaakType.class);
        return result;
    }
    
    
    public List<ZgwRol> getRollen(Map<String, String> parameters) {
        var zaakTypeJson = get(this.baseUrl + endpointRol, parameters);
        Type type = new TypeToken<QueryResult<ZgwRol>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwRol> queryResult = gson.fromJson(zaakTypeJson, type);
        return queryResult.getResults();
    }

    public List<ZgwRolType> getRolTypen(Map<String, String> parameters) {
        var rolTypeJson = get(baseUrl + endpointRolType, parameters);
        Type type = new TypeToken<QueryResult<ZgwRolType>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwRolType> queryResult = gson.fromJson(rolTypeJson, type);
        return queryResult.getResults();
    }

//    public ZgwRolType getRoltypeByZaakTypeUrlAndOmschrijvingGeneriek(String zaakTypeUrl, String omschrijvingGeneriek) {
//        Map<String, String> parameters = new HashMap<>();
//        parameters.put("zaaktype", zaakTypeUrl);
//        parameters.put("omschrijvingGeneriek", omschrijvingGeneriek);
//        
//		Type type = new TypeToken<QueryResult<ZgwRolType>>() {
//		}.getType();
//		Gson gson = new Gson();
//		QueryResult<ZgwRolType> queryResult = gson.fromJson(rolTypeJson, type);
//		for(ZgwRolType found : queryResult.getResults()) {
//			if(found.zaaktype.equals(zaaktype) &&  found.omschrijving.equals(omschrijving)) {
//				result = found;
//			}
//		}
//        
//        
//        return this.getRolTypen(parameters).get(0);
//    }
	public ZgwRolType getRolTypeByZaaktypeUrlAndOmschrijving(String zaaktype, String omschrijving)  {
		Map<String, String> parameters = new HashMap();

		var rolTypeJson = get(this.baseUrl + "/catalogi/api/v1/roltypen", parameters);
		ZgwRolType result = null;
			Type type = new TypeToken<QueryResult<ZgwRolType>>() {
			}.getType();
			Gson gson = new Gson();
			QueryResult<ZgwRolType> queryResult = gson.fromJson(rolTypeJson, type);
			for(ZgwRolType found : queryResult.getResults()) {
				if(found.zaaktype.equals(zaaktype) &&  found.omschrijving.equals(omschrijving)) {
					result = found;
				}
			}
		return result;
	}    
    

    public void updateZaak(String zaakUuid, ZgwZaakPut zaak) {
        Gson gson = new Gson();
        String json = gson.toJson(zaak);
        this.put(baseUrl + endpointZaak + "/" + zaakUuid, json);
    }

    public void deleteRol(String rolUuid) {
        delete(baseUrl + endpointRol + "/" + rolUuid);
    }

    public List<ZgwZaakInformatieObject> getZaakInformatieObjectenByZaak(String zaakUrl) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);
        return this.getZgwZaakInformatieObjects(parameters);
    }

    public ZgwZaak getZaakByIdentificatie(String zaakIdentificatie) {
        Map<String, String> parameters = new HashMap();
        parameters.put("identificatie", zaakIdentificatie);

        ZgwZaak zgwZaak = this.getZaak(parameters);

        if(zgwZaak == null) return null;
        
        //When Verlenging/Opschorting not set, zgw returns object with empty values, in stead of null.
        //This will cause issues when response of getzaakdetails is used for updatezaak.
        if (zgwZaak.getVerlenging().getDuur() == null || zgwZaak.getVerlenging().getReden().equals("")) {
            zgwZaak.setVerlenging(null);
        }
        if (zgwZaak.getOpschorting().getReden().equals("")) {
            zgwZaak.setOpschorting(null);
        }
        return zgwZaak;
    }

    public ZgwZaakInformatieObject getZgwZaakInformatieObject(ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject) {
        Map<String, String> parameters = new HashMap();
        parameters.put("informatieobject", zgwEnkelvoudigInformatieObject.getUrl());
        return this.getZgwZaakInformatieObjects(parameters).get(0);
    }

	public ZgwStatusType getStatusTypeByZaakTypeAndVolgnummer(String zaakTypeUrl, String volgnummer, String verwachteomschrijving)  {
		ZgwStatusType statustype =  getStatusTypeByZaakTypeAndVolgnummer(zaakTypeUrl, Integer.valueOf(volgnummer));		
		if(!statustype.omschrijving.startsWith(verwachteomschrijving)) 
		{
			throw new ConverterException("zaakstatus verschil in omschrijving met volgnummer #" + statustype.volgnummer + " verwacht: '" + statustype.omschrijving + "' gekregen: '" +  verwachteomschrijving  + "'");
		}
		return statustype;
	}    
    
    private ZgwStatusType getStatusTypeByZaakTypeAndVolgnummer(String zaakTypeUrl, int volgnummer) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaaktype", zaakTypeUrl);

        return this.getStatusTypes(parameters)
                .stream()
                .filter(zgwStatusType -> zgwStatusType.volgnummer == volgnummer)
                .findFirst()
                .orElse(null);
    }  
    
    public List<ZgwRol> getRollenByZaakUrl(String zaakUrl) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);

        return this.getRollen(parameters);
    }

    public List<ZgwRol> getRollenByBsn(String bsn) {
        Map<String, String> parameters = new HashMap();
        parameters.put("betrokkeneIdentificatie__natuurlijkPersoon__inpBsn", bsn);
        return this.getRollen(parameters);
    }    
    
    public ZgwRol getRolByZaakUrlAndRolTypeUrl(String zaakUrl, String rolTypeUrl) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);
        parameters.put("roltype", rolTypeUrl);

        return this.getRollen(parameters)
                .stream()                
                .findFirst()
                .orElse(null);
    }

    public List<ZgwStatus> getStatussenByZaakUrl(String zaakUrl) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);

        return this.getStatussen(parameters);
    }

    public ZgwZaakType getZgwZaakTypeByIdentificatie(String identificatie) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("identificatie", identificatie);
        var types = this.getZaakTypes(parameters);
		if (types.size() == 1) {
	        return types.get(0);
		}
		return null;
    }

    //TODO: filter by zaaktype
    public ZgwInformatieObjectType getZgwInformatieObjectTypeByOmschrijving(String omschrijving) {
        Map<String, String> parameters = new HashMap();
        parameters.put("status", "definitief");

        var zaakTypeJson = get(this.baseUrl + endpointInformatieobjecttype, parameters);
        Type type = new TypeToken<QueryResult<ZgwInformatieObjectType>>() {
        }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwInformatieObjectType> queryResult = gson.fromJson(zaakTypeJson, type);
        for(ZgwInformatieObjectType current: queryResult.results) {
            log.debug("gevonden ZgwInformatieObjectType met omschrijving: '" + current.omschrijving + "'");
            if(omschrijving.equals(current.omschrijving)) {
                return current;
            }
        }
        return null;
    }
}
