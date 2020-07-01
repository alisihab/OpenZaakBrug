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
import java.util.List;
import java.util.Map;

@Service
public class ZGWClient {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${openzaak.baseUrl}")
    private String baseUrl;

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
        var documentJson = get(baseUrl+"/documenten/api/v1/enkelvoudiginformatieobjecten?identificatie="+identificatie,null);
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
        var zaakJson = get(baseUrl + "/zaken/api/v1/zaken", parameters);
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
            String response = this.post(baseUrl + "/zaken/api/v1/zaken", json);
            result = gson.fromJson(response, ZgwZaak.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in addZaak: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public RolNPS addRolNPS(RolNPS rolNPS) {
        RolNPS result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(rolNPS);
            String response = this.post(baseUrl + "/zaken/api/v1/rollen", json);
            result = gson.fromJson(response, RolNPS.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in addRolNPS: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public ZgwEnkelvoudigInformatieObject addDocument(ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject) {
        ZgwEnkelvoudigInformatieObject result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(zgwEnkelvoudigInformatieObject);
            String response = this.post(baseUrl + "/documenten/api/v1/enkelvoudiginformatieobjecten", json);
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
            String response = this.post(baseUrl + "/zaken/api/v1/zaakinformatieobjecten", json);
            result = gson.fromJson(response, ZgwZaakInformatieObject.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in addDocument: " + ex.getMessage());
            throw ex;
        }

        return result;

    }

    public List<ZgwEnkelvoudigInformatieObject> getLijstZaakDocumenten(Map<String, String> parameters) {
        var result = new ArrayList();

        try {
            var zaakInformatieObjects = getZgwZaakInformatieObjects(parameters);
            result = (ArrayList) getZgwEnkelvoudigInformatieObjectList(result, zaakInformatieObjects);

        } catch (Exception ex) {
            log.error("Exception in getLijstZaakdocumenten: " + ex.getMessage());
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
        var zaakInformatieObjectJson = get(baseUrl + "/zaken/api/v1/zaakinformatieobjecten", parameters);

        Gson gson = new Gson();
        Type documentList = new TypeToken<ArrayList<ZgwZaakInformatieObject>>() {
        }.getType();
        return gson.fromJson(zaakInformatieObjectJson, documentList);
    }

    private ZgwEnkelvoudigInformatieObject getZaakDocument(String url) {
        ZgwEnkelvoudigInformatieObject informatieObject = null;

        var zaakInformatieObjectJson = get(url, null);
        Gson gson = new Gson();
        informatieObject = gson.fromJson(zaakInformatieObjectJson, ZgwEnkelvoudigInformatieObject.class);

        return informatieObject;
    }

    public List<ZgwStatusType> getStatusTypes(Map<String, String> parameters) {
        var statusTypeJson = get(baseUrl + "/catalogi/api/v1/statustypen", parameters);
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
        var statusTypeJson = get(baseUrl + "/zaken/api/v1/statussen", parameters);
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

    public ZgwStatus actualiseerZaakStatus(ZgwStatus zgwSatus) {
        ZgwStatus result = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(zgwSatus);
            String response = this.post(baseUrl + "/zaken/api/v1/statussen", json);
            result = gson.fromJson(response, ZgwStatus.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Exception in actualiseerZaakStatus: " + ex.getMessage());
            throw ex;
        }

        return result;
    }

    public List<ZgwZaakType> getZaakType(Map<String, String> parameters) {
        var zaakTypeJson = get(baseUrl + "/catalogi/api/v1/zaaktypen", parameters);
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
        var zaakTypeJson = get(this.baseUrl + "/zaken/api/v1/rollen", parameters);
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

}
