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



//        @PostMapping(value = "/BeantwoordVraag", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
//    public String beantwoordVraag(@RequestHeader(name = "SOAPAction", required = true) String soapAction, @RequestBody String body) {
//
//        soapAction = soapAction.replace("\"", "");
//        Converter converter = null;
//        if (soapAction.contains("geefZaakdocumentLezen")) {
//            EdcLv01 edcLv01 = (EdcLv01) XmlUtils.getStUFObject(body, EdcLv01.class);
//            converter = this.convertorFactory.getConvertor(soapAction, edcLv01.stuurgegevens.zender.applicatie);
//            response = converter.Convert(zaakService, edcLv01);
//        }
//
//
//        var stufRequest = new StufRequest(XmlUtils.convertStringToDocument(body));
//
//        if (stufRequest.isgeefZaakDetails()) {
//            try {
//                response = XmlUtils.xmlToString(zaakService.getZaakDetails(stufRequest.getZakLv01ZaakDetails()));
//            } catch (Exception ex) {
//                handleFetchZaakException(ex);
//            }
//        }
//
//        if (stufRequest.isgeefLijstZaakdocumenten()) {
//            try {
//                response = XmlUtils.xmlToString(zaakService.getLijstZaakdocumenten(stufRequest.getZakLv01LijstZaakdocumenten()));
//            } catch (Exception ex) {
//                handleFetchZaakException(ex);
//            }
//        }
//
//        return response;
//    }
//
//    @PostMapping(value = "/OntvangAsynchroon", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
//    public String ontvangAsynchroon(@RequestHeader(name = "SOAPAction", required = true) String soapAction, @RequestBody String body) {
//        soapAction = soapAction.replace("\"", "");
//        Converter converter = null;
//        if (soapAction.contains("creeerZaak")) {
//            ZakLk01_v2 zakLk01_v2r = (ZakLk01_v2) XmlUtils.getStUFObject(body, ZakLk01_v2.class);
//            converter = this.convertorFactory.getConvertor(soapAction, zakLk01_v2r.stuurgegevens.zender.applicatie);
//            response = converter.Convert(zaakService, zakLk01_v2r);
//        }
//        if (soapAction.contains("voegZaakdocumentToe")) {
//            EdcLk01 edcLk01 = (EdcLk01) XmlUtils.getStUFObject(body, EdcLk01.class);
//            converter = this.convertorFactory.getConvertor(soapAction, edcLk01.stuurgegevens.zender.applicatie);
//            response = converter.Convert(zaakService, edcLk01);
//        }
//        if(soapAction.contains("actualiseerZaakstatus")){
//            ZakLk01_v2 zakLk01 = (ZakLk01_v2) XmlUtils.getStUFObject(body, ZakLk01_v2.class);
//            converter = this.convertorFactory.getConvertor(soapAction, zakLk01.stuurgegevens.zender.applicatie);
//            response = converter.Convert(zaakService, zakLk01);
//        }
//
//        return response;
//    }



//    private void handleFetchZaakException(Exception e) {
//        var f03 = new F03();
//        f03.setFaultString("Object was not found");
//        f03.setCode("StUF064");
//        f03.setOmschrijving("Object niet gevonden");
//        f03.setDetails(e.getMessage());
//        response = f03.getSoapMessageAsString();
//    }
}
