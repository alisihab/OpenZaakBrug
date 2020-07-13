package nl.haarlem.translations.zdstozgw.converter;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;

public abstract class Converter {

	@SuppressWarnings("serial")
	public class ConverterException extends RuntimeException {
		protected Converter converter;
		protected String stacktrace;
		protected String requestbody;

		public ConverterException(Converter converter, String omschrijving, String requestBody, Throwable err) {
			super(omschrijving, err);
			this.converter = converter;
			this.requestbody = requestBody;

			// get the stacktrace and store it
			var swriter = new java.io.StringWriter();
			var pwriter = new java.io.PrintWriter(swriter);
			this.printStackTrace(pwriter);
			this.stacktrace = swriter.toString();
		}

		public String getStacktrace() {
			return this.stacktrace;
		}

		public String getFaultString() {
			return getMessage();
		}

		public String getOmschrijving() {
			return this.toString();
		}

		public String getDetails() {
			return this.stacktrace;
		}

		public String getDetailsXml() {
			return this.requestbody;
		}

		public HttpStatus getHttpStatus() {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	protected String template;
	protected String zdsUrl;

	public Converter(String template, String legacyService) {
		this.template = template;
		this.zdsUrl = legacyService;
	}

	public String getImplementation() {
		return this.getClass().getCanonicalName();
	}

	public String getTemplate() {
		return this.template;
	}
	
	public abstract String proxyZds(String zdsSoapAction, RequestResponseCycle session, ApplicationParameterRepository repository, String zdsRequest) throws Exception;	
	public abstract String proxyZdsAndReplicateToZgw(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String body);	
	public abstract String convertToZgwAndReplicateToZds(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String body);	
	public abstract String convertToZgw(RequestResponseCycle session, ZGWClient zaakService, ConfigService configService, ApplicationParameterRepository repository, String requestBody);
}