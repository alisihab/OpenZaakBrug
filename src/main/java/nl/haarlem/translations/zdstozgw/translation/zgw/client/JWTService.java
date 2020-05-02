package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import static java.time.ZonedDateTime.now;

import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.hmac.HMACSigner;

@Service
public class JWTService {

	@Value("${openzaak.jwt.secret}")
	private String secret;

	@Value("${openzaak.jwt.issuer}")
	private String issuer;

	public String getJWT() {
		Signer signer = HMACSigner.newSHA256Signer(this.secret);

		io.fusionauth.jwt.domain.JWT jwt = new io.fusionauth.jwt.domain.JWT().setIssuer(this.issuer)
				.setIssuedAt(now(ZoneOffset.UTC)).addClaim("client_id", this.issuer).addClaim("user_id", this.issuer)
				.addClaim("user_reresentation", this.issuer).setExpiration(now(ZoneOffset.UTC).plusMinutes(10));

		return io.fusionauth.jwt.domain.JWT.getEncoder().encode(jwt, signer);
	}
}
