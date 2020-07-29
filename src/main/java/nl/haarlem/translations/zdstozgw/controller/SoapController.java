package nl.haarlem.translations.zdstozgw.controller;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.ConverterFactory;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

@RestController
public class SoapController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConverterFactory converterFactory;
    private final ConfigService configService;
    private final RequestHandlerFactory requestHandlerFactory;

    private String response = "NOT IMPLEMENTED";

    @Autowired
    public SoapController(ConverterFactory converterFactory, ConfigService configService, RequestHandlerFactory requestHandlerFactory){
        this.converterFactory = converterFactory;
        this.configService = configService;
        this.requestHandlerFactory = requestHandlerFactory;
    }

    @PostMapping(value = "/{requestUrl}", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String HandleRequest(@PathVariable("requestUrl") String requestUrl,
                                           @RequestHeader(name = "SOAPAction", required = true) String soapAction,
                                           @RequestBody String body) {

        RequestHandler requestHandler = requestHandlerFactory.getRequestHandler(this.converterFactory.getConverter(soapAction.replace("\"", ""), body));
        return requestHandler.execute(body,requestUrl,soapAction);
    }
}
