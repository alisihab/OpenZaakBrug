package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.converter.Convertor;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class CreeerZaakConverter implements Convertor {
    protected String templatePath;

    public CreeerZaakConverter(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public String Convert(ZaakService zaakService, Object object) {
        try {

            var zaak = zaakService.creeerZaak((ZakLk01_v2) object);
            var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
            bv03.setReferentienummer(zaak.getUuid());
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