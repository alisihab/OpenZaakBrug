package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

import java.lang.invoke.MethodHandles;

import javax.xml.soap.SOAPConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BasicRequestHandler extends RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    @Autowired
    public BasicRequestHandler(Converter converter, ConfigService configService) {
        super(converter, configService);
    }

    @Override
    public ResponseEntity<?> execute(String path, String soapAction, String request)  {
		try {
			var response = this.converter.convert(soapAction, request);
			return new ResponseEntity<>(response, HttpStatus.OK);	        
		}
		catch(Exception ex) {
			log.warn("request for path: /" + path + "/ with soapaction: " + soapAction, ex);
			
			// get the stacktrace
			var swriter = new java.io.StringWriter();
			var pwriter = new java.io.PrintWriter(swriter);
			ex.printStackTrace(pwriter);
			var stacktrace = swriter.toString();			
			 
	        var fo03 = new ZdsFo03();
	        fo03.body = new ZdsFo03.Body();
	        https://www.gemmaonline.nl/images/gemmaonline/4/4f/Stuf0301_-_ONV0347_%28zonder_renvooi%29.pdf
	        fo03.body.code = "StUF058";
	        fo03.body.plek = "server";
	        fo03.body.omschrijving = ex.toString();
	        fo03.body.entiteittype = "";
	        fo03.body.details = stacktrace;
	        fo03.body.detailsXML = request;                    
	        
	        var response = XmlUtils.getSOAPFaultMessageFromObject(SOAPConstants.SOAP_RECEIVER_FAULT, ex.toString(), fo03);
	        
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
}