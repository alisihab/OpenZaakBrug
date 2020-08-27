package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "edcLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsEdcLk01 extends ZdsZknDocument {

    @XmlElement(namespace = ZKN, name = "object")
    //public List<ZdsEdcLk01.Object> objects;
    public List<ZdsZaakDocument> objects;
}
