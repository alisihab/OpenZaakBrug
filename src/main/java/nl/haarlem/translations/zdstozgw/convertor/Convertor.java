package nl.haarlem.translations.zdstozgw.convertor;
import java.lang.invoke.MethodHandles;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.model.StufRequest;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public abstract class Convertor {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	protected String template;	
    public Convertor(String template) {
        this.template = template;
    }
    
	public abstract String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestBody);

	public String getImplementation() {
		return this.getClass().getCanonicalName();
	}

	public String getTemplate() {
		return this.template;
	}

	public String passThrough(String zdsUrl, String zdsSoapAction, RequestResponseCycle session, ApplicationParameterRepository repository, String zdsRequest) throws Exception 
	{ 
        // what are we going to do?
		session.setZdsSoapAction(zdsUrl);
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
	        
	        session.setZdsResponeCode(zdsResponeCode);
	        session.setZdsResponeBody(zdsResponeBody);
	        
	        return zdsResponeBody;
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
	}	
}
