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

        modelMapper.typeMap(ZgwZaak.class, ZakLa01GeefZaakDetails.Antwoord.Zaak.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getStartdatum, ZakLa01GeefZaakDetails.Antwoord.Zaak::setStartdatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getRegistratiedatum, ZakLa01GeefZaakDetails.Antwoord.Zaak::setRegistratiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getPublicatiedatum, ZakLa01GeefZaakDetails.Antwoord.Zaak::setPublicatiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getEinddatumGepland, ZakLa01GeefZaakDetails.Antwoord.Zaak::setEinddatumGepland))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getUiterlijkeEinddatumAfdoening, ZakLa01GeefZaakDetails.Antwoord.Zaak::setUiterlijkeEinddatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getEinddatum, ZakLa01GeefZaakDetails.Antwoord.Zaak::setEinddatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaak::getArchiefactiedatum, ZakLa01GeefZaakDetails.Antwoord.Zaak::setDatumVernietigingDossier))
                .addMappings(mapper -> mapper.using(convertZgwArchiefNomitieToZdsArchiefNominatie()).map(ZgwZaak::getArchiefnominatie, ZakLa01GeefZaakDetails.Antwoord.Zaak::setArchiefnominatie));


        modelMapper.typeMap(ZgwBetrokkeneIdentificatie.class, NatuurlijkPersoon.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwBetrokkeneIdentificatie::getGeboortedatum, NatuurlijkPersoon::setGeboortedatum))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZgwBetrokkeneIdentificatie::getGeslachtsaanduiding, NatuurlijkPersoon::setGeslachtsaanduiding))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZgwBetrokkeneIdentificatie::getInpBsn, NatuurlijkPersoon::setBsn));

        modelMapper.typeMap(ZgwStatus.class, ZakLa01GeefZaakDetails.Antwoord.Zaak.Status.class)
                .addMappings(mapper -> mapper.map(ZgwStatus::getStatustoelichting, ZakLa01GeefZaakDetails.Antwoord.Zaak.Status::setToelichting));

        modelMapper.typeMap(ZgwZaakInformatieObject.class, ZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwZaakInformatieObject::getRegistratiedatum, ZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant::setRegistratiedatum));

        modelMapper.typeMap(Heeft.class, ZgwStatus.class)
                .addMappings(mapper -> mapper.using(convertStufDateToDateTimeString()).map(Heeft::getDatumStatusGezet, ZgwStatus::setDatumStatusGezet));

        addZgwEnkelvoudigInformatieObjectToZaakDocumentTypeMapping(modelMapper);
        addZdsZaakToZgwZaakTypeMapping(modelMapper);
        addZdsNatuurlijkPersoonToZgwBetrokkeneIdentificatieTypeMapping(modelMapper);

        modelMapper.addConverter(convertZgwRolToZdsRol());

        return modelMapper;
    }

    private void addZgwEnkelvoudigInformatieObjectToZaakDocumentTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwEnkelvoudigInformatieObject.class, ZaakDocument.class)
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwEnkelvoudigInformatieObject::getCreatiedatum, ZaakDocument::setCreatiedatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwEnkelvoudigInformatieObject::getOntvangstdatum, ZaakDocument::setOntvangstdatum))
                .addMappings(mapper -> mapper.using(convertDateStringToStufDate()).map(ZgwEnkelvoudigInformatieObject::getVerzenddatum, ZaakDocument::setVezenddatum))
                .addMappings(mapper -> mapper.using(convertToUpperCase()).map(ZgwEnkelvoudigInformatieObject::getVertrouwelijkheidaanduiding, ZaakDocument::setVertrouwelijkheidAanduiding))
                .addMappings(mapper -> mapper.map(ZgwEnkelvoudigInformatieObject::getUrl, ZaakDocument::setLink));
    }

    public void addZdsZaakToZgwZaakTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZakLk01.Object.class, ZgwZaak.class)
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZakLk01.Object::getRegistratiedatum, ZgwZaak::setRegistratiedatum))
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZakLk01.Object::getStartdatum, ZgwZaak::setStartdatum))
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(ZakLk01.Object::getEinddatumGepland, ZgwZaak::setEinddatumGepland))
                .addMappings(mapper -> mapper.using(getZGWArchiefNominatie()).map(ZakLk01.Object::getArchiefnominatie, ZgwZaak::setArchiefnominatie));
    }

    public void addZdsNatuurlijkPersoonToZgwBetrokkeneIdentificatieTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(NatuurlijkPersoon.class, ZgwBetrokkeneIdentificatie.class)
                .addMappings(mapper -> mapper.using(convertStufDateToDateString()).map(NatuurlijkPersoon::getGeboortedatum, ZgwBetrokkeneIdentificatie::setGeboortedatum))
                .addMappings(mapper -> mapper.map(NatuurlijkPersoon::getBsn, ZgwBetrokkeneIdentificatie::setInpBsn))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(NatuurlijkPersoon::getGeslachtsaanduiding, ZgwBetrokkeneIdentificatie::setGeslachtsaanduiding));
    }

    private  AbstractConverter<String, String> convertStufDateToDateString() {
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

    private  AbstractConverter<String, String> convertStufDateToDateTimeString() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String stufDate) {

                var year = stufDate.substring(0, 4);
                var month = stufDate.substring(4, 6);
                var day = stufDate.substring(6, 8);
                var hours = stufDate.substring(8, 10);
                var minutes = stufDate.substring(10, 12);
                var seconds =  stufDate.substring(12, 14);
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

    private AbstractConverter<ZgwRol, Rol> convertZgwRolToZdsRol() {
        return new AbstractConverter<>() {
            @Override
            protected Rol convert(ZgwRol zgwRol) {
                Rol rol = new Rol();
                rol.gerelateerde = new Gerelateerde();
                if(zgwRol.getBetrokkeneType().equalsIgnoreCase(NATUURLIJK_PERSOON.getDescription())){
                    rol.gerelateerde.natuurlijkPersoon = modelMapper().map(zgwRol.betrokkeneIdentificatie, NatuurlijkPersoon.class);
                    rol.gerelateerde.natuurlijkPersoon.entiteittype = "NPS";
                }else if(zgwRol.getBetrokkeneType().equalsIgnoreCase(MEDEWERKER.getDescription())){
                    rol.gerelateerde.medewerker = modelMapper().map(zgwRol.betrokkeneIdentificatie, Medewerker.class);
                    rol.gerelateerde.medewerker.entiteittype = "MDW";
                }else{
                    throw new RuntimeException("Betrokkene type nog niet geïmplementeerd");
                }
                return rol;
            }
        };
    }



}
