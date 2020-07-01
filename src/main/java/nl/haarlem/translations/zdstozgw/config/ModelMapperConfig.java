package nl.haarlem.translations.zdstozgw.config;

import nl.haarlem.translations.zdstozgw.translation.zds.model.Gerelateerde;
import nl.haarlem.translations.zdstozgw.translation.zds.model.NatuurlijkPersoon;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Rol;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwNatuurlijkPersoon;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwRol;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaak;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static nl.haarlem.translations.zdstozgw.translation.BetrokkeneType.NATUURLIJK_PERSOON;

@Configuration
public class ModelMapperConfig {

    @Autowired
    ZGWClient zgwClient;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration() // Fetch the configuration
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull());

        modelMapper.typeMap(ZgwZaak.class, ZakLa01.Antwoord.Zaak.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getStartdatum, ZakLa01.Antwoord.Zaak::setStartdatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getRegistratiedatum, ZakLa01.Antwoord.Zaak::setRegistratiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getPublicatiedatum, ZakLa01.Antwoord.Zaak::setPublicatiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getEinddatumGepland, ZakLa01.Antwoord.Zaak::setEinddatumGepland))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getUiterlijkeEinddatumAfdoening, ZakLa01.Antwoord.Zaak::setUiterlijkeEinddatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getEinddatum, ZakLa01.Antwoord.Zaak::setEinddatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getArchiefactiedatum, ZakLa01.Antwoord.Zaak::setDatumVernietigingDossier))
                .addMappings(mapper -> mapper.using(convertZgwArchiefNomitieToZdsArchiefNominatie()).map(ZgwZaak::getArchiefnominatie, ZakLa01.Antwoord.Zaak::setArchiefnominatie));


        modelMapper.typeMap(ZgwNatuurlijkPersoon.class, NatuurlijkPersoon.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwNatuurlijkPersoon::getGeboortedatum, NatuurlijkPersoon::setGeboortedatum))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZgwNatuurlijkPersoon::getGeslachtsaanduiding, NatuurlijkPersoon::setGeslachtsaanduiding));

        modelMapper.addConverter(convertZgwRolToZdsRol());

        return modelMapper;
    }

    private AbstractConverter<String, String> convertDateStringToStufDate() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String s) {
                if (s == null) {
                    return null;
                }
                var year = s.substring(0, 4);
                var month = s.substring(5, 7);
                var day = s.substring(8, 10);
                return year + month + day;
            }
        };
    }

    private AbstractConverter<String, String> convertZgwArchiefNomitieToZdsArchiefNominatie() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String s) {
                if (s.toUpperCase().equals("vernietigen")) {
                    return "J";
                } else {
                    return "N";
                }
            }
        };
    }

    private AbstractConverter<String, String> convertToLowerCase() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String s) {
                return s.toLowerCase();
            }
        };
    }

    private AbstractConverter<ZgwRol, Rol> convertZgwRolToZdsRol() {
        return new AbstractConverter<>() {
            @Override
            protected Rol convert(ZgwRol zgwRol) {
                Rol rol = new Rol();
                rol.gerelateerde = new Gerelateerde();
                if(zgwRol.getBetrokkeneType().equalsIgnoreCase(NATUURLIJK_PERSOON.getDescription())){
                    rol.gerelateerde.natuurlijkPersoon = modelMapper().map(zgwRol.betrokkeneIdentificatie, NatuurlijkPersoon.class);
                }else {
                    throw new RuntimeException("Betrokkene type nog niet geïmplementeerd");
                }
                return rol;
            }
        };
    }



}
