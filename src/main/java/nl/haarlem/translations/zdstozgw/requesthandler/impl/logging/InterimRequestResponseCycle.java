package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;
import org.apache.commons.httpclient.HttpStatus;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class InterimRequestResponseCycle {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne()
    @JoinColumn(name = "request_response_cycle_id")
    private RequestResponseCycle requestResponseCycle;

    @Lob
    private String zgwUrl;
    @Lob
    private String zgwMethod;
    @Lob
    private String zgwRequestBody;
    @Lob
    private String zgwResponseBody;
    @Lob
    private String zgwResponseCode;
}