package nl.haarlem.translations.zdstozgw;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

@SpringBootTest
public class XmlUnitTests {

    @Test
    public void contextLoads() {
    }

    @Ignore
    @Test
    public void getStUFObject_whenParsingActualiseerZaakstatus_convertsRequiredNodes() {
        try {
            //assign
            String content = IOUtils.toString(getClass()
                    .getClassLoader()
                    .getResourceAsStream("zds1.1/ActualiseerZaakstatus"), "UTF-8");

            //actheb
            ZdsZakLk01ActualiseerZaakstatus zdsZakLk01ActualiseerZaakstatus = (ZdsZakLk01ActualiseerZaakstatus) XmlUtils.getStUFObject(content, ZdsZakLk01ActualiseerZaakstatus.class);
            var object = zdsZakLk01ActualiseerZaakstatus.objects.get(1);

            //assert
            assumeTrue(object.identificatie != null);
            assumeTrue(object.heeft.get(0).datumStatusGezet != null);
            assumeTrue(object.heeft.get(0).gerelateerde.volgnummer != null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
