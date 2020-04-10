package nl.haarlem.translations.zdstozgw.convertor.impl;

import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class NotImplemented extends Convertor {

    public NotImplemented(String template, String legacyService) {
        super(template, legacyService);
    }

    @Override
    public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestbody) {
    	throw new RuntimeException("Not implemented");
    }
}