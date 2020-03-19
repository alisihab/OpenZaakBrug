package nl.haarlem.translations.zdstozgw.controller;

import nl.haarlem.translations.zdstozgw.convertor.ConvertorFactory;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaak;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

@RestController
public class SoapController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private ZaakService zaakService;
	@Autowired	
	protected ApplicationParameterRepository repository;

    private String response = "NOT IMPLEMENTED";
   
    @PostMapping(value = "/VrijBerichtService", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String vrijBerichtService(@RequestHeader(name = "SOAPAction", required = true) String soapAction, @RequestBody String body) {
        var convertor = ConvertorFactory.getConvertor(soapAction.replace("\"", ""), body);        
        if (convertor != null) {
        	this.response = convertor.Convert(zaakService, repository, new StufRequest(XmlUtils.convertStringToDocument(body)));        	
        }
        return this.response;        
    }    
    
    @PostMapping(value = "/BeantwoordVraag", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String beantwoordVraag(@RequestBody String body) {

        var stufRequest = new StufRequest(XmlUtils.convertStringToDocument(body));

        if (stufRequest.isgeefZaakDetails()) {
            try {
                response = XmlUtils.xmlToString(zaakService.getZaakDetails(stufRequest.getZakLv01ZaakDetails()));
            } catch (Exception ex) {
                handleFetchZaakException(ex);
            }
        }

        if (stufRequest.isgeefLijstZaakdocumenten()) {
            try {
                response = XmlUtils.xmlToString(zaakService.getLijstZaakdocumenten(stufRequest.getZakLv01LijstZaakdocumenten()));
            } catch (Exception ex) {
                handleFetchZaakException(ex);
            }
        }

        return response;
    }

    private ZakLk01_v2 getZakLka01(String body) {
        ZakLk01_v2 zakLk01 = null;
        try {
            zakLk01 = (ZakLk01_v2) JAXBContext.newInstance(ZakLk01_v2.class)
                    .createUnmarshaller()
                    .unmarshal(MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(body.getBytes())).getSOAPBody().extractContentAsDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zakLk01;
    }

    private EdcLk01 getZakLEdcLk01(String body) {
        EdcLk01 edcLk01 = null;
        try {
            edcLk01 = (EdcLk01) JAXBContext.newInstance(EdcLk01.class)
                    .createUnmarshaller()
                    .unmarshal(MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(body.getBytes())).getSOAPBody().extractContentAsDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return edcLk01;
    }

    @PostMapping(value = "/OntvangAsynchroon", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String ontvangAsynchroon(@RequestHeader(name = "SOAPAction", required = true) String soapAction, @RequestBody String body) {
        soapAction = soapAction.replace("\"", "");
        Convertor convertor = null;
        if (soapAction.contains("creeerZaak")) {
            ZakLk01_v2 zakLk01_v2r = getZakLka01(body);
            convertor = ConvertorFactory.getConvertor(soapAction, zakLk01_v2r.stuurgegevens.zender.applicatie);
            response = convertor.Convert(zaakService, repository, zakLk01_v2r);
        }
        if (soapAction.contains("voegZaakdocumentToe")) {
            EdcLk01 edcLk01 = getZakLEdcLk01(body);
            convertor = ConvertorFactory.getConvertor(soapAction, edcLk01.stuurgegevens.zender.applicatie);
            response = convertor.Convert(zaakService, repository, edcLk01);
        }


        try {
            SOAPPart soapPart = MessageFactory.newInstance()
                    .createMessage(null, new ByteArrayInputStream(body.getBytes())).getSOAPPart();

            switch (getActionFromSoapHeader(soapPart)) {
                case "http://www.egem.nl/StUF/sector/zkn/0310/actualiseerZaakstatus_Lk01": {
                    ZakLk01_v2 zakLk01 = (ZakLk01_v2) JAXBContext.newInstance(ZakLk01_v2.class)
                            .createUnmarshaller()
                            .unmarshal(soapPart.getEnvelope().getBody().extractContentAsDocument());
                    actualiseerZaakstatus(zakLk01);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void actualiseerZaakstatus(ZakLk01_v2 zakLk01) {
        // TODO: call ZGW api
    }

    private String getActionFromSoapHeader(SOAPPart soapPart) throws SOAPException {
        NodeList nodeList = soapPart.getEnvelope().getHeader().getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", "Action");
        if (nodeList.getLength() > 0) return nodeList.item(0).getFirstChild().getNodeValue();
        return "";
    }

//    private void voegZaakDocumentToe(StufRequest stufRequest) {
//        EdcLk01 edcLk01 = stufRequest.getEdcLk01();
//        try {
//            ZgwZaakInformatieObject zgwZaakInformatieObject = zaakService.voegZaakDocumentToe(edcLk01);
//            setResponseToDocumentBv03(zgwZaakInformatieObject);
//        } catch (Exception e) {
//            handleAddZaakException(e);
//        }
//
//    }

    private void setResponseToZaakBv03(ZgwZaak createdZaak) {
        var bv03 = new Bv03();
        bv03.setReferentienummer(createdZaak.getUuid());
        response = bv03.getSoapMessageAsString();
    }

    private void setResponseToDocumentBv03(ZgwZaakInformatieObject zgwZaakInformatieObject) {
        var bv03 = new Bv03();
        bv03.setReferentienummer(zgwZaakInformatieObject.getUuid());
        response = bv03.getSoapMessageAsString();
    }

    private void handleAddZaakException(Exception e) {
        var f03 = new F03();
        f03.setFaultString("Object was not saved");
        f03.setCode("StUF046");
        f03.setOmschrijving("Object niet opgeslagen");
        f03.setDetails(e.getMessage());
        response = f03.getSoapMessageAsString();
    }

    private void handleFetchZaakException(Exception e) {
        var f03 = new F03();
        f03.setFaultString("Object was not found");
        f03.setCode("StUF064");
        f03.setOmschrijving("Object niet gevonden");
        f03.setDetails(e.getMessage());
        response = f03.getSoapMessageAsString();
    }
}
