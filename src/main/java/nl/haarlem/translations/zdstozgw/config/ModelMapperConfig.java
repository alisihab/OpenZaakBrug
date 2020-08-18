package nl.haarlem.translations.zdstozgw.config;

import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static nl.haarlem.translations.zdstozgw.translation.BetrokkeneType.MEDEWERKER;
import static nl.haarlem.translations.zdstozgw.translation.BetrokkeneType.NATUURLIJK_PERSOON;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration() // Fetch the configuration
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull());

        modelMapper.typeMap(ZgwStatus.class, ZdsZakLa01GeefZaakDetails.Status.class)
                .addMappings(mapper -> mapper.map(ZgwStatus::getStatustoelichting, ZdsZakLa01GeefZaakDetails.Status::setToelichting));

        modelMapper.typeMap(ZgwZaakInformatieObject.class, ZdsZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaakInformatieObject::getRegistratiedatum, ZdsZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant::setRegistratiedatum));

        modelMapper.typeMap(ZdsHeeft.class, ZgwStatus.class)
                .addMappings(mapper -> mapper.using(convertStufDateToDateTimeString()).map(ZdsHeeft::getDatumStatusGezet, ZgwStatus::setDatumStatusGezet));

        modelMapper.typeMap(ZdsZaak.Opschorting.class, ZgwOpschorting.class)
                .addMappings(mapper -> mapper.using(convertStringToBoolean()).map(ZdsZaak.Opschorting::getIndicatie, ZgwOpschorting::setIndicatie));

        addZgwZaakToZdsZaakTypeMapping(modelMapper);
        addZgwBetrokkeneIdentificatieToNatuurlijkPersoonTypeMapping(modelMapper);
        addZgwEnkelvoudigInformatieObjectToZaakDocumentTypeMapping(modelMapper);
        addZgwEnkelvoudigInformatieObjectToZdsZaakDocumentDetailTypeMapping(modelMapper);
        addZdsZaakToZgwZaakTypeMapping(modelMapper);
        addZdsNatuurlijkPersoonToZgwBetrokkeneIdentificatieTypeMapping(modelMapper);
        addZdsZaakDocumentToZgwEnkelvoudigInformatieObjectTypeMapping(modelMapper);
        addZgwZaakToGeefZaakDetailsTypeMappingTypeMapping(modelMapper);

        modelMapper.addConverter(convertZgwRolToZdsRol());

        return modelMapper;
    }

    private void addZgwBetrokkeneIdentificatieToNatuurlijkPersoonTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwBetrokkeneIdentificatie.class, ZdsNatuurlijkPersoon.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwBetrokkeneIdentificatie::getGeboortedatum, ZdsNatuurlijkPersoon::setGeboortedatum))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZgwBetrokkeneIdentificatie::getGeslachtsaanduiding, ZdsNatuurlijkPersoon::setGeslachtsaanduiding))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZgwBetrokkeneIdentificatie::getInpBsn, ZdsNatuurlijkPersoon::setBsn));
    }

    private void addZgwZaakToZdsZaakTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwZaak.class, ZdsZaak.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getStartdatum, ZdsZaak::setStartdatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getRegistratiedatum, ZdsZaak::setRegistratiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getPublicatiedatum, ZdsZaak::setPublicatiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getEinddatumGepland, ZdsZaak::setEinddatumGepland))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getUiterlijkeEinddatumAfdoening, ZdsZaak::setUiterlijkeEinddatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getEinddatum, ZdsZaak::setEinddatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getArchiefactiedatum, ZdsZaak::setDatumVernietigingDossier))
                .addMappings(mapper -> mapper.using(convertZgwArchiefNomitieToZdsArchiefNominatie()).map(ZgwZaak::getArchiefnominatie, ZdsZaak::setArchiefnominatie));
    }

    private void addZgwZaakToGeefZaakDetailsTypeMappingTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwZaak.class, ZdsZakLa01GeefZaakDetails.Antwoord.Object.class)
                .includeBase(ZgwZaak.class, ZdsZaak.class);
    }

    private void addZgwEnkelvoudigInformatieObjectToZaakDocumentTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwEnkelvoudigInformatieObject.class, ZdsZaakDocument.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwEnkelvoudigInformatieObject::getCreatiedatum, ZdsZaakDocument::setCreatiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwEnkelvoudigInformatieObject::getOntvangstdatum, ZdsZaakDocument::setOntvangstdatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwEnkelvoudigInformatieObject::getVerzenddatum, ZdsZaakDocument::setVezenddatum))
                .addMappings(mapper -> mapper.using(convertToUpperCase()).map(ZgwEnkelvoudigInformatieObject::getVertrouwelijkheidaanduiding, ZdsZaakDocument::setVertrouwelijkheidAanduiding))
                .addMappings(mapper -> mapper.map(ZgwEnkelvoudigInformatieObject::getUrl, ZdsZaakDocument::setLink));
    }

    public void addZdsZaakToZgwZaakTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZdsZaak.class, ZgwZaakPut.class)
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZdsZaak::getRegistratiedatum, ZgwZaakPut::setRegistratiedatum))
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZdsZaak::getStartdatum, ZgwZaakPut::setStartdatum))
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZdsZaak::getEinddatumGepland, ZgwZaakPut::setEinddatumGepland))
                .addMappings(mapper -> mapper.using(getZGWArchiefNominatie()).map(ZdsZaak::getArchiefnominatie, ZgwZaakPut::setArchiefnominatie));
    }

    public void addZdsNatuurlijkPersoonToZgwBetrokkeneIdentificatieTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZdsNatuurlijkPersoon.class, ZgwBetrokkeneIdentificatie.class)
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZdsNatuurlijkPersoon::getGeboortedatum, ZgwBetrokkeneIdentificatie::setGeboortedatum))
                .addMappings(mapper -> mapper.map(ZdsNatuurlijkPersoon::getBsn, ZgwBetrokkeneIdentificatie::setInpBsn))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZdsNatuurlijkPersoon::getGeslachtsaanduiding, ZgwBetrokkeneIdentificatie::setGeslachtsaanduiding));
    }

    public void addZgwEnkelvoudigInformatieObjectToZdsZaakDocumentDetailTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwEnkelvoudigInformatieObject.class, ZdsEdcLa01.Object.class)
                .includeBase(ZgwEnkelvoudigInformatieObject.class, ZdsZaakDocument.class);
    }

    public void addZdsZaakDocumentToZgwEnkelvoudigInformatieObjectTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZdsEdcLk01.Object.class, ZgwEnkelvoudigInformatieObject.class)
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZdsEdcLk01.Object::getCreatiedatum, ZgwEnkelvoudigInformatieObject::setCreatiedatum))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZdsEdcLk01.Object::getVertrouwelijkAanduiding, ZgwEnkelvoudigInformatieObject::setVertrouwelijkheidaanduiding))
                .addMapping(src -> src.getZdsInhoud().getValue(), ZgwEnkelvoudigInformatieObject::setInhoud)
                .addMapping(src -> src.getZdsInhoud().getBestandsnaam(), ZgwEnkelvoudigInformatieObject::setBestandsnaam);
    }

    private AbstractConverter<String, String> convertStufDateToDateString() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String stufDate) {

                var year = stufDate.substring(0, 4);
                var month = stufDate.substring(4, 6);
                var day = stufDate.substring(6, 8);
                return year + "-" + month + "-" + day;
            }
        };
    }

    private AbstractConverter<String, String> convertStufDateToDateTimeString() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String stufDate) {

                var year = stufDate.substring(0, 4);
                var month = stufDate.substring(4, 6);
                var day = stufDate.substring(6, 8);
                var hours = stufDate.substring(8, 10);
                var minutes = stufDate.substring(10, 12);
                var seconds = stufDate.substring(12, 14);
                var milliseconds = stufDate.substring(14);
                return year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "." + milliseconds + "Z";
            }
        };
    }

    private AbstractConverter<String, String> getZGWArchiefNominatie() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String archiefNominatie) {
                if (archiefNominatie.toUpperCase().equals("J")) {
                    return "vernietigen";
                } else {
                    return "blijvend_bewaren";
                }
            }
        };
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

    private AbstractConverter<String, Boolean> convertStringToBoolean() {
        return new AbstractConverter<>() {
            @Override
            protected Boolean convert(String s) {
                return s.toLowerCase().equals("j");
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

    private AbstractConverter<String, String> convertToUpperCase() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String s) {
                return s.toUpperCase();
            }
        };
    }

    private AbstractConverter<ZgwRol, ZdsRol> convertZgwRolToZdsRol() {
        return new AbstractConverter<>() {
            @Override
            protected ZdsRol convert(ZgwRol zgwRol) {
                ZdsRol zdsRol = new ZdsRol();
                zdsRol.gerelateerde = new ZdsGerelateerde();
                if (zgwRol.getBetrokkeneType().equalsIgnoreCase(NATUURLIJK_PERSOON.getDescription())) {
                    zdsRol.gerelateerde.zdsNatuurlijkPersoon = modelMapper().map(zgwRol.betrokkeneIdentificatie, ZdsNatuurlijkPersoon.class);
                    zdsRol.gerelateerde.zdsNatuurlijkPersoon.entiteittype = "NPS";
                } else if (zgwRol.getBetrokkeneType().equalsIgnoreCase(MEDEWERKER.getDescription())) {
                    zdsRol.gerelateerde.zdsMedewerker = modelMapper().map(zgwRol.betrokkeneIdentificatie, ZdsMedewerker.class);
                    zdsRol.gerelateerde.zdsMedewerker.entiteittype = "MDW";
                } else {
                    throw new RuntimeException("Betrokkene type nog niet geïmplementeerd");
                }
                return zdsRol;
            }
        };
    }
}
