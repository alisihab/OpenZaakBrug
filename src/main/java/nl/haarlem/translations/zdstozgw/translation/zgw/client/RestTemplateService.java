package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import lombok.Data;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

@Service
@Data
public class RestTemplateService {

    @Autowired
    private JWTService jwtService;

    RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;

    @Autowired
    public RestTemplateService(@Value("${nl.haarlem.translations.zdstozgw.trustAllCerts:false}") boolean trustAllCerts, RestTemplateBuilder restTemplateBuilder) {
//        if (trustAllCerts) {
//            this.restTemplate = new RestTemplate(this.getAllCertsTrustingRequestFactory());
//        } else {
//            this.restTemplate = new RestTemplate();
//        }
        this.restTemplate = restTemplateBuilder.build();
    }

    private HttpComponentsClientHttpRequestFactory getAllCertsTrustingRequestFactory() {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = null;
        try {
            sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (Exception ex) {
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

    public HttpHeaders getHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Crs", "EPSG:4326");
        headers.set("Content-Crs", "EPSG:4326");
        headers.set("Authorization", "Bearer " + jwtService.getJWT());

        return headers;
    }
}
