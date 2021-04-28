package nl.haarlem.translations.zdstozgw.controller;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.ConverterFactory;
import nl.haarlem.translations.zdstozgw.debug.Debugger;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerFactory;

@RestController
public class SoapController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final Debugger debug = Debugger.getDebugger(MethodHandles.lookup().lookupClass());

	private final ConverterFactory converterFactory;
	private final ConfigService configService;
	private final RequestHandlerFactory requestHandlerFactory;

	@Autowired
	public SoapController(ConverterFactory converterFactory, ConfigService configService,
			RequestHandlerFactory requestHandlerFactory) {
		this.converterFactory = converterFactory;
		this.configService = configService;
		this.requestHandlerFactory = requestHandlerFactory;
	}


    /**
     * Does not handle any requests, returns a list of available endpoints
     *
     * @return List of available endpoints
     */
	@GetMapping(path = { "/" }, produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<?> HandleRequest() {
		var session = new RequestResponseCycle();
		
		this.requestHandlerFactory.getRequestHandler(this.converterFactory.getConverter(session));
		return null;
	}

    /**
     * Receives the SOAP requests. Based on the configuration and path variables, the correct translation implementation is used.
     *
     * @param modus
     * @param version
     * @param protocol
     * @param endpoint
     * @param soapAction
     * @param body
     * @return ZDS response
     */
	@PostMapping(path = { "/{modus}/{version}/{protocol}/{endpoint}" },
                    consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<?> HandleRequest(
			@PathVariable String modus, @PathVariable String version, @PathVariable String protocol,
			@PathVariable String endpoint, @RequestHeader(name = "SOAPAction", required = true) String soapAction,
			@RequestBody String body, String referentienummer) {

		var path = modus + "/" + version + "/" + protocol + "/" + endpoint;
		if (referentienummer == null) {
			referentienummer = "ozb-" + java.util.UUID.randomUUID().toString();
		}
		var session = new RequestResponseCycle(modus, version, protocol, endpoint, path, soapAction.replace("\"", ""), body, referentienummer);
		log.info("Processing request for path: /" + path + "/ with soapaction: " + soapAction
				+ " with referentienummer:" + referentienummer);

		RequestContextHolder.getRequestAttributes().setAttribute("referentienummer", referentienummer,
				RequestAttributes.SCOPE_REQUEST);

		var converter = this.converterFactory.getConverter(session);		
		var handler = this.requestHandlerFactory.getRequestHandler(converter);		
		handler.save(session);

		String reportName = session.getModus();
		if (reportName == null || reportName.length() < 1) {
			reportName = "Execute";
		} else {
			reportName = reportName.substring(0, 1).toUpperCase() + reportName.substring(1);
			if (soapAction != null) {
				int i = soapAction.lastIndexOf('/');
				if (i != -1 ) {
					reportName = reportName + " " + soapAction.substring(i + 1, soapAction.length() - 1);
				}
			}
		}
		ResponseEntity<?> response;
		try {
			debug.startpoint(reportName, body);
			debug.inputpoint("modus", modus);
			debug.inputpoint("version", version);
			debug.inputpoint("protocol", protocol);
			debug.inputpoint("endpoint", endpoint);
			debug.inputpoint("soapAction", soapAction);
			debug.infopoint("referentienummer", referentienummer);
			debug.infopoint("converter", converter.getClass().getCanonicalName());
			debug.infopoint("handler", handler.getClass().getCanonicalName());
			debug.infopoint("path", path);
			response = handler.execute();
			debug.outputpoint("statusCode", response.getStatusCodeValue());
			debug.outputpoint("kenmerk", session.getKenmerk());

			var message = "Soapaction: " + soapAction + " took " + session.getDurationInMilliseconds() + " milliseconds";			
			debug.infopoint("Total duration", message);			
			debug.endpoint(reportName, response.getBody().toString());
		} catch(Throwable t) {			
			debug.abortpoint(reportName, t.toString());
			throw t;
		} finally {
			debug.close();
		}
		session.setResponse(response);
		handler.save(session);
		return response;
	}
}
