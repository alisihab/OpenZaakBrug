package nl.haarlem.translations.zdstozgw.requesthandler;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;

@Data
public abstract class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    protected Converter converter;
    protected ConfigService configService;

    public RequestHandler(Converter converter, ConfigService configService) {
        this.converter = converter;
        this.configService = configService;
    }


	protected ZdsFo03 getErrorZdsDocument(Exception ex, Converter convertor) {
		log.warn("request for path: /" + converter.getContext().getUrl()+ "/ with soapaction: " + converter.getContext().getSoapAction(), ex);
		
		// get the stacktrace
		var swriter = new java.io.StringWriter();
		var pwriter = new java.io.PrintWriter(swriter);
		ex.printStackTrace(pwriter);
		var stacktrace = swriter.toString();			
		 
        var fo03 = converter.getZdsDocument() != null ? new ZdsFo03(converter.getZdsDocument().stuurgegevens) : new ZdsFo03();
        fo03.body = new ZdsFo03.Body();
        https://www.gemmaonline.nl/images/gemmaonline/4/4f/Stuf0301_-_ONV0347_%28zonder_renvooi%29.pdf
        fo03.body.code = "StUF058";
        fo03.body.plek = "server";
        fo03.body.omschrijving = ex.toString();
        fo03.body.entiteittype = "";
        if (ex instanceof ConverterException) {
        	var ce = (ConverterException) ex;
        	fo03.body.details = ce.details;
        }	        
        else {
        	fo03.body.details = stacktrace;
        }
        fo03.body.detailsXML = converter.getContext().getRequestBody();
        return fo03;
	}    
    
    public abstract ResponseEntity<?> execute();
}