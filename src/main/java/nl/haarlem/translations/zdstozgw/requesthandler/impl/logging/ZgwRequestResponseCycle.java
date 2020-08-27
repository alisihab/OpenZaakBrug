package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ZgwRequestResponseCycle {
    @Id
    @GeneratedValue
    private long id;

    private String zgwMethod;    
    @Lob
    private String zgwUrl;
    @Lob
    private String zgwRequestBody;
    @Lob
    private String zgwResponseCode;
    @Lob
    private String zgwResponseBody;

    private String httpSessionId;
}