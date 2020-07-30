package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakdocumentLezenConverter extends Converter {

    public GeefZaakdocumentLezenConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        EdcLv01 edcLv01 = new EdcLv01();
        try {
            edcLv01 = (EdcLv01) XmlUtils.getStUFObject(request, EdcLv01.class);
            EdcLa01 edcLa01 = this.getZaakService().getZaakDocumentLezen(edcLv01);
            return XmlUtils.getSOAPMessageFromObject(edcLa01, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new Fo03(edcLv01.stuurgegevens);
            fo03.body = new Fo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }

}

