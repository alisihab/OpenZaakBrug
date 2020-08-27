package nl.haarlem.translations.zdstozgw.translation.zds.client;

import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

//import nl.haarlem.translations.zdstozgw.translation.zgw.services.HttpService;

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
			throw new ConverterException("Requesting url:" + zdsUrl + " with soapaction: " + zdsSoapAction , ce);
		} catch (java.lang.IllegalArgumentException iae) {
			throw new ConverterException("Requesting url:" + zdsUrl + " with soapaction: " + zdsSoapAction , iae);						
		} finally {
			// Release current connection to the connection pool once you are done
			post.releaseConnection();
		}		
	}		
}