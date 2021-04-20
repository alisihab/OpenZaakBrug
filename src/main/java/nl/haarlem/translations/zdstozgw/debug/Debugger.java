package nl.haarlem.translations.zdstozgw.debug;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.nn.testtool.ExternalConnectionCode;
import nl.nn.testtool.ExternalConnectionCodeThrowsException;
import nl.nn.testtool.TestTool;

/**
 * @author Jaco de Groot
 */
public class Debugger {
	private static Map<Class<?>, Debugger> debuggers = new HashMap<Class<?>, Debugger>();
	private TestTool testTool;
	private String sourceClassName;

	private Debugger(TestTool testTool, String sourceClassName) {
		this.testTool = testTool;
		this.sourceClassName = sourceClassName;
	}

	public static synchronized Debugger getDebugger(Class<?> clazz) {
		Debugger debugger = debuggers.get(clazz);
		if (debugger == null) {
			debugger = new Debugger(SpringContext.getBean(TestTool.class), clazz.getCanonicalName());
			debuggers.put(clazz, debugger);
		}
		return debugger;
	}
	
	public Boolean isReportGeneratorEnabled() {
		return testTool.isReportGeneratorEnabled();
	}

	public void startpoint(String name) {
		startpoint(name, null);
	}

	public <T> T startpoint(String name, T message) {
		return testTool.startpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public <T> T endpoint(String name, T message) {
		return testTool.endpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public <T> T endpoint(String name, ExternalConnectionCode externalConnectionCode) {
		return testTool.endpoint(getReferentienummer(), sourceClassName, name, externalConnectionCode);
	}

	public <T, E extends Exception> T endpoint(String name,
			ExternalConnectionCodeThrowsException externalConnectionCodeThrowsException, E throwsException) throws E {
		return testTool.endpoint(getReferentienummer(), sourceClassName, name, externalConnectionCodeThrowsException,
				throwsException);
	}

	public <T> T inputpoint(String name, T message) {
		return testTool.inputpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public <T> T outputpoint(String name, T message) {
		return testTool.outputpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public <T, E extends Exception> T outputpoint(String name,
			ExternalConnectionCodeThrowsException externalConnectionCodeThrowsException, E throwsException) throws E {
		return testTool.outputpoint(getReferentienummer(), sourceClassName, name,
				externalConnectionCodeThrowsException, throwsException);
	}

	public <T> T infopoint(String name, T message) {
		return testTool.infopoint(getReferentienummer(), sourceClassName, name, message);
	}

	public <T> T abortpoint(String name, T message) {
		return testTool.abortpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public void close() {
		testTool.close(getReferentienummer());
	}

	private static String getReferentienummer() {
		return (String)RequestContextHolder.getRequestAttributes()
				.getAttribute("referentienummer", RequestAttributes.SCOPE_REQUEST);
	}

}
