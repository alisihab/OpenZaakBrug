package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

@Entity
@Data
public class RequestResponseCycle {
    @Id
    @GeneratedValue
    private long id;

    private String converterImplementation;
    private String converterTemplate;

    private LocalDateTime timestamp;
    private long durationInMilliseconds;
    private String clientUrl;
    private String clientSoapAction;
    @Lob
    private String clientRequestBody;
    @Lob
    private String clientResponseBody;
    private int clientResponseCode;

    // Wanneer we ergens in het proces een fout hebben, dan willen we die bewaren
    @Lob
    private String stackTrace;
}