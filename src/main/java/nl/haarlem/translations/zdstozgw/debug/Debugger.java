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

	public void startpoint(String name) {
		startpoint(name, null);
	}

	public String startpoint(String name, String message) {
		return (String)testTool.startpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public Object endpoint(String name, Object message) {
		return testTool.endpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public Object endpoint(String name, ExternalConnectionCode externalConnectionCode) {
		return testTool.endpoint(getReferentienummer(), sourceClassName, name, externalConnectionCode);
	}

	public <E extends Exception> Object endpoint(String name,
			ExternalConnectionCodeThrowsException externalConnectionCodeThrowsException, E throwsException) throws E {
		return testTool.endpoint(getReferentienummer(), sourceClassName, name, externalConnectionCodeThrowsException);
	}

	public String inputpoint(String name, String message) {
		return (String)testTool.inputpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public Object outputpoint(String name, Object message) {
		return testTool.outputpoint(getReferentienummer(), sourceClassName, name, message);
	}

	public <E extends Exception> Object outputpoint(String name,
			ExternalConnectionCodeThrowsException externalConnectionCodeThrowsException, E throwsException) throws E {
		return testTool.outputpoint(getReferentienummer(), sourceClassName, name,
				externalConnectionCodeThrowsException, throwsException);
	}

	public Object infopoint(String name, Object message) {
		return testTool.infopoint(getReferentienummer(), sourceClassName, name, message);
	}

	public Object abortpoint(String name, Object message) {
		return testTool.abortpoint(getReferentienummer(), sourceClassName, name, message);
	}

	private static String getReferentienummer() {
		return (String)RequestContextHolder.getRequestAttributes()
				.getAttribute("referentienummer", RequestAttributes.SCOPE_REQUEST);
	}

}
