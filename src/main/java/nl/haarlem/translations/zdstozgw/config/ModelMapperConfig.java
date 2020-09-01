package nl.haarlem.translations.zdstozgw.config;

import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static nl.haarlem.translations.zdstozgw.translation.BetrokkeneType.MEDEWERKER;
import static nl.haarlem.translations.zdstozgw.translation.BetrokkeneType.NATUURLIJK_PERSOON;

@Configuration
public class ModelMapperConfig {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration() // Fetch the configuration
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull());

        modelMapper.typeMap(ZgwStatus.class, ZdsHeeft.class)
                .addMappings(mapper -> mapper.map(ZgwStatus::getStatustoelichting, ZdsHeeft::setToelichting))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwStatus::getDatumStatusGezet, ZdsHeeft::setDatumStatusGezet));
        
        modelMapper.typeMap(ZgwZaakInformatieObject.class, ZdsZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant.class)
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakInformatieObject::getRegistratiedatum, ZdsZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant::setRegistratiedatum));

        modelMapper.typeMap(ZdsHeeft.class, ZgwStatus.class)
                .addMappings(mapper -> mapper.using(convertStufDateTimeToZgwDateTime()).map(ZdsHeeft::getDatumStatusGezet, ZgwStatus::setDatumStatusGezet));
        
        modelMapper.typeMap(ZdsZaak.Opschorting.class, ZgwOpschorting.class)
                .addMappings(mapper -> mapper.using(convertStringToBoolean()).map(ZdsZaak.Opschorting::getIndicatie, ZgwOpschorting::setIndicatie));

        addZdsZaakToZgwZaakTypeMapping(modelMapper);
        addZgwZaakToZdsZaakTypeMapping(modelMapper);
        
        addZdsZaakToZgwZaakPutTypeMapping(modelMapper);
        addZgwZaakPutToZdsZaakTypeMapping(modelMapper);        
        
        addZgwBetrokkeneIdentificatieToNatuurlijkPersoonTypeMapping(modelMapper);
        addZgwEnkelvoudigInformatieObjectToZaakDocumentTypeMapping(modelMapper);
        addZgwEnkelvoudigInformatieObjectToZdsZaakDocumentDetailTypeMapping(modelMapper);        
        addZdsNatuurlijkPersoonToZgwBetrokkeneIdentificatieTypeMapping(modelMapper);
        addZdsZaakDocumentToZgwEnkelvoudigInformatieObjectTypeMapping(modelMapper);
        addZgwZaakToGeefZaakDetailsTypeMappingTypeMapping(modelMapper);

        modelMapper.addConverter(convertZgwRolToZdsRol());

        return modelMapper;
    }

    private void addZgwBetrokkeneIdentificatieToNatuurlijkPersoonTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwBetrokkeneIdentificatie.class, ZdsNatuurlijkPersoon.class)
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwBetrokkeneIdentificatie::getGeboortedatum, ZdsNatuurlijkPersoon::setGeboortedatum))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZgwBetrokkeneIdentificatie::getGeslachtsaanduiding, ZdsNatuurlijkPersoon::setGeslachtsaanduiding))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZgwBetrokkeneIdentificatie::getInpBsn, ZdsNatuurlijkPersoon::setBsn));
    }
    
    private void addZgwZaakToZdsZaakTypeMapping(ModelMapper modelMapper) { 	
        modelMapper.typeMap(ZgwZaak.class, ZdsZaak.class)
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getStartdatum, ZdsZaak::setStartdatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getRegistratiedatum, ZdsZaak::setRegistratiedatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getPublicatiedatum, ZdsZaak::setPublicatiedatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getEinddatumGepland, ZdsZaak::setEinddatumGepland))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getUiterlijkeEinddatumAfdoening, ZdsZaak::setUiterlijkeEinddatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaak::getEinddatum, ZdsZaak::setEinddatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getArchiefactiedatum, ZdsZaak::setDatumVernietigingDossier))
                .addMappings(mapper -> mapper.using(convertZgwArchiefNomitieToZdsArchiefNominatie()).map(ZgwZaakPut::getArchiefnominatie, ZdsZaak::setArchiefnominatie));
    }

    private void addZgwZaakPutToZdsZaakTypeMapping(ModelMapper modelMapper) { 	
        modelMapper.typeMap(ZgwZaakPut.class, ZdsZaak.class)
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getStartdatum, ZdsZaak::setStartdatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getRegistratiedatum, ZdsZaak::setRegistratiedatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getPublicatiedatum, ZdsZaak::setPublicatiedatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getEinddatumGepland, ZdsZaak::setEinddatumGepland))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getUiterlijkeEinddatumAfdoening, ZdsZaak::setUiterlijkeEinddatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwZaakPut::getArchiefactiedatum, ZdsZaak::setDatumVernietigingDossier))
                .addMappings(mapper -> mapper.using(convertZgwArchiefNomitieToZdsArchiefNominatie()).map(ZgwZaakPut::getArchiefnominatie, ZdsZaak::setArchiefnominatie));
    }
    
    private void addZgwZaakToGeefZaakDetailsTypeMappingTypeMapping(ModelMapper modelMapper) {
        //modelMapper.typeMap(ZgwZaak.class, ZdsZakLa01GeefZaakDetails.Antwoord.Object.class)
    	//	.includeBase(ZgwZaak.class, ZdsZaak.class);
        modelMapper.typeMap(ZgwZaak.class, ZdsZaak.class);    	    	
    }

    private void addZgwEnkelvoudigInformatieObjectToZaakDocumentTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwEnkelvoudigInformatieObject.class, ZdsZaakDocument.class)
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwEnkelvoudigInformatieObject::getCreatiedatum, ZdsZaakDocument::setCreatiedatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwEnkelvoudigInformatieObject::getOntvangstdatum, ZdsZaakDocument::setOntvangstdatum))
                .addMappings(mapper -> mapper.using(convertZgwDateToStufDate()).map(ZgwEnkelvoudigInformatieObject::getVerzenddatum, ZdsZaakDocument::setVezenddatum))
                .addMappings(mapper -> mapper.using(convertToUpperCase()).map(ZgwEnkelvoudigInformatieObject::getVertrouwelijkheidaanduiding, ZdsZaakDocument::setVertrouwelijkheidAanduiding))
                .addMappings(mapper -> mapper.map(ZgwEnkelvoudigInformatieObject::getUrl, ZdsZaakDocument::setLink));
    }

    public void addZdsZaakToZgwZaakTypeMapping(ModelMapper modelMapper) {
    	modelMapper.typeMap(ZdsZaak.class, ZgwZaak.class)
			.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getStartdatum, ZgwZaakPut::setStartdatum))    	
    		.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getRegistratiedatum, ZgwZaakPut::setRegistratiedatum))            
    		.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getPublicatiedatum, ZgwZaakPut::setPublicatiedatum))
    		.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getEinddatumGepland, ZgwZaakPut::setEinddatumGepland))    		
            .addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getUiterlijkeEinddatum, ZgwZaak::setUiterlijkeEinddatumAfdoening))
            .addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getEinddatum, ZgwZaak::setEinddatum))
            .addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getDatumVernietigingDossier, ZgwZaak::setArchiefactiedatum))
    		.addMappings(mapper -> mapper.using(getZGWArchiefNominatie()).map(ZdsZaak::getArchiefnominatie, ZgwZaakPut::setArchiefnominatie));
    }

    public void addZdsZaakToZgwZaakPutTypeMapping(ModelMapper modelMapper) {
    	modelMapper.typeMap(ZdsZaak.class, ZgwZaakPut.class)
			.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getStartdatum, ZgwZaakPut::setStartdatum))    	
    		.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getRegistratiedatum, ZgwZaakPut::setRegistratiedatum))            
    		.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getPublicatiedatum, ZgwZaakPut::setPublicatiedatum))
    		.addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaak::getEinddatumGepland, ZgwZaakPut::setEinddatumGepland))    		
    		.addMappings(mapper -> mapper.using(getZGWArchiefNominatie()).map(ZdsZaak::getArchiefnominatie, ZgwZaakPut::setArchiefnominatie));
    }
    
    public void addZdsNatuurlijkPersoonToZgwBetrokkeneIdentificatieTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZdsNatuurlijkPersoon.class, ZgwBetrokkeneIdentificatie.class)
                .addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsNatuurlijkPersoon::getGeboortedatum, ZgwBetrokkeneIdentificatie::setGeboortedatum))
                .addMappings(mapper -> mapper.map(ZdsNatuurlijkPersoon::getBsn, ZgwBetrokkeneIdentificatie::setInpBsn))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZdsNatuurlijkPersoon::getGeslachtsaanduiding, ZgwBetrokkeneIdentificatie::setGeslachtsaanduiding));
    }

    public void addZgwEnkelvoudigInformatieObjectToZdsZaakDocumentDetailTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(ZgwEnkelvoudigInformatieObject.class, ZdsZaakDocument.class)
                .includeBase(ZgwEnkelvoudigInformatieObject.class, ZdsZaakDocument.class);
    }

    public void addZdsZaakDocumentToZgwEnkelvoudigInformatieObjectTypeMapping(ModelMapper modelMapper) {
    	modelMapper.typeMap(ZdsZaakDocument.class, ZgwEnkelvoudigInformatieObject.class)
                .addMappings(mapper -> mapper.using(convertStufDateToZgwDate()).map(ZdsZaakDocument::getCreatiedatum, ZgwEnkelvoudigInformatieObject::setCreatiedatum))
                .addMappings(mapper -> mapper.using(convertToLowerCase()).map(ZdsZaakDocument::getVertrouwelijkheidAanduiding, ZgwEnkelvoudigInformatieObject::setVertrouwelijkheidaanduiding))
                .addMapping(src -> src.getInhoud().getValue(), ZgwEnkelvoudigInformatieObject::setInhoud)
                .addMapping(src -> src.getInhoud().getBestandsnaam(), ZgwEnkelvoudigInformatieObject::setBestandsnaam);
    }

    private AbstractConverter<String, String> convertStufDateToZgwDate() {
        return new AbstractConverter<>() {
        	
			@Override
            protected String convert(String stufDate) {
				if(stufDate == null) {
					return null;
				}
        		var zdsDateFormatter = new SimpleDateFormat("yyyyMMdd");
        		zdsDateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
        		var zgwDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        		zgwDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        		try {
        			var date = zdsDateFormatter.parse(stufDate);
        			var zgwDate = zgwDateFormatter.format(date); 
        			log.debug("convertStufDateToZgwDate: " + stufDate + " --> " + zgwDate);
        			return zgwDate;

        		} catch (ParseException e) {
        			throw new ConverterException("ongeldige stuf-datetime: '" + stufDate + "'");
        		}				
            }
        };
    }

    private AbstractConverter<String, String> convertStufDateTimeToZgwDateTime() {
        return new AbstractConverter<>() {
        	
            @Override
            protected String convert(String stufDateTime) {
            	if(stufDateTime == null) {
            		return null;
            	}
        		if(stufDateTime.length() == 8) {
        			// 
        			stufDateTime += "000000000";
        		}        		
        		var zdsDateFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        		zdsDateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
        		var zgwDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        		zgwDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        		try {
        			var datetime = zdsDateFormatter.parse(stufDateTime);
        			var zgwDateTime = zgwDateFormatter.format(datetime); 
        			log.debug("convertStufDateTimeToZgwDateTime: " + stufDateTime + " --> " + zgwDateTime);
        			return zgwDateTime;

        		} catch (ParseException e) {
        			throw new ConverterException("ongeldige stuf-datetime: '" + stufDateTime + "'");
        		}
            }
        };
    }

    private AbstractConverter<String, String> convertZgwDateToStufDate() {
        return new AbstractConverter<>() {
        	
            @Override
            protected String convert(String zgwDate) {
                if (zgwDate == null) {
                    return null;
                }
        		var zgwDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        		zgwDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        		var zdsDateFormatter = new SimpleDateFormat("yyyyMMdd");
        		zdsDateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
        		try {
        			var date = zgwDateFormatter.parse(zgwDate);
        			var stufDate = zdsDateFormatter.format(date); 
        			log.debug("convertZgwDateToStufDate: " + zgwDate + " --> " + stufDate);
        			return zgwDate;

        		} catch (ParseException e) {
        			throw new ConverterException("ongeldige stuf-datetime: '" + zgwDate + "'");
        		}				
                
                
            }
        };
    }

    
       private AbstractConverter<String, String> getZGWArchiefNominatie() {
        return new AbstractConverter<>() {
        	
            @Override
            protected String convert(String archiefNominatie) {
                var result = archiefNominatie.toUpperCase().equals("J") ? "vernietigen" : "blijvend_bewaren";
            	log.debug("getZGWArchiefNominatie: " + archiefNominatie + " --> " + result);
                return result;                
            }
        };
    }

    private AbstractConverter<String, Boolean> convertStringToBoolean() {
        return new AbstractConverter<>() {
        	
            @Override
            protected Boolean convert(String s) {
                var result = s.toLowerCase().equals("j");
            	log.debug("convertStringToBoolean: " + s + " --> " + result);
                return result;                   
            }
        };
    }

    private AbstractConverter<String, String> convertZgwArchiefNomitieToZdsArchiefNominatie() {
        return new AbstractConverter<>() {

        	@Override
            protected String convert(String s) {
            	var result = s.toUpperCase().equals("vernietigen") ? "J" :  "N";
            	log.debug("convertZgwArchiefNomitieToZdsArchiefNominatie: " + s + " --> " + result);
                return result;                   
            }
        };
    }

    private AbstractConverter<String, String> convertToLowerCase() {
        return new AbstractConverter<>() {
        	
            @Override
            protected String convert(String s) {
                var result =  s.toLowerCase();
            	log.debug("convertToLowerCase: " + s + " --> " + result);
                return result;                  
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
                    zdsRol.gerelateerde.natuurlijkPersoon = modelMapper().map(zgwRol.betrokkeneIdentificatie, ZdsNatuurlijkPersoon.class);
                    zdsRol.gerelateerde.natuurlijkPersoon.entiteittype = "NPS";
                } else if (zgwRol.getBetrokkeneType().equalsIgnoreCase(MEDEWERKER.getDescription())) {
                    zdsRol.gerelateerde.medewerker = modelMapper().map(zgwRol.betrokkeneIdentificatie, ZdsMedewerker.class);
                    zdsRol.gerelateerde.medewerker.entiteittype = "MDW";
                } else {
                    throw new RuntimeException("Betrokkene type nog niet geïmplementeerd");
                }
            	log.debug("convertToLowerCase: " + zgwRol.roltoelichting + " --> " + zdsRol.toString());                
                return zdsRol;
            }
        };
    }
}
