package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import lombok.Data;

@Data
@Entity
@Table(indexes = @Index(columnList = "referentienummer"))
public class ZgwRequestResponseCycle {
	@Id
	@GeneratedValue
	private long id;
	private String referentienummer;

	private LocalDateTime startdatetime;
	private LocalDateTime stopdatetime;
	private Long durationInMilliseconds;	
	
	private String zgwMethod;
	@Column(columnDefinition="TEXT")
	private String zgwUrl;
	@Column(columnDefinition="TEXT")
	private String zgwRequestBody;
	private Integer zgwRequestSize;

	
	private int zgwResponseCode;	
	@Column(columnDefinition="TEXT")
	private String zgwResponseBody;	
	private Integer zgwResponseSize;

	public ZgwRequestResponseCycle(String referentienummer, HttpRequest request, byte[] body) throws UnsupportedEncodingException {
		this.referentienummer = referentienummer;

		this.zgwMethod = request.getMethodValue();
		this.zgwUrl = request.getURI().toString();
		this.zgwRequestBody = new String(body, "UTF-8");

		startdatetime = LocalDateTime.now();
		this.zgwRequestSize = this.zgwRequestBody.length();
	}

	public long getDurationInMilliseconds() {
		var milliseconds = Duration.between(startdatetime, LocalDateTime.now()).toMillis();
		return milliseconds;
	}

	public void setResonse(ClientHttpResponse response) throws UnsupportedEncodingException, IOException {
		StringBuilder inputStringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
		String line = bufferedReader.readLine();
		while (line != null) {
			line = line.replaceAll("\u0000", "");
			inputStringBuilder.append(line);
			inputStringBuilder.append('\n');
			line = bufferedReader.readLine();
		}
		
		this.zgwResponseBody = inputStringBuilder.toString();
		this.zgwResponseSize = this.zgwResponseBody.length();
		this.zgwResponseCode = response.getStatusCode().value();

		this.stopdatetime = LocalDateTime.now();
		this.durationInMilliseconds = Duration.between(startdatetime, stopdatetime).toMillis();	
	}
}