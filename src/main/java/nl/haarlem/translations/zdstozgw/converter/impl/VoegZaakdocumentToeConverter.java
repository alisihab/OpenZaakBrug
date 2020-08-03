package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Fo03;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class VoegZaakdocumentToeConverter extends Converter {

    public VoegZaakdocumentToeConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        EdcLk01 edcLk01 = new EdcLk01();
        try {
            edcLk01 = (EdcLk01) XmlUtils.getStUFObject(request, EdcLk01.class);
            this.getZaakService().voegZaakDocumentToe(edcLk01);
            var bv03 = new Bv03(edcLk01.stuurgegevens);
            return XmlUtils.getSOAPMessageFromObject(bv03, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new Fo03(edcLk01.stuurgegevens);
            fo03.body = new Fo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }
}
