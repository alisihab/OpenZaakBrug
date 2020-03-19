package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

import java.lang.Object;

public class GeefZaakDocumentLezenConverter implements Converter {
    protected String templatePath;

    public GeefZaakDocumentLezenConverter(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public String Convert(ZaakService zaakService, Object object) {
        String result = "";
        try {
            EdcLa01 edcLa01 = zaakService.getZaakDoumentLezen((EdcLv01) object);
            edcLa01.stuurgegevens = new Stuurgegevens();
            edcLa01.stuurgegevens.zender = new Zender();
            edcLa01.stuurgegevens.zender.applicatie = ((EdcLv01) object).stuurgegevens.ontvanger.applicatie;
            edcLa01.stuurgegevens.zender.organisatie =  ((EdcLv01) object).stuurgegevens.ontvanger.organisatie;
            edcLa01.stuurgegevens.zender.gebruiker = ((EdcLv01) object).stuurgegevens.ontvanger.organisatie;

            edcLa01.stuurgegevens.ontvanger = new Ontvanger();
            edcLa01.stuurgegevens.ontvanger.applicatie = ((EdcLv01) object).stuurgegevens.zender.applicatie;
            edcLa01.stuurgegevens.ontvanger.organisatie = ((EdcLv01) object).stuurgegevens.zender.organisatie;
            edcLa01.stuurgegevens.ontvanger.gebruiker = ((EdcLv01) object).stuurgegevens.zender.gebruiker;

            edcLa01.stuurgegevens.berichtcode = "La01";

            return XmlUtils.getSOAPMessageFromObject(edcLa01);

        } catch (Exception ex) {
            ex.printStackTrace();
            var f03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
            f03.setFaultString("Object was not saved");
            f03.setCode("StUF046");
            f03.setOmschrijving("Object niet opgeslagen");
            f03.setDetails(ex.getMessage());
            return f03.getSoapMessageAsString();
        }
    }

}

