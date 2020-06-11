package nl.haarlem.translations.zdstozgw.converter.impl;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

@Data
public class VoegZaakdocumentToeConverter extends Converter {

    public VoegZaakdocumentToeConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        try {
            EdcLk01 edcLk01 = (EdcLk01) XmlUtils.getStUFObject(request, EdcLk01.class);
            ZgwZaakInformatieObject zgwZaakInformatieObject = this.getZaakService().voegZaakDocumentToe((EdcLk01) edcLk01);
            var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
            bv03.setReferentienummer(zgwZaakInformatieObject.getUuid());
            return bv03.getSoapMessageAsString();

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
