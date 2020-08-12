package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.translation.zgw.client.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

@Component
public class HttpService {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    JWTService jwtService;

    public String downloadFile(String fileURL) throws IOException {
        URL url = new URL(fileURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        httpConn.setRequestProperty("Authorization", "Bearer " + jwtService.getJWT());
        httpConn.setRequestProperty("Accept-Crs", "EPSG:4326");
        httpConn.setRequestProperty("Content-Crs", "EPSG:4326");

        int responseCode = httpConn.getResponseCode();
        String result = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = httpConn.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] byteArray = buffer.toByteArray();
            is.close();

            byte[] encoded = Base64.getEncoder().encode(byteArray);
            result = new String(encoded);
        } else {
            log.error("Error downloading file: " + fileURL);
        }
        httpConn.disconnect();
        return result;
    }
}
