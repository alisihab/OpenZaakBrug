package nl.haarlem.translations.zdstozgw.convertor.impl;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.Document;
import lombok.Data;

import nl.haarlem.translations.zdstozgw.translation.zds.model.StufRequest;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;
import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class GenereerZaakIdentificatie implements Convertor {

	@Data
	private class GenereerZaakIdentificatie_Di02 {
	    final XpathDocument xpathDocument;
	    Document document;
	    
		public GenereerZaakIdentificatie_Di02(StufRequest stufRequest) {
	        this.document = stufRequest.body;
	        this.xpathDocument  = new XpathDocument(document);			
		}
	}
	
	@Data
	private class GenereerZaakIdentificatie_Du02 {
	    final XpathDocument xpathDocument;
	    Document document;

		public GenereerZaakIdentificatie_Du02(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);			
			this.xpathDocument  = new XpathDocument(document);
		}
	}	
	
	protected String template;

    public GenereerZaakIdentificatie(String template) {
        this.template = template;
    }

    @Override
    public String Convert(ZaakService zaakService, Object object) {
    	var stufRequest = (StufRequest) object;
    	DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");
   	        
    	// TODO: Wat is voorkeur manier om de laatst gegenereerde identifier persistent te maken?
    	DateFormat identifierformat = new SimpleDateFormat("yyyyMMddHHmmssSS");
    	var zaakidentifier = identifierformat.format(new Date());
    	
    	var di02 = new GenereerZaakIdentificatie_Di02(stufRequest);
    	var du02 = new GenereerZaakIdentificatie_Du02(this.template); 	    	    	
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:organisatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:applicatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:gebruiker"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:organisatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:applicatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:gebruiker"));
    	du02.xpathDocument.setNodeValue(".//stuf:referentienummer", di02.xpathDocument.getNodeValue(".//stuf:referentienummer"));    	
    	du02.xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));    
    	du02.xpathDocument.setNodeValue(".//zkn:identificatie", zaakidentifier);
    	
    	return XmlUtils.xmlToString(du02.document);
    }
}