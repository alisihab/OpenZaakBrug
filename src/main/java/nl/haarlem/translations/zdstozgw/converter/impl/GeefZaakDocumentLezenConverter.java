package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakDocumentLezenConverter extends Converter {

    public GeefZaakDocumentLezenConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        String result = "";
        try {
            EdcLv01 edcLv01 = (EdcLv01) XmlUtils.getStUFObject(request, EdcLv01.class);
            EdcLa01 edcLa01 = this.getZaakService().getZaakDocumentLezen((EdcLv01) edcLv01);
            edcLa01 = getEdcLa01WithStuurgegevens((EdcLv01) edcLv01, edcLa01);

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

    private EdcLa01 getEdcLa01WithStuurgegevens(EdcLv01 object, EdcLa01 edcLa01) {
        edcLa01.stuurgegevens = new Stuurgegevens();
        edcLa01.stuurgegevens.zender = new Zender();
        edcLa01.stuurgegevens.zender.applicatie = object.stuurgegevens.ontvanger.applicatie;
        edcLa01.stuurgegevens.zender.organisatie =  object.stuurgegevens.ontvanger.organisatie;
        edcLa01.stuurgegevens.zender.gebruiker = object.stuurgegevens.ontvanger.organisatie;

        edcLa01.stuurgegevens.ontvanger = new Ontvanger();
        edcLa01.stuurgegevens.ontvanger.applicatie = object.stuurgegevens.zender.applicatie;
        edcLa01.stuurgegevens.ontvanger.organisatie = object.stuurgegevens.zender.organisatie;
        edcLa01.stuurgegevens.ontvanger.gebruiker = object.stuurgegevens.zender.gebruiker;
        return edcLa01;
    }

}

