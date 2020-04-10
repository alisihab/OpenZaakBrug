package nl.haarlem.translations.zdstozgw.convertor.impl;

import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;

public class VoegZaakdocumentToe extends Convertor {

    public VoegZaakdocumentToe(String template, String legacyService) {
        super(template, legacyService);
    }

    @Override
    public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestBody) {
        try {
        	EdcLk01 object = nl.haarlem.translations.zdstozgw.controller.SoapController.getZakLEdcLk01(requestBody);
            ZgwZaakInformatieObject zgwZaakInformatieObject = zaakService.voegZaakDocumentToe((EdcLk01) object);
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