package nl.haarlem.translations.zdstozgw;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.jupiter.extension.ParallelLoadExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@LoadWith("load_generation.properties")
@ExtendWith({ParallelLoadExtension.class})
public class LoadTest {

/*

Run this class as JUnit test in you IDE or command line using Maven with:

  mvn test -Dtest=nl.haarlem.translations.zdstozgw.LoadTest

Settings can be changed in load_generation.properties

See result in target/zerocode-junit-granular-report.csv. This csv will also include results from previous runs. Remove
target/zerocode-test-reports/* before running to prevent this.

As Spring Boot is started when the first requests are fired the responseDelayMilliSec of the first requests include the
time needed to start Spring Boot. Hence their responseDelayMilliSec will be much higher than those of other requests.
The csv is not sorted on start time so those higher values appear at "random" places in the csv.

To speed things up replace debugStorage in spring-ladybug.xml with:

	<bean name="debugStorage" class="nl.nn.testtool.storage.memory.Storage" autowire="byName">
		<property name="name" value="Logging"/>
	</bean>

*/

    @Test
    @DisplayName("Testing parallel load for ReportRunner running all Ladybug test reports")
    @TestMapping(testClass = LadybugTests.class, testMethod = "runAllTestReports")
    public void creeerZaak() {
    }

    @Test
    @DisplayName("Testing parallel load for serveral ReportRunners running one specific Ladybug test report")
    @TestMappings({
            @TestMapping(testClass = LadybugTests.class, testMethod = "runGenereerZaakIdentificatieTestReport"),
            @TestMapping(testClass = LadybugTests.class, testMethod = "runCreeerZaakTestReport"),
            @TestMapping(testClass = LadybugTests.class, testMethod = "runVoegZaakdocumentToeTestReport"),
            @TestMapping(testClass = LadybugTests.class, testMethod = "runGeefZaakdetailsTestReport"),
    })
    public void genereerZaakIdentificatie() {
    }

}
