package bg.tuvarna.lab;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.time.*;
import java.util.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.core.*;

@Service
public class Tokens {
    public static final String ISSUER="https://issuer.lab.invalid";
    private final NimbusJwtEncoder encoder;
    private final JwtDecoder decoder;
    private final Clock clock;
    public Tokens(LabMode mode, Clock clock) throws Exception {
        this.clock=clock;
        KeyPairGenerator generator=KeyPairGenerator.getInstance("RSA"); generator.initialize(2048);
        KeyPair pair=generator.generateKeyPair();
        RSAKey key=new RSAKey.Builder((RSAPublicKey)pair.getPublic()).privateKey(pair.getPrivate()).keyID("lab-key").build();
        encoder=new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
        NimbusJwtDecoder verified=NimbusJwtDecoder.withPublicKey((RSAPublicKey)pair.getPublic()).build();
        JwtTimestampValidator time=new JwtTimestampValidator(Duration.ZERO); time.setClock(clock);
        OAuth2TokenValidator<Jwt> required=jwt -> jwt.getAudience().contains("lab-api") && jwt.getExpiresAt()!=null && jwt.getSubject()!=null && !jwt.getSubject().isBlank()
            ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        verified.setJwtValidator(new DelegatingOAuth2TokenValidator<>(time,new JwtIssuerValidator(ISSUER),required));
        decoder=mode.vulnerable(9)? raw -> {
            try {
                // INTENTIONAL LAB BUG: parsing is not signature verification.
                SignedJWT parsed=SignedJWT.parse(raw);
                return new Jwt(raw,null,null,parsed.getHeader().toJSONObject(),parsed.getJWTClaimsSet().getClaims());
            } catch(Exception e) { throw new BadJwtException("Invalid token"); }
        } : verified;
    }
    public JwtDecoder decoder() { return decoder; }
    public String issue(String subject) { return issue(subject,ISSUER,"lab-api",clock.instant().plusSeconds(300),"documents.read"); }
    String issue(String subject,String issuer,String audience,Instant expires,String scope) {
        JwtClaimsSet claims=JwtClaimsSet.builder().issuer(issuer).audience(List.of(audience)).subject(subject)
            .issuedAt(clock.instant().minusSeconds(600)).expiresAt(expires).claim("scope",scope).build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
