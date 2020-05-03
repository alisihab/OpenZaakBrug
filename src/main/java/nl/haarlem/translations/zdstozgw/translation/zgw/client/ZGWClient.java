package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nl.haarlem.translations.zdstozgw.translation.zgw.model.QueryResult;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.RolNPS;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatus;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatusType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaak;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;

@Service
public class ZGWClient {

	@SuppressWarnings("serial")
	public class ZGWClientException extends Exception {
		protected String details;

		public ZGWClientException(String message, String details, Throwable err) {
			super(message, err);
			this.details = details;
		}

		public String getDetails() {
			return this.details;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Value("${openzaak.baseUrl}")
	private String baseUrl;

	@Autowired
	RestTemplateService restTemplateService;

	private String post(String url, String json) throws ZGWClientException {
		log.debug("POST: " + url + ", json: " + json);
		HttpEntity<String> request = new HttpEntity<String>(json, this.restTemplateService.getHeaders());
		String zgwResponse = null;
		try {
			zgwResponse = this.restTemplateService.getRestTemplate().postForObject(url, request, String.class);
		} catch (HttpStatusCodeException hsce) {

			/*
			 * public class ZgwFout { public class ZgwFoutInvalidParams { public String
			 * name; public String code; public String reason;
			 * 
			 * } public String type; public String code; public String title; public String
			 * status; public String detail; public String instance; public
			 * ZgwFoutInvalidParams[] invalidParams; } ZgwFout fout =
			 * g.fromJson(hsce.getResponseBodyAsString(), ZgwFout.class);
			 */
			// more robust than fromJson
			log.warn("fout met verzendende-json:" + json + "\n" + hsce.getResponseBodyAsString().replace("{", "{\n")
					.replace("\",", "\",\n").replace("\"}", "\"\n}"));

			throw new ZGWClientException("POST naar OpenZaak: " + url + " gaf foutmelding:" + hsce.getMessage(), json,
					hsce);
		} catch (org.springframework.web.client.ResourceAccessException rae) {
			throw new ZGWClientException("POST naar OpenZaak: " + url + " niet geslaagd", json, rae);
		}
		log.debug("POST response: " + zgwResponse);
		return zgwResponse;
	}

	private String get(String url, Map<String, String> parameters) throws ZGWClientException {
		log.debug("GET: " + url);

		if (parameters != null) {
			url = getUrlWithParameters(url, parameters);
		}

		HttpEntity entity = new HttpEntity(this.restTemplateService.getHeaders());
		ResponseEntity<String> response = null;

		try {
			response = this.restTemplateService.getRestTemplate().exchange(url, HttpMethod.GET, entity, String.class);
		} catch (HttpStatusCodeException hsce) {
			throw new ZGWClientException("GET naar OpenZaak: " + url + " gaf foutmelding" + hsce.getStatusText(), url,
					hsce);
		} catch (org.springframework.web.client.ResourceAccessException rae) {
			throw new ZGWClientException("GET naar OpenZaak: " + url + " niet geslaagd", url, rae);
		}

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

	public ZgwEnkelvoudigInformatieObject getZgwEnkelvoudigInformatieObject(String identificatie)
			throws ZGWClientException {
		ZgwEnkelvoudigInformatieObject result = null;
		var documentJson = get(
				this.baseUrl + "/documenten/api/v1/enkelvoudiginformatieobjecten?identificatie=" + identificatie, null);
		try {
			Type type = new TypeToken<QueryResult<ZgwEnkelvoudigInformatieObject>>() {
			}.getType();
			Gson gson = new Gson();
			QueryResult<ZgwEnkelvoudigInformatieObject> queryResult = gson.fromJson(documentJson, type);

			if (queryResult.getResults().size() == 1) {
				result = queryResult.getResults().get(0);
			}
		} catch (Exception ex) {
			log.error("ZgwEnkelvoudigInformatieObject: " + ex.getMessage());
			throw ex;
		}
		return result;
	}

	public ZgwZaak getZaakDetails(Map<String, String> parameters) throws ZGWClientException {

		ZgwZaak result = null;
		var zaakJson = get(this.baseUrl + "/zaken/api/v1/zaken", parameters);
		try {
			Type type = new TypeToken<QueryResult<ZgwZaak>>() {
			}.getType();
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

	public ZgwZaak addZaak(ZgwZaak zgwZaak) throws ZGWClientException {
		Gson gson = new Gson();
		String json = gson.toJson(zgwZaak);
		String response = this.post(this.baseUrl + "/zaken/api/v1/zaken", json);
		return gson.fromJson(response, ZgwZaak.class);
	}

	public RolNPS addRolNPS(String roltype, RolNPS rolNPS) throws ZGWClientException {
		RolNPS result = null;
		try {
			Gson gson = new Gson();
			String json = gson.toJson(rolNPS);
			String response = this.post(this.baseUrl + "/zaken/api/v1/rollen", json);
			result = gson.fromJson(response, RolNPS.class);
		} catch (HttpStatusCodeException ex) {
			log.error("Exception in addRolNPS: " + ex.getMessage());
			throw ex;
		}

		return result;
	}

	public ZgwEnkelvoudigInformatieObject addDocument(ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject)
			throws ZGWClientException {
		ZgwEnkelvoudigInformatieObject result = null;
		try {
			Gson gson = new Gson();
			String json = gson.toJson(zgwEnkelvoudigInformatieObject);
			String response = this.post(this.baseUrl + "/documenten/api/v1/enkelvoudiginformatieobjecten", json);
			result = gson.fromJson(response, ZgwEnkelvoudigInformatieObject.class);
		} catch (HttpStatusCodeException ex) {
			log.error("Exception in addDocument: " + ex.getMessage());
			throw ex;
		}

		return result;
	}

	public ZgwZaakInformatieObject addDocumentToZaak(ZgwZaakInformatieObject zgwZaakInformatieObject)
			throws ZGWClientException {
		ZgwZaakInformatieObject result = null;
		try {
			Gson gson = new Gson();
			String json = gson.toJson(zgwZaakInformatieObject);
			String response = this.post(this.baseUrl + "/zaken/api/v1/zaakinformatieobjecten", json);
			result = gson.fromJson(response, ZgwZaakInformatieObject.class);
		} catch (HttpStatusCodeException ex) {
			log.error("Exception in addDocument: " + ex.getMessage());
			throw ex;
		}

		return result;

	}

	public List<ZgwEnkelvoudigInformatieObject> getLijstZaakDocumenten(Map<String, String> parameters)
			throws ZGWClientException {
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

	private List<ZgwEnkelvoudigInformatieObject> getZgwEnkelvoudigInformatieObjectList(
			List<ZgwEnkelvoudigInformatieObject> tempResult, List<ZgwZaakInformatieObject> zaakInformatieObjects)
			throws ZGWClientException {
		var result = tempResult;
		for (ZgwZaakInformatieObject zaakInformatieObject : zaakInformatieObjects) {
			result.add(getZaakDocument(zaakInformatieObject.getInformatieobject()));
		}
		tempResult = result;
		return tempResult;
	}

	private List<ZgwZaakInformatieObject> getZgwZaakInformatieObjects(Map<String, String> parameters)
			throws ZGWClientException {
		// Fetch EnkelvoudigInformatieObjects
		var zaakInformatieObjectJson = get(this.baseUrl + "/zaken/api/v1/zaakinformatieobjecten", parameters);

		Gson gson = new Gson();
		Type documentList = new TypeToken<ArrayList<ZgwZaakInformatieObject>>() {
		}.getType();
		return gson.fromJson(zaakInformatieObjectJson, documentList);
	}

	private ZgwEnkelvoudigInformatieObject getZaakDocument(String url) throws ZGWClientException {
		ZgwEnkelvoudigInformatieObject informatieObject = null;

		var zaakInformatieObjectJson = get(url, null);
		Gson gson = new Gson();
		informatieObject = gson.fromJson(zaakInformatieObjectJson, ZgwEnkelvoudigInformatieObject.class);

		return informatieObject;
	}

	public List<ZgwStatusType> getStatusTypes(Map<String, String> parameters) throws ZGWClientException {
		var statusTypeJson = get(this.baseUrl + "/catalogi/api/v1/statustypen", parameters);
		try {
			Type type = new TypeToken<QueryResult<ZgwStatusType>>() {
			}.getType();
			Gson gson = new Gson();
			QueryResult<ZgwStatusType> queryResult = gson.fromJson(statusTypeJson, type);
			return queryResult.getResults();
		} catch (Exception ex) {
			log.error("Exception in getStatusTypes: " + ex.getMessage());
			throw ex;
		}
	}

	public ZgwStatus actualiseerZaakStatus(ZgwStatus zgwSatus) throws ZGWClientException {
		ZgwStatus result = null;
		try {
			Gson gson = new Gson();
			String json = gson.toJson(zgwSatus);
			String response = this.post(this.baseUrl + "/zaken/api/v1/statussen", json);
			result = gson.fromJson(response, ZgwStatus.class);
		} catch (HttpStatusCodeException ex) {
			log.error("Exception in actualiseerZaakStatus: " + ex.getMessage());
			throw ex;
		}

		return result;
	}
}
