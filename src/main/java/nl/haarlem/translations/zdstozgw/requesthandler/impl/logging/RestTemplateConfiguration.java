package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestTemplateConfiguration {
	
	@Bean
    public LoggingRestTemplate customRestTemplateCustomizer() {
        return new LoggingRestTemplate();
    }
}
