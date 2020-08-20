package nl.haarlem.translations.zdstozgw.controller;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.converter.ConverterFactory;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerFactory;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

@RestController
public class SoapController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConverterFactory converterFactory;
    private final ConfigService configService;
    private final RequestHandlerFactory requestHandlerFactory;

    @Autowired
    public SoapController(ConverterFactory converterFactory, ConfigService configService, RequestHandlerFactory requestHandlerFactory) {
        this.converterFactory = converterFactory;
        this.configService = configService;
        this.requestHandlerFactory = requestHandlerFactory;
    }

	@PostMapping(path = {"/{path}", "/{path}/{path2}"}, consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)    
    public ResponseEntity<?> HandleRequest(
			// we dont use path2, only used so it can be used as wildcard
			@PathVariable String path,
			@RequestHeader(name = "SOAPAction", required = true) String soapAction, 
			@RequestBody String body) {
		
		log.info("Processing request for path: /" + path + "/ with soapaction: " + soapAction);		

		var converter = this.converterFactory.getConverter(path, soapAction.replace("\"", ""));
		RequestHandler requestHandler = requestHandlerFactory.getRequestHandler(converter);     
		return requestHandler.execute(path, soapAction, body); 
    }
}