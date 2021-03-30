package nl.haarlem.translations.zdstozgw;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nl.nn.testtool.Checkpoint;
import nl.nn.testtool.Report;
import nl.nn.testtool.TestTool;
import nl.nn.testtool.run.ReportRunner;
import nl.nn.testtool.run.RunResult;
import nl.nn.testtool.storage.CrudStorage;
import nl.nn.testtool.storage.Storage;
import nl.nn.testtool.storage.StorageException;
import nl.nn.testtool.transform.ReportXmlTransformer;

/**
 * Call Ladybug to run the reports present in the test storage (see Test tab in Ladybug) as JUnit test
 * 
 * @author Jaco de Groot
 */
@SpringBootTest
public class LadybugTests {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	@Autowired
	private TestTool testTool;
	@Autowired
	private CrudStorage testStorage;
	@Autowired
	private Storage debugStorage; 
	@Autowired
	private ReportXmlTransformer reportXmlTransformer;

	@Test
	public void runTestReports() {
		List<Report> reports = new ArrayList<Report>();
		try {
			List<Integer> storageIds = testStorage.getStorageIds();
			for (Integer storageId : storageIds) {
				Report report = testStorage.getReport(storageId);
				reports.add(report);
			}
		} catch (StorageException e) {
			fail(e.getMessage());
		}
		assertTrue("No reports found", reports.size() > 0);
		Collections.sort(reports, new ReportNameComparator());
		ReportRunner reportRunner = new ReportRunner();
		reportRunner.setTestTool(testTool);
		reportRunner.setDebugStorage(debugStorage);
		reportRunner.run(reports, false, true);
		for (Report report : reports) {
			RunResult runResult = reportRunner.getResults().get(report.getStorageId());
			if (runResult.errorMessage != null) {
				fail(runResult.errorMessage);
			} else {
				Report runResultReport = null;
				try {
					runResultReport = reportRunner.getRunResultReport(runResult.correlationId);
				} catch (StorageException e) {
					fail(e.getMessage());
				}
				if (runResultReport == null) {
					fail("Result report not found. Report generator not enabled?");
				} else {
					report.setGlobalReportXmlTransformer(reportXmlTransformer);
					runResultReport.setGlobalReportXmlTransformer(reportXmlTransformer);
					runResultReport.setTransformation(report.getTransformation());
					runResultReport.setReportXmlTransformer(report.getReportXmlTransformer());
					if (log.isInfoEnabled()) {
						int stubbed = 0;
						boolean first = true;
						for (Checkpoint checkpoint : runResultReport.getCheckpoints()) {
							if (first) {
								first = false;
							} else if (checkpoint.getMessageHasBeenStubbed()) {
								stubbed++;
							}
						}
						int total = runResultReport.getCheckpoints().size() - 1;
						String stubInfo = " (" + stubbed + "/" + total + " stubbed)";
						log.info("Assert " + report.getName() + " (" + (report.getEndTime() - report.getStartTime()) + " >> "
								+ (runResultReport.getEndTime() - runResultReport.getStartTime()) + " ms)" + stubInfo);
					}
					assertEquals(report.toXml(reportRunner), runResultReport.toXml(reportRunner));
				}
			}
		}
	}
}

class ReportNameComparator implements Comparator<Report> {

	@Override
	public int compare(Report o1, Report o2) {
		return o1.getFullPath().compareTo(o2.getFullPath());
	}
}