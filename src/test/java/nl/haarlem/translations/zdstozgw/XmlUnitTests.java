package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.Assume.assumeTrue;

@SpringBootTest
public class XmlUnitTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void getStUFObject_whenParsingActualiseerZaakstatus_convertsRequiredNodes() {
        try {
            //assign
            String content = IOUtils.toString(getClass()
                    .getClassLoader()
                    .getResourceAsStream("zds1.1/ActualiseerZaakstatus"), "UTF-8");

            //actheb
            ZdsZakLk01ActualiseerZaakstatus zdsZakLk01ActualiseerZaakstatus = (ZdsZakLk01ActualiseerZaakstatus) XmlUtils.getStUFObject(content, ZdsZakLk01ActualiseerZaakstatus.class);
            ZdsZakLk01ActualiseerZaakstatus.Object object = zdsZakLk01ActualiseerZaakstatus.objects.get(1);

            //assert
            assumeTrue(object.identificatie != null);
            assumeTrue(object.heeft.datumStatusGezet != null);
            assumeTrue(object.heeft.gerelateerde.volgnummer != null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getApplicicatieFromZender() throws IOException {
        //assign
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("zds1.1/ActualiseerZaakstatus"), "UTF-8");

        //act
        String applicatie = XmlUtils.getApplicicatieFromZender(content);

        //assert
        Assert.assertEquals("GWS4all", applicatie);
    }
}
