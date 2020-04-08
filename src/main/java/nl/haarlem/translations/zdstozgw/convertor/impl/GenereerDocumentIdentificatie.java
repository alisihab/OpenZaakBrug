package nl.haarlem.translations.zdstozgw.convertor.impl;


import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import lombok.Data;

import nl.haarlem.translations.zdstozgw.translation.zds.model.StufRequest;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;
import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

import nl.haarlem.translations.zdstozgw.jpa.model.ApplicationParameter;

public class GenereerDocumentIdentificatie extends Convertor {

	@Data
	private class GenereerDocumentIdentificatie_Di02 {
	    final XpathDocument xpathDocument;
	    Document document;
	    
		public GenereerDocumentIdentificatie_Di02(StufRequest stufRequest) {
	        this.document = stufRequest.body;
	        this.xpathDocument  = new XpathDocument(document);			
		}
	}
	
	@Data
	private class GenereerDocumentIdentificatie_Du02 {
	    final XpathDocument xpathDocument;
	    Document document;

		public GenereerDocumentIdentificatie_Du02(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);			
			this.xpathDocument  = new XpathDocument(document);
		}
	}	
	
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	protected String template;
	
    public GenereerDocumentIdentificatie(String template) {
        this.template = template;
    }

    @Override
    public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, Object object) {
    	var stufRequest = (StufRequest) object;
    	DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");   	    
    	
    	var prefixparam  = repository.getOne("DocumentIdentificatiePrefix");
    	var idparam= repository.getOne("DocumentIdentificatieHuidige");    	
    	var identificatie = Long.parseLong(idparam.getParameterValue()) + 1;	
    	idparam.setParameterValue(Long.toString(identificatie));
    	repository.save(idparam);
    	    	
    	var di02 = new GenereerDocumentIdentificatie_Di02(stufRequest);
    	var du02 = new GenereerDocumentIdentificatie_Du02(this.template); 	    	    	
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:organisatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:applicatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:gebruiker"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:organisatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:applicatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:gebruiker"));
    	du02.xpathDocument.setNodeValue(".//stuf:referentienummer", di02.xpathDocument.getNodeValue(".//stuf:referentienummer"));    	
    	du02.xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));    
    	du02.xpathDocument.setNodeValue(".//zkn:identificatie", prefixparam.getParameterValue() + identificatie);
    	
    	return XmlUtils.xmlToString(du02.document);
    }

	@Override
	public String getImplementation() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public String getTemplate() {
		return this.template;
	}
}