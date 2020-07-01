package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private RequestResponseCycleService requestResponseCycleService;
    private RequestResponseCycle currentRequestResponseCycle;
    private InterimRequestResponseCycle currentInterimRequestResponseCycle;


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        requestResponseCycleService = SpringContext.getBean(RequestResponseCycleService.class);
        this.currentRequestResponseCycle = this.requestResponseCycleService.getRequestResponseCycleSession();
        addRequestToDatabase(request,body);
        ClientHttpResponse response = execution.execute(request, body);
        addResponseToDatabase(response);
        return response;
    }

    private void addRequestToDatabase(HttpRequest request, byte[] body) throws UnsupportedEncodingException {
        this.currentInterimRequestResponseCycle = new InterimRequestResponseCycle()
                .setRequestResponseCycle(this.currentRequestResponseCycle)
                .setZgwRequestBody(new String(body, "UTF-8"))
                .setZgwUrl(request.getURI().toString())
                .setZgwMethod(request.getMethodValue());

        this.requestResponseCycleService.add(this.currentInterimRequestResponseCycle);
    }

    private void addResponseToDatabase(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }

        this.requestResponseCycleService.add(this.currentInterimRequestResponseCycle
                .setZgwResponseBody(inputStringBuilder.toString())
                .setZgwResponseCode(response.getStatusCode().toString()));

    }

}
