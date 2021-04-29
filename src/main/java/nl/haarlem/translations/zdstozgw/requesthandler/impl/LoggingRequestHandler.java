package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.xml.soap.SOAPConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Configuration;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycleService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class LoggingRequestHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private RequestResponseCycleService sessionService;

	public LoggingRequestHandler(Converter converter, ConfigService configService) {
		super(converter, configService);
		this.sessionService = SpringContext.getBean(RequestResponseCycleService.class);
	}

	@Override
	public void save(RequestResponseCycle session) {
		this.sessionService.save(session);
	}
}