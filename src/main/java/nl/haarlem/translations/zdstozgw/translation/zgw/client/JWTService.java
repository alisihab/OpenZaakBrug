package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.hmac.HMACSigner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

import static java.time.ZonedDateTime.now;

@Service
public class JWTService {

    @Value("${openzaak.jwt.secret}")
    private String secret;

    @Value("${openzaak.jwt.issuer}")
    private String issuer;

    public String getJWT() {
        Signer signer = HMACSigner.newSHA256Signer(secret);

        io.fusionauth.jwt.domain.JWT jwt = new io.fusionauth.jwt.domain.JWT().setIssuer(issuer)
                .setIssuedAt(now(ZoneOffset.UTC))
                .addClaim("client_id", issuer)
                .addClaim("user_id", issuer)
                .addClaim("user_reresentation", issuer)
                .setExpiration(now(ZoneOffset.UTC).plusMinutes(10));

        return io.fusionauth.jwt.domain.JWT.getEncoder().encode(jwt, signer);
    }
}
