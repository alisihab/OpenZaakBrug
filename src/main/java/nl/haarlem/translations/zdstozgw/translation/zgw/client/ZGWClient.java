package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.io.IOException;
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

    @Value("${zgw.endpoint.roltype}")
    private String endpointRolType;

    @Value("${zgw.endpoint.rol}")
    private String endpointRol;

    @Value("${zgw.endpoint.zaaktype}")
    private String endpointZaaktype;

    @Value("${zgw.endpoint.status}")
    private String endpointStatus;

    @Value("${zgw.endpoint.statustype}")
    private String endpointStatustype;

    @Value("${zgw.endpoint.zaakinformatieobject}")
    private String endpointZaakinformatieobject;

    @Value("${zgw.endpoint.enkelvoudiginformatieobject}")
    private String endpointEnkelvoudiginformatieobject;

    @Value("${zgw.endpoint.zaak}")
    private String endpointZaak;

    @Autowired
    HttpService httpService;

    @Autowired
    RestTemplateService restTemplateService;

    private String post(String url, String json) throws HttpStatusCodeException {
        log.debug("POST: " + url + ", json: " + json);
        HttpEntity<String> request = new HttpEntity<String>(json, restTemplateService.getHeaders());
        String zgwResponse = null;
        zgwResponse = restTemplateService.getRestTemplate().postForObject(url, request, String.class);
        log.debug("POST response: " + zgwResponse);
        return zgwResponse;
    }

    private String get(String url, Map<String, String> parameters) throws HttpStatusCodeException {
        log.debug("GET: " + url);

        if (parameters != null) {
            url = getUrlWithParameters(url, parameters);
        }

        HttpEntity entity = new HttpEntity(restTemplateService.getHeaders());

        ResponseEntity<String> response = restTemplateService.getRestTemplate().exchange(
                url, HttpMethod.GET, entity, String.class);
        log.debug("GET response: " + response.getBody());

        return response.getBody();
    }

    public void delete(String url) throws HttpStatusCodeException {
        log.debug("DELETE: " + url );
        HttpEntity entity = new HttpEntity(restTemplateService.getHeaders());

        ResponseEntity<String> response = restTemplateService.getRestTemplate().exchange(
                url, HttpMethod.DELETE, entity, String.class);
        log.debug("DELETE response: " + response.getBody());
    }

    public String put(String url, String json) throws HttpStatusCodeException {
        log.debug("PUT: " + url + ", json: " + json);
        HttpEntity<String> request = new HttpEntity<String>(json, restTemplateService.getHeaders());

        ResponseEntity<String> response = restTemplateService.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        log.debug("PUT response: " + response.getBody());
        return response.getBody();
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

    public ZgwEnkelvoudigInformatieObject getZgwEnkelvoudigInformatieObject(String identificatie){
        ZgwEnkelvoudigInformatieObject result = null;
        var documentJson = get(baseUrl+ endpointEnkelvoudiginformatieobject+"?identificatie="+identificatie,null);
        try {
            Type type = new TypeToken<QueryResult<ZgwEnkelvoudigInformatieObject>>(){}.getType();
            Gson gson = new Gson();
            QueryResult<ZgwEnkelvoudigInformatieObject> queryResult = gson.fromJson(documentJson, type);

            if (queryResult.getResults().size() == 1) {
                result = queryResult.getResults().get(0);
            }
        } catch (Exception ex) {
            log.error("ZgwEnkelvoudigInformatieObject: " + ex.getMessage());
            throw ex;
        }
        return  result;
    }

    public ZgwZaak getZaakByUrl(String url){
        ZgwZaak result = null;
        var zaakJson = get(url, null);
        try {
            Gson gson = new Gson();
            result = gson.fromJson(zaakJson, ZgwZaak.class);

        } catch (Exception ex) {
            log.error("Exception in getZaakDetails: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public String getBas64Inhoud(String url){
        String result = null;
        try {
            result  = httpService.downloadFile(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    public ZgwZaak getZaak(Map<String, String> parameters) {

        ZgwZaak result = null;
        var zaakJson = get(baseUrl + endpointZaak, parameters);
        try {
            Type type = new TypeToken<QueryResult<ZgwZaak>>(){}.getType();
            Gson gson = new Gson();
            QueryResult<ZgwZaak> queryResult = gson.fromJson(zaakJson, type);
            if (queryResult.getResults().size() == 1) {
                result = queryResult.getResults().get(0);
            }
        } catch (Exception ex) {
            log.error("Exception in getZaakDetails: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public ZgwZaak addZaak(ZgwZaak zgwZaak) throws HttpStatusCodeException {
        ZgwZaak result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(zgwZaak);
            String response = this.post(baseUrl + endpointZaak, json);
            result = gson.fromJson(response, ZgwZaak.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in addZaak: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public ZgwRol addZgwRol(ZgwRol zgwRol) {
        ZgwRol result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(zgwRol);
            String response = this.post(baseUrl + endpointRol, json);
            result = gson.fromJson(response, ZgwRol.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in addZgwRol: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public ZgwEnkelvoudigInformatieObject addZaakDocument(ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject) {
        ZgwEnkelvoudigInformatieObject result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(zgwEnkelvoudigInformatieObject);
            String response = this.post(baseUrl + endpointEnkelvoudiginformatieobject, json);
            result = gson.fromJson(response, ZgwEnkelvoudigInformatieObject.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in addDocument: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public ZgwZaakInformatieObject addDocumentToZaak(ZgwZaakInformatieObject zgwZaakInformatieObject) {
        ZgwZaakInformatieObject result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(zgwZaakInformatieObject);
            String response = this.post(baseUrl + endpointZaakinformatieobject, json);
            result = gson.fromJson(response, ZgwZaakInformatieObject.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in addDocument: " + ex.getMessage());
            throw ex;
        }

        return result;

    }

    private List<ZgwEnkelvoudigInformatieObject> getZgwEnkelvoudigInformatieObjectList(List<ZgwEnkelvoudigInformatieObject> tempResult, List<ZgwZaakInformatieObject> zaakInformatieObjects) {
        var result = tempResult;
        zaakInformatieObjects.forEach(zaakInformatieObject -> {
            result.add(getZaakDocument(zaakInformatieObject.getInformatieobject()));
        });

        tempResult = result;
        return tempResult;
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
        ZgwEnkelvoudigInformatieObject informatieObject = null;

        var zaakInformatieObjectJson = get(url, null);
        Gson gson = new Gson();
        informatieObject = gson.fromJson(zaakInformatieObjectJson, ZgwEnkelvoudigInformatieObject.class);

        return informatieObject;
    }

    public List<ZgwStatusType> getStatusTypes(Map<String, String> parameters) {
        var statusTypeJson = get(baseUrl + endpointStatustype, parameters);
        try {
            Type type = new TypeToken<QueryResult<ZgwStatusType>>(){}.getType();
            Gson gson = new Gson();
            QueryResult<ZgwStatusType> queryResult = gson.fromJson(statusTypeJson, type);
            return queryResult.getResults();
        } catch (Exception ex) {
            log.error("Exception in getStatusTypes: " + ex.getMessage());
            throw ex;
        }
    }

    public List<ZgwStatus> getStatussen(Map<String, String> parameters) {
        var statusTypeJson = get(baseUrl + endpointStatus, parameters);
        try {
            Type type = new TypeToken<QueryResult<ZgwStatus>>(){}.getType();
            Gson gson = new Gson();
            QueryResult<ZgwStatus> queryResult = gson.fromJson(statusTypeJson, type);
            return queryResult.getResults();
        } catch (Exception ex) {
            log.error("Exception in getStatussen: " + ex.getMessage());
            throw ex;
        }
    }

    public <T> T getResource(String url, Class<T> resourceType) {
        try {
            Gson gson = new Gson();
            String response = get(url,null);
            return gson.fromJson(response, resourceType);
        } catch (Exception ex) {
            log.error("Exception in getResource: " + ex.getMessage() + "for resourceType: "+ resourceType.getCanonicalName());
            throw ex;
        }
    }

    public ZgwStatus actualiseerZaakStatus(ZgwStatus zgwSatus) {
        ZgwStatus result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(zgwSatus);
            String response = this.post(baseUrl + endpointStatus, json);
            result = gson.fromJson(response, ZgwStatus.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in actualiseerZaakStatus: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public List<ZgwZaakType> getZaakTypes(Map<String, String> parameters) {
        var zaakTypeJson = get(baseUrl + endpointZaaktype, parameters);
        try {
            Type type = new TypeToken<QueryResult<ZgwZaakType>>(){}.getType();
            Gson gson = new Gson();
            QueryResult<ZgwZaakType> queryResult = gson.fromJson(zaakTypeJson, type);
            return queryResult.getResults();
        } catch (Exception ex) {
            log.error("Exception in getZaaktype: " + ex.getMessage());
            throw ex;
        }
    }

    public List<ZgwRol> getRollen(Map<String, String> parameters) {
        var zaakTypeJson = get(this.baseUrl + endpointRol, parameters);
        Type type = new TypeToken<QueryResult<ZgwRol>>() { }.getType();
        Gson gson = new Gson();
        QueryResult<ZgwRol> queryResult = gson.fromJson(zaakTypeJson, type);
        var result = new ArrayList<ZgwRol>();
        for (ZgwRol current : queryResult.results) {
            log.debug("gevonden rol met omschrijving: '" + current.roltoelichting + "'");
            result.add(current);
        }
        return result;
    }

    public List<ZgwRolType> getRolTypen(Map<String, String> parameters) {
        var rolTypeJson = get(baseUrl + endpointRolType, parameters);
        try {
            Type type = new TypeToken<QueryResult<ZgwRolType>>(){}.getType();
            Gson gson = new Gson();
            QueryResult<ZgwRolType> queryResult = gson.fromJson(rolTypeJson, type);
            return queryResult.getResults();
        } catch (Exception ex) {
            log.error("Exception in getRolTypen: " + ex.getMessage());
            throw ex;
        }
    }

    public ZgwRolType getRoltypeByZaakTypeUrlAndOmschrijvingGeneriek(String zaakTypeUrl, String omschrijvingGeneriek){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("zaaktype", zaakTypeUrl);
        parameters.put("omschrijvingGeneriek", omschrijvingGeneriek);

        return this.getRolTypen(parameters).get(0);
    }

    public void updateZaak(String zaakUuid, ZgwZaak zaak){
        Gson gson = new Gson();
        String json = gson.toJson(zaak);
        this.put(baseUrl+endpointZaak+"/"+ zaakUuid, json);
    }

    public void deleteRol(String rolUuid){
        delete(baseUrl+endpointRol+"/"+rolUuid);
    }

    public List<ZgwZaakInformatieObject> getZaakInformatieObjectenByZaak(String zaakUrl){
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);

        return this.getZgwZaakInformatieObjects(parameters);
    }

    public ZgwZaak getZaak(String zaakIdentificatie) {
        Map<String, String> parameters = new HashMap();
        parameters.put("identificatie", zaakIdentificatie);

        ZgwZaak zgwZaak = this.getZaak(parameters);

        //When Verlenging/Opschorting not set, zgw returns object with empty values, in stead of null.
        //This will cause issues when response of getzaakdetails is used for updatezaak.
        if(zgwZaak.getZgwVerlenging().getDuur() == null || zgwZaak.getZgwVerlenging().getReden().equals("")){
            zgwZaak.setZgwVerlenging(null);
        }
        if(zgwZaak.getZgwOpschorting().getReden().equals("")){
            zgwZaak.setZgwOpschorting(null);
        }
        return zgwZaak;
    }

    public ZgwZaakInformatieObject getZgwZaakInformatieObject(ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject) {
        Map<String, String> parameters = new HashMap();
        parameters.put("informatieobject", zgwEnkelvoudigInformatieObject.getUrl());
        return this.getZgwZaakInformatieObjects(parameters).get(0);
    }

    public ZgwStatusType getStatusTypeByZaakTypeAndVolgnummer(String zaakTypeUrl, int volgnummer){
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

    public ZgwRol getRolByZaakUrlAndOmschrijvingGeneriek(String zaakUrl, String omschrijvingGeneriek) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);
        parameters.put("omschrijvingGeneriek", omschrijvingGeneriek);

        return this.getRollen(parameters)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<ZgwStatus> getStatussenByZaakUrl(String zaakUrl){
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);

        return this.getStatussen(parameters);
    }

    public ZgwZaakType getZgwZaakTypeByIdentificatie(String identificatie){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("identificatie", identificatie);

        return this.getZaakTypes(parameters).get(0);
    }
}
