package nl.haarlem.translations.zdstozgw.debug;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jaco de Groot
 */
@Configuration
public class DebugConfig {

	@Bean
	public ServletRegistrationBean<DebugServlet> servletRegistrationBean(){
		return new ServletRegistrationBean<DebugServlet>(new DebugServlet(), "/debug/*");
	}

}
