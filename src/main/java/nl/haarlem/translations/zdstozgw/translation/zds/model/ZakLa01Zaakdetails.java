package nl.haarlem.translations.zdstozgw.translation.zds.model;

import org.w3c.dom.Document;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

@Data
public class ZakLa01Zaakdetails {

	private final XpathDocument xpathDocument;
	private Document document;

	public ZakLa01Zaakdetails() {
		getBaseDocument();
		this.xpathDocument = new XpathDocument(this.document);
		this.xpathDocument.setNodeValue("//stuf:tijdstipBericht", StufUtils.getTijdstipBericht());
	}

	public void setIdentificatie(String identificatie) {
		this.xpathDocument.setNodeValue("//zkn:identificatie", identificatie);
	}

	public void setOmschrijving(String omschrijving) {
		this.xpathDocument.setNodeValue("//zkn:omschrijving", omschrijving);
	}

	public void setToelichting(String toelichting) {
		this.xpathDocument.setNodeValue("//zkn:toelichting", toelichting);
	}

	public void setResultaat(String omschrijving, String toelichting) {
		this.xpathDocument.setNodeValue("//zkn:resultaat/zkn:omschrijving", omschrijving);
		this.xpathDocument.setNodeValue("//zkn:resultaat/zkn:toelichting", toelichting);
	}

	public void setStartDatum(String startDatum) {
		this.xpathDocument.setNodeValue("//zkn:startdatum", startDatum);
	}

	public void setRegistratieDatum(String registratieDatum) {
		this.xpathDocument.setNodeValue("//zkn:registratiedatum", registratieDatum);
	}

	public void setPublicatieDatum(String publicatieDatum) {
		if (publicatieDatum != null) {
			this.xpathDocument.setNodeValue("//zkn:publicatiedatum", publicatieDatum);
		} else {
			this.xpathDocument.setNodeEmpty("//zkn:publicatiedatum");
		}
	}

	public void setEinddatumGepland(String einddatumGepland) {
		if (einddatumGepland != null) {
			this.xpathDocument.setNodeValue("//zkn:einddatumGepland", einddatumGepland);
		} else {
			this.xpathDocument.setNodeEmpty("//zkn:einddatumGepland");
		}
	}

	public void setUiterlijkeEinddatum(String uiterlijkeEinddatum) {
		if (uiterlijkeEinddatum != null) {
			this.xpathDocument.setNodeValue("//zkn:uiterlijkeEinddatum", uiterlijkeEinddatum);
		} else {
			this.xpathDocument.setNodeEmpty("//zkn:uiterlijkeEinddatum");
		}
	}

	public void setEinddatum(String einddatum) {
		if (einddatum != null) {
			this.xpathDocument.setNodeValue("//zkn:einddatum", einddatum);
		} else {
			this.xpathDocument.setNodeEmpty("//zkn:einddatum");
		}
	}

	public void setArchiefNominatie(String archiefNominate) {
		this.xpathDocument.setNodeValue("//zkn:archiefnominatie", archiefNominate);
	}

	public void setDatumVernietigingDossier(String datumVernietigingDossier) {
		if (datumVernietigingDossier == null) {
			this.xpathDocument.setNodeValue("//zkn:datumVernietigingDossier", datumVernietigingDossier);
		} else {
			this.xpathDocument.setNodeEmpty("//zkn:datumVernietigingDossier");
		}

	}

	public void setEmptyResultaat() {
		this.xpathDocument.setNodeEmpty("//zkn:resultaat/zkn:omschrijving");
		this.xpathDocument.setNodeEmpty("//zkn:resultaat/zkn:toelichting");
	}

	public void setZaakTypeOmschrijving(String zaakTypeOmschrijving) {
		this.xpathDocument.setNodeValue("//zkn:isVan/zkn:gerelateerde/zkn:omschrijving", zaakTypeOmschrijving);
	}

	public void setZaakTypeCode(String zaakTypeCode) {
		this.xpathDocument.setNodeValue("//zkn:isVan/zkn:gerelateerde/zkn:code", zaakTypeCode);
	}

	public void setZaakTypeIngangsDatumObject(String zaakTypeIngangsDatumObject) {
		this.xpathDocument.setNodeValue("//zkn:isVan/zkn:gerelateerde/zkn:ingangsdatumObject", zaakTypeIngangsDatumObject);
	}

	private void getBaseDocument() {
		this.document = XmlUtils.convertStringToDocument(
				"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.stufstandaarden.nl/koppelvlak/zds0120\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
						+ "   <soap11env:Body>\n" + "      <tns:geefZaakdetails_ZakLa01>\n"
						+ "         <ns2:stuurgegevens xmlns:ns2=\"http://www.egem.nl/StUF/sector/zkn/0310\">\n"
						+ "            <ns0:berichtcode xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">La01</ns0:berichtcode>\n"
						+ "            <ns3:zender xmlns:ns3=\"http://www.egem.nl/StUF/StUF0301\">\n"
						+ "               <ns3:organisatie>0392</ns3:organisatie>\n"
						+ "               <ns3:applicatie>ZSH</ns3:applicatie>\n" + "            </ns3:zender>\n"
						+ "            <ns4:ontvanger xmlns:ns4=\"http://www.egem.nl/StUF/StUF0301\">\n"
						+ "               <ns4:organisatie>0392</ns4:organisatie>\n"
						+ "               <ns4:applicatie>ZSH</ns4:applicatie>\n" + "            </ns4:ontvanger>\n"
						+ "            <ns0:referentienummer xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">039255309169-0d98-4529-8f30-440bf5acbc12</ns0:referentienummer>\n"
						+ "            <ns0:tijdstipBericht xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\"></ns0:tijdstipBericht>\n"
						+ "            <ns0:entiteittype xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">ZAK</ns0:entiteittype>\n"
						+ "         </ns2:stuurgegevens>\n"
						+ "         <ns5:parameters xmlns:ns5=\"http://www.egem.nl/StUF/sector/zkn/0310\">\n"
						+ "            <ns0:indicatorVervolgvraag xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">false</ns0:indicatorVervolgvraag>\n"
						+ "            <ns0:indicatorAfnemerIndicatie xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">false</ns0:indicatorAfnemerIndicatie>\n"
						+ "            <ns0:aantalVoorkomens xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">0</ns0:aantalVoorkomens>\n"
						+ "         </ns5:parameters>\n"
						+ "         <ns6:antwoord xmlns:ns6=\"http://www.egem.nl/StUF/sector/zkn/0310\">\n"
						+ "            <ns6:object ns26:entiteittype=\"ZAK\" xmlns:ns26=\"http://www.egem.nl/StUF/StUF0301\">\n"
						+ "               <ns6:identificatie></ns6:identificatie>\n"
						+ "               <ns6:omschrijving></ns6:omschrijving>\n"
						+ "               <ns6:toelichting></ns6:toelichting>\n" + "               <ns6:resultaat>\n"
						+ "                  <ns6:omschrijving />\n" + "                  <ns6:toelichting />\n"
						+ "               </ns6:resultaat>\n" + "               <ns6:startdatum></ns6:startdatum>\n"
						+ "               <ns6:registratiedatum></ns6:registratiedatum>\n"
						+ "               <ns6:publicatiedatum/>\n" + "               <ns6:einddatumGepland/>\n"
						+ "               <ns6:uiterlijkeEinddatum/>\n" + "               <ns6:einddatum/>\n"
						+ "               <ns6:betalingsIndicatie xsi:nil=\"true\" ns26:noValue=\"geenWaarde\"/>\n"
						+ "               <ns6:laatsteBetaaldatum xsi:nil=\"true\" ns26:noValue=\"geenWaarde\"/>\n"
						+ "               <ns6:archiefnominatie/>\n"
						+ "               <ns6:datumVernietigingDossier/>\n"
						+ "               <ns6:zaakniveau>1</ns6:zaakniveau>\n"
						+ "               <ns6:deelzakenIndicatie>N</ns6:deelzakenIndicatie>\n"
						+ "               <ns26:tijdvakGeldigheid>\n"
						+ "                  <ns26:beginGeldigheid xsi:nil=\"true\" ns26:noValue=\"geenWaarde\"/>\n"
						+ "                  <ns26:eindGeldigheid xsi:nil=\"true\" ns26:noValue=\"geenWaarde\"/>\n"
						+ "               </ns26:tijdvakGeldigheid>\n"
						+ "               <ns26:tijdstipRegistratie xsi:nil=\"true\" ns26:noValue=\"geenWaarde\"/>\n"
						+ "               <ns6:isVan ns26:entiteittype=\"ZAKZKT\">\n"
						+ "                  <ns6:gerelateerde ns26:entiteittype=\"ZKT\">\n"
						+ "                     <ns6:omschrijving/>\n" + "                     <ns6:code/>\n"
						+ "                     <ns6:ingangsdatumObject/>\n" + "                  </ns6:gerelateerde>\n"
						+ "               </ns6:isVan>\n" + "            </ns6:object>\n" + "         </ns6:antwoord>\n"
						+ "      </tns:geefZaakdetails_ZakLa01>\n" + "   </soap11env:Body>\n"
						+ "</soap11env:Envelope>");
	}

}
