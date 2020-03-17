package nl.haarlem.translations.zdstozgw.translation.zds.model;
import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.*;

@XmlRootElement(namespace = ZKN, name="zakLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLk01_v2 {
    @XmlElement(namespace = ZKN, name="object")
    public List<Object> objects;
}
