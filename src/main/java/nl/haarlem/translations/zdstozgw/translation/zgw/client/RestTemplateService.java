package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import java.lang.invoke.MethodHandles;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Service
@Data
public class RestTemplateService {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private JWTService jwtService;

	RestTemplateBuilder restTemplateBuilder;

	private RestTemplate restTemplate;

	@Autowired
	public RestTemplateService(
			@Value("${nl.haarlem.translations.zdstozgw.trustAllCerts:false}") boolean trustAllCerts,
			@Value("${nl.haarlem.translations.zdstozgw.connectionRequestTimeout:30000}") int connectionRequestTimeout,
			@Value("${nl.haarlem.translations.zdstozgw.connectTimeout:30000}") int connectTimeout,
			@Value("${nl.haarlem.translations.zdstozgw.readTimeout:600000}") int readTimeout,
			@Value("${nl.haarlem.translations.zdstozgw.maxConnPerRoute:20}") int maxConnPerRoute,
			@Value("${nl.haarlem.translations.zdstozgw.maxConnTotal:100}") int maxConnTotal,
			RestTemplateBuilder restTemplateBuilder) {
	    if(trustAllCerts){
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        }
		this.restTemplate = restTemplateBuilder.build();
		this.restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(getAllCertsTrustingRequestFactory(
				connectionRequestTimeout, connectTimeout, readTimeout, maxConnPerRoute, maxConnTotal)));
	}

	private HttpComponentsClientHttpRequestFactory getAllCertsTrustingRequestFactory(int connectionRequestTimeout,
			int connectTimeout, int readTimeout, int maxConnPerRoute, int maxConnTotal) {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = null;
		try {
			sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
					.build();
		} catch (Exception ex) {
		}

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf)
				.setMaxConnPerRoute(maxConnPerRoute).setMaxConnTotal(maxConnTotal).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
		requestFactory.setConnectTimeout(connectTimeout);
		requestFactory.setReadTimeout(readTimeout);

		requestFactory.setHttpClient(httpClient);
		return requestFactory;
	}

	public HttpHeaders getHeaders() {
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept-Crs", "EPSG:4326");
		headers.set("Content-Crs", "EPSG:4326");
		headers.set("Authorization", "Bearer " + this.jwtService.getJWT());
		log.debug("headers:" + headers);

		return headers;
	}
}
