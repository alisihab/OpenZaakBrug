package nl.haarlem.translations.zdstozgw.converter;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class ConverterException extends Exception {
	protected Converter converter = null;
		
	public ConverterException(String omschrijving) {
		super(omschrijving);
	}	
	public ConverterException(String omschrijving, Throwable cause) {
		super(omschrijving);
	}
	public ConverterException(Converter converter, String omschrijving, Throwable cause) {
		super(omschrijving, cause);
	}
		
	public HttpStatus getHttpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}	
}