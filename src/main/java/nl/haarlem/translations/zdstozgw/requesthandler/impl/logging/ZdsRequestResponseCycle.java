package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;

@Entity
@Data
public class ZdsRequestResponseCycle {
	@Id
	@GeneratedValue
	private long id;
	private String referentienummer;

	private String zdsMethod;
	private String zdsUrl;
	private String zdsSoapAction;
	@Lob
	private String zdsRequestBody;
	private int zdsResponseCode;
	@Lob
	private String zdsResponseBody;
}