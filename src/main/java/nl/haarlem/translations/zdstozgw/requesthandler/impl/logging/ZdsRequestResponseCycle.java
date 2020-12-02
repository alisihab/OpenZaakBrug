package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import javax.persistence.Column;
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
	@Column(columnDefinition="TEXT")
	private String zdsRequestBody;
	private int zdsResponseCode;
	@Column(columnDefinition="TEXT")
	private String zdsResponseBody;
}