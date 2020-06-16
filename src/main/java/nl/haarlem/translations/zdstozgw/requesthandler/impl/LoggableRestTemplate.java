package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

public class LoggableRestTemplate implements RestTemplateCustomizer {
    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
    }
}

