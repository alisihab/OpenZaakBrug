package nl.haarlem.translations.zdstozgw.controller;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.ConverterFactory;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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

	@GetMapping(path = {"/"}, produces = MediaType.TEXT_HTML_VALUE)    
    public ResponseEntity<?> HandleRequest() {
		// will always throw an exception, the exception contains valid endpoints
		var context = new RequestHandlerContext("/", "", "");
		requestHandlerFactory.getRequestHandler(this.converterFactory.getConverter(context));
		return null;
    }    
    
	@PostMapping(path = {"/{modus}/{version}/{protocol}/{endpoint}"}, consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)    
    public ResponseEntity<?> HandleRequest(
			// we dont use path2, only used so it can be used as wildcard
			@PathVariable String modus,
			@PathVariable String version,
			@PathVariable String protocol,
			@PathVariable String endpoint,
			@RequestHeader(name = "SOAPAction", required = true) String soapAction, 
			@RequestBody String body) {

		var path = modus + "/" +  version  + "/" +  protocol  + "/" + endpoint;
		var context = new RequestHandlerContext(path, soapAction.replace("\"", ""), body);
		log.info("Processing request for path: /" + path + "/ with soapaction: " + soapAction + " with referentienummer:" + context.getReferentienummer());
		
		//TODO: lelijk en het is geen php, moet anders naar nl.haarlem.translations.zdstozgw.config.SpringContext.LoggingRequestInterceptor
		RequestContextHolder.getRequestAttributes().setAttribute("referentienummer", context.getReferentienummer(), RequestAttributes.SCOPE_REQUEST);
		
		var converter = this.converterFactory.getConverter(context);		
        var handler = requestHandlerFactory.getRequestHandler(converter);		
		return handler.execute(); 
    }
}