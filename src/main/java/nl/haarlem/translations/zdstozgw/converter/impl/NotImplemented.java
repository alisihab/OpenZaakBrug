package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class NotImplemented extends Converter {

    public NotImplemented(String template, String legacyService) {
        super(template, legacyService);
    }

    @Override
    public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestbody) {
    	throw new RuntimeException("Not implemented");
    }
}