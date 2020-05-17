package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01.ZdsZaak;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.apache.commons.io.IOUtils;
import static org.junit.Assume.*;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class XmlUnitTests {

	@Test
	public void contextLoads() {}

	@Test
	public void getStUFObject_whenParsingActualiseerZaakstatus_convertsRequiredNodes(){
		try {
			//assign
			String content = IOUtils.toString(getClass()
					.getClassLoader()
					.getResourceAsStream("zds1.1/ActualiseerZaakstatus"), "UTF-8");

			//act
			ZakLk01 zakLk01_v2 = (ZakLk01) XmlUtils.getStUFObject(content, ZakLk01.class);
			ZdsZaak  object = zakLk01_v2.object.get(1);

			//assert
			assumeTrue(object.identificatie != null);
			assumeTrue(object.heeft.datumStatusGezet != null);
			assumeTrue(object.heeft.gerelateerde.volgnummer != null);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
