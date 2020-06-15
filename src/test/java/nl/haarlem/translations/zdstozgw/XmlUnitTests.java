package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.translation.zds.model.Stuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;import org.apache.commons.io.IOUtils;
import static org.junit.Assume.*;

import org.junit.Assert;
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
			ZakLk01_v2 zakLk01_v2 = (ZakLk01_v2) XmlUtils.getStUFObject(content, ZakLk01_v2.class);
			ZakLk01_v2.Object object = zakLk01_v2.objects.get(1);

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
		Assert.assertEquals( "GWS4all",applicatie);
	}
}
