package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import java.lang.invoke.MethodHandles;

import javax.xml.soap.SOAPConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class BasicRequestHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	public BasicRequestHandler(Converter converter, ConfigService configService) {
		super(converter, configService);
	}

	@Override
	public ResponseEntity<?> execute() {
		log.info("Executing request with handler: " + this.getClass().getCanonicalName() + " and converter: "
				+ this.converter.getClass().getCanonicalName());

		this.converter.load();
		try {
			var zdsResponse = this.converter.execute();
			var response = XmlUtils.getSOAPMessageFromObject(zdsResponse);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception ex) {
			log.warn("Executing request with handler: " + this.getClass().getCanonicalName() + " and converter: "
					+ this.converter.getClass().getCanonicalName(), ex);
			var fo03 = getErrorZdsDocument(ex, this.converter);
			var response = XmlUtils.getSOAPFaultMessageFromObject(SOAPConstants.SOAP_RECEIVER_FAULT, ex.toString(),
					fo03);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}