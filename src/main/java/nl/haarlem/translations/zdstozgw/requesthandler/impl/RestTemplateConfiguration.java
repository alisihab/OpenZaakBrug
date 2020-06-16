package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    @Qualifier("customRestTemplateCustomizer")
    public LoggableRestTemplate customRestTemplateCustomizer() {
        return new LoggableRestTemplate();
    }

//    @Bean
//    @DependsOn(value = {"customRestTemplateCustomizer"})
//    public RestTemplateBuilder restTemplateBuilder() {
//        return new RestTemplateBuilder(customRestTemplateCustomizer());
//    }
}
