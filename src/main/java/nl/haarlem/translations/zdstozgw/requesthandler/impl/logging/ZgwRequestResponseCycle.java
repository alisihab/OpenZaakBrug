package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;

@Entity
@Data
public class ZgwRequestResponseCycle {
	@Id
	@GeneratedValue
	private long id;
	private String referentienummer;

	private String zgwMethod;
	@Column(columnDefinition="TEXT")
	private String zgwUrl;
	@Column(columnDefinition="TEXT")
	private String zgwRequestBody;
	private int zgwResponseCode;
	@Column(columnDefinition="TEXT")
	private String zgwResponseBody;
}