package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Configuratie;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycleService;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.xml.soap.SOAPConstants;

public class LoggingRequestHandler extends RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RequestResponseCycleService sessionService;

    public LoggingRequestHandler(Converter converter, ConfigService configService) {
        super(converter, configService);
        sessionService = SpringContext.getBean(RequestResponseCycleService.class);
    }

    @Override
    public ResponseEntity<?> execute()  {
    	log.info("Executing request with handler: " + this.getClass().getCanonicalName() + " and converter: " + this.converter.getClass().getCanonicalName());    	
        Configuratie configuratie = configService.getConfiguratie();

        // TODO: netter
        LocalDateTime start = LocalDateTime.now();
        RequestResponseCycle session = new RequestResponseCycle()
                .setTimestamp(start)
                .setClientUrl(this.getConverter().getContext().getUrl())
                .setClientSoapAction(this.getConverter().getContext().getSoapAction())
                .setClientRequestBody(this.getConverter().getContext().getRequestBody())
                .setConverterImplementation(this.getConverter().getTranslation().getImplementation())
                .setConverterTemplate(this.getConverter().getTranslation().getTemplate());
        sessionService.save(session);
        this.sessionService.setRequestResponseCycleSession(session);
        
        this.converter.load();
        try {
			var response = this.converter.execute();
        	session.setClientResponseBody(response.getBody().toString());
        	session.setClientResponseCode(response.getStatusCodeValue());
        	session.setDurationInMilliseconds(Duration.between(start, LocalDateTime.now()).toMillis());
            this.sessionService.save(session);
            
            return response;
        }
		catch(Exception ex) {
			var fo03 = getErrorZdsDocument(ex, this.getConverter());
	        var response = XmlUtils.getSOAPFaultMessageFromObject(SOAPConstants.SOAP_RECEIVER_FAULT, ex.toString(), fo03);
	        
	        // log this response
        	session.setClientResponseBody(response);
        	// TODO: use the correct response code
        	session.setClientResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        session.setDurationInMilliseconds(Duration.between(start, LocalDateTime.now()).toMillis());
	        sessionService.save(session);

	        return new ResponseEntity<>(response, ex instanceof ConverterException ? ((ConverterException) ex).getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    
//    protected String postZdsRequest() {
//        String url = this.converter.getTranslation().getLegacyservice();
//        String soapAction = this.converter.getTranslation().getSoapAction();
//        log.info("Performing ZDS request to: '" + url + "' for soapaction:" + soapAction);
//        var post = new PostMethod(this.converter.getTranslation().getLegacyservice());
//        try {
//            post.setRequestHeader("SOAPAction", soapAction);
//            post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
//            StringRequestEntity requestEntity = new org.apache.commons.httpclient.methods.StringRequestEntity(this.request, "text/xml", "utf-8");
//            post.setRequestEntity(requestEntity);
//            var httpclient = new org.apache.commons.httpclient.HttpClient();
//            int responsecode = httpclient.executeMethod(post);
//            String zdsResponseCode = "" + responsecode;
//            String zdsResponseBody = post.getResponseBodyAsString();
//
//            if (responsecode != 200) {
//                log.warn("Receive the responsecode status " + responsecode + "  from: " + url + " (dus geen status=200  van het ouwe zaaksysteem)");
//            }
//            return zdsResponseBody;
//        } catch (IOException ce) {
//            throw new RuntimeException("OpenZaakBrug kon geen geen verbinding maken met:" + url);
//        } finally {
//            // Release current connection to the connection pool once you are done
//            post.releaseConnection();
//        }
//    }
}
