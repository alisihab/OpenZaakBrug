package nl.haarlem.translations.zdstozgw.translation.zds.client;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
//import nl.haarlem.translations.zdstozgw.translation.zgw.services.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nl.haarlem.translations.zdstozgw.translation.zgw.model.QueryResult;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwBetrokkeneIdentificatie;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwInformatieObjectType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwRol;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwRolType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatus;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatusType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakType;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

@Service
public class ZDSClient {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public ResponseEntity<?> post(String zdsUrl, String zdsSoapAction, String zdsRequest) {
		// what are we going to do?
		//session.addZdsRequest(zdsUrl, zdsSoapAction, zdsRequest);
		log.info("Performing ZDS request to: '" + zdsUrl + "' for soapaction:" + zdsSoapAction);
		var post = new PostMethod(zdsUrl);
		try { 			
			post.setRequestHeader("SOAPAction", zdsSoapAction);
			post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
			StringRequestEntity requestEntity = new org.apache.commons.httpclient.methods.StringRequestEntity(zdsRequest, "text/xml", "utf-8");
			post.setRequestEntity(requestEntity);
			var httpclient = new org.apache.commons.httpclient.HttpClient();
			int responsecode = httpclient.executeMethod(post);
			String zdsResponseBody = post.getResponseBodyAsString();
			
	        return new ResponseEntity<>(zdsResponseBody, HttpStatus.valueOf(responsecode));	
		} catch (IOException ce) {
			throw new RuntimeException(ce);
		} finally {
			// Release current connection to the connection pool once you are done
			post.releaseConnection();
		}		
	}		
}