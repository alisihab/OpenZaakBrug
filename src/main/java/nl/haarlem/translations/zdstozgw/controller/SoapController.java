package nl.haarlem.translations.zdstozgw.controller;

import nl.haarlem.translations.zdstozgw.convertor.ConvertorFactory;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.RequestResponseCycleRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaak;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.Instant;

@RestController
public class SoapController {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	enum ReplicationModus {
		USE_ZDS, USE_ZDS_AND_REPLICATE_2_ZWG, USE_ZWG_AND_REPLICATE_2_ZDS, USE_ZWG
	}

	@Autowired
	private ZaakService zaakService;
	@Autowired
	protected ApplicationParameterRepository repository;
	@Autowired
	protected RequestResponseCycleRepository sessions;

	ReplicationModus replicationModus = ReplicationModus.USE_ZWG;

	@PostMapping(value = "/VrijBerichtService", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<?> vrijBerichtService(@RequestHeader(name = "SOAPAction", required = true) String soapAction, @RequestBody String body) {
		// store our information as fast as possible
		var beginTime = Instant.now();
		var session = new RequestResponseCycle("VrijBerichtService", soapAction.replace("\"", ""), body);
		sessions.save(session);

		// what we will return
		String responseBody = null;
		var responseCode = HttpStatus.OK;

		try {
			// get the right convertor
			var convertor = ConvertorFactory.getConvertor(soapAction.replace("\"", ""), body);
			if (convertor != null) {
				// session.setConverter(convertor.getClass().getCanonicalName());
				session.setConverterImplementation(convertor.getImplementation());
				session.setConverterTemplate(convertor.getTemplate());
				sessions.save(session);
			} else {
				throw new RuntimeException("no convertor found for soapaction:" + soapAction);
			}
			// do the correct action
			switch (replicationModus) {
				case USE_ZDS:
					break;
				case USE_ZDS_AND_REPLICATE_2_ZWG:
					break;
				case USE_ZWG_AND_REPLICATE_2_ZDS:	
					break;
				case USE_ZWG:
					responseBody = convertor.Convert(zaakService, repository, new StufRequest(XmlUtils.convertStringToDocument(body)));
					session.setZgwResponeBody(responseBody);
					break;
				default:
			}
		}
		catch(Exception ex) {
			// handle the error nice 
			
		    Document document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument("nl/haarlem/translations/controller/Fault_F0.xml");
		    XpathDocument xpathDocument = new XpathDocument(document);
		    		    
		    xpathDocument.setNodeValue(".//faultstring", ex.toString());
		    xpathDocument.setNodeValue(".//stuf:omschrijving", ex.toString());
		    var swriter = new java.io.StringWriter();
		    var pwriter = new java.io.PrintWriter(swriter);
		    var stackTrace = swriter.toString();
		    session.setStackTrace(stackTrace);
		    xpathDocument.setNodeValue(".//stuf:details", stackTrace);		    
		    xpathDocument.setNodeValue(".//stuf:detailsXML", body);
    	
		    responseBody =  XmlUtils.xmlToString(document);
			responseCode = HttpStatus.INTERNAL_SERVER_ERROR;
		}
			
		// after all the work
		session.setClientResponseCode(responseCode.toString());
		session.setClientRequestBody(responseBody);
		session.setDuration(Duration.between(beginTime, Instant.now()));
		sessions.save(session);
		return new ResponseEntity<>(responseBody, responseCode);
	}

	@PostMapping(value = "/BeantwoordVraag", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
	public String beantwoordVraag(@RequestHeader(name = "SOAPAction", required = true) String soapAction, @RequestBody String body) {
		var stufRequest = new StufRequest(XmlUtils.convertStringToDocument(body));
		String response = "NOT IMPLEMENTED! (" + soapAction + ")";

		if (stufRequest.isgeefZaakDetails()) {
			try {
				response = XmlUtils.xmlToString(zaakService.getZaakDetails(stufRequest.getZakLv01ZaakDetails()));
			} catch (Exception ex) {
				handleFetchZaakException(ex);
			}
		}

		if (stufRequest.isgeefLijstZaakdocumenten()) {
			try {
				response = XmlUtils
						.xmlToString(zaakService.getLijstZaakdocumenten(stufRequest.getZakLv01LijstZaakdocumenten()));
			} catch (Exception ex) {
				handleFetchZaakException(ex);
			}
		}

		return response;
	}

	private ZakLk01_v2 getZakLka01(String body) {
		ZakLk01_v2 zakLk01 = null;
		try {
			zakLk01 = (ZakLk01_v2) JAXBContext.newInstance(ZakLk01_v2.class).createUnmarshaller().unmarshal(
					MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(body.getBytes()))
							.getSOAPBody().extractContentAsDocument());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zakLk01;
	}

	private EdcLk01 getZakLEdcLk01(String body) {
		EdcLk01 edcLk01 = null;
		try {
			edcLk01 = (EdcLk01) JAXBContext.newInstance(EdcLk01.class).createUnmarshaller().unmarshal(
					MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(body.getBytes()))
							.getSOAPBody().extractContentAsDocument());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return edcLk01;
	}

	@PostMapping(value = "/OntvangAsynchroon", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
	public String ontvangAsynchroon(@RequestHeader(name = "SOAPAction", required = true) String soapAction,
			@RequestBody String body) {
		soapAction = soapAction.replace("\"", "");
		Convertor convertor = null;
		String response = "NOT IMPLEMENTED! (" + soapAction + ")";

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
				ZakLk01_v2 zakLk01 = (ZakLk01_v2) JAXBContext.newInstance(ZakLk01_v2.class).createUnmarshaller()
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
		NodeList nodeList = soapPart.getEnvelope().getHeader()
				.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", "Action");
		if (nodeList.getLength() > 0)
			return nodeList.item(0).getFirstChild().getNodeValue();
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

//	private String setResponseToZaakBv03(ZgwZaak createdZaak) {
//		var bv03 = new Bv03();
//		bv03.setReferentienummer(createdZaak.getUuid());
//		return bv03.getSoapMessageAsString();
//	}
//
//	private String setResponseToDocumentBv03(ZgwZaakInformatieObject zgwZaakInformatieObject) {
//		var bv03 = new Bv03();
//		bv03.setReferentienummer(zgwZaakInformatieObject.getUuid());
//		return bv03.getSoapMessageAsString();
//	}
//
//	private String handleAddZaakException(Exception e) {
//		var f03 = new F03();
//		f03.setFaultString("Object was not saved");
//		f03.setCode("StUF046");
//		f03.setOmschrijving("Object niet opgeslagen");
//		f03.setDetails(e.getMessage());
//		return f03.getSoapMessageAsString();
//	}

	private String handleFetchZaakException(Exception e) {
		var f03 = new F03();
		f03.setFaultString("Object was not found");
		f03.setCode("StUF064");
		f03.setOmschrijving("Object niet gevonden");
		f03.setDetails(e.getMessage());
		return f03.getSoapMessageAsString();
	}
}
