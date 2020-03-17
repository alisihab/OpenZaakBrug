package nl.haarlem.translations.zdstozgw.controller;

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
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.soap.MessageFactory;
import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;

@RestController
public class SoapController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private ZaakService zaakService;

    private String response = "NOT IMPLEMENTED";

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

    @PostMapping(value = "/OntvangAsynchroon", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public String ontvangAsynchroon(@RequestBody String body) {


        try {
            JAXBContext.newInstance(ZakLk01_v2.class)
                    .createUnmarshaller()
                    .unmarshal(MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(body.getBytes())).getSOAPBody().extractContentAsDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }

        var stufRequest = new StufRequest(XmlUtils.convertStringToDocument(body));

        if (stufRequest.isCreeerZaak()) {
            creerZaak(stufRequest);
        }
        if (stufRequest.isVoegZaakdocumentToe()) {
            voegZaakDocumentToe(stufRequest);
        }

        return response;
    }

    private void voegZaakDocumentToe(StufRequest stufRequest) {
        EdcLk01 edcLk01 = stufRequest.getEdcLk01();
        try {
            ZgwZaakInformatieObject zgwZaakInformatieObject = zaakService.voegZaakDocumentToe(edcLk01);
            setResponseToDocumentBv03(zgwZaakInformatieObject);
        } catch (Exception e) {
            handleAddZaakException(e);
        }

    }

    private void creerZaak(StufRequest stufRequest) {
        try {
            ZakLk01 zakLk01 = stufRequest.getZakLk01();
            var zaak = zaakService.creeerZaak(zakLk01);
            setResponseToZaakBv03(zaak);
        } catch (Exception ex) {
            handleAddZaakException(ex);
        }
    }

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
