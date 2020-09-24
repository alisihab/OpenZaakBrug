package nl.haarlem.translations.zdstozgw.converter;

@SuppressWarnings("serial")
public class ConverterException extends RuntimeException {

	public String details;

	public ConverterException(String omschrijving) {
		super(omschrijving);
	}

	public ConverterException(String omschrijving, Throwable cause) {
		super(omschrijving);
		// TODO: voor nu wel handig, later maar weg?
		cause.printStackTrace();
	}

	public ConverterException(String omschrijving, String details, Throwable cause) {
		super(omschrijving);
		this.details = details;
	}
}