package nl.haarlem.translations.zdstozgw.converter;
import java.lang.invoke.MethodHandles;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.model.StufRequest;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public abstract class Converter {

	@SuppressWarnings("serial")
	public class ConverterException extends RuntimeException {
		protected Converter converter;
		protected String stacktrace;
		protected String requestbody;
		
		public ConverterException(Converter converter, String omschrijving, String requestBody, Throwable err) {
			super(omschrijving, err);			
			this.converter = converter;
			this.requestbody = requestBody;
					
			// get the stacktrace and store it
		    var swriter = new java.io.StringWriter();
		    var pwriter = new java.io.PrintWriter(swriter);		    
		    this.printStackTrace(pwriter);
			this.stacktrace = swriter.toString();
		}

		public String getStacktrace() {
			return stacktrace;
		}

		public String getFaultString() {
			return getMessage();
		}

		public String getOmschrijving() {
			return this.toString();
		}

		public String getDetails() {
			return stacktrace;
		}

		public String getDetailsXml() {
			return requestbody;
		}

		public HttpStatus getHttpStatus() {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}		
	}
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	protected String template;
	protected String zdsUrl;
    
	public Converter(String template, String legacyService) {
		this.template = template;
        this.zdsUrl = legacyService;        
	}
    
	public abstract String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestBody);

	public String getImplementation() {
		return this.getClass().getCanonicalName();
	}

	public String getTemplate() {
		return this.template;
	}

	public String passThrough(String zdsSoapAction, RequestResponseCycle session, ApplicationParameterRepository repository, String zdsRequest) throws Exception 
	{ 
        // what are we going to do?
		session.setZdsUrl(zdsUrl);
        session.setZdsRequest(zdsRequest);
        session.setZdsSoapAction(zdsSoapAction);

        log.info("Performing ZDS request to: '" + zdsUrl + "' for soapaction:" + zdsSoapAction);
        
		var post = new PostMethod(zdsUrl);        
        try {
	        post.setRequestHeader("SOAPAction", zdsSoapAction);
	        post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");	        
	        StringRequestEntity requestEntity =  new org.apache.commons.httpclient.methods.StringRequestEntity(zdsRequest, "text/xml", "utf-8");
	        post.setRequestEntity(requestEntity);
	        
	        var httpclient = new org.apache.commons.httpclient.HttpClient();
	        int responsecode = httpclient.executeMethod(post);
	        String zdsResponeCode = "" + responsecode;
	        String zdsResponeBody = post.getResponseBodyAsString(); 

	        if(responsecode != 200) {
	        	log.warn("Receive the responsecode status "  + responsecode + "  from: " + zdsUrl + " (dus geen status=200  van het ouwe zaaksysteem)");	        	
	        }
	        session.setZdsResponeCode(zdsResponeCode);
	        session.setZdsResponeBody(zdsResponeBody);
	        
	        return zdsResponeBody;
        }
	    catch(java.net.ConnectException ce) {
	    	throw new ConverterException(this, "OpenZaakBrug kon geen geen verbinding maken met:" + zdsUrl, zdsRequest, ce);
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
	}	
}