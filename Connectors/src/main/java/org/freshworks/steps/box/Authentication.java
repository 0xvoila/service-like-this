package org.freshworks.steps.box;

import java.io.FileReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.PrivateKey;
import java.security.Security;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

public class Authentication {

    public static String getAccessToken() {
        try {
            // Create a file reader
            FileReader reader = new FileReader("/Users/aaggarwal/Documents/config.json");

            // Use the powerful GSON library (github.com/google/gson)
            // to covert the string into a Config object
            ObjectMapper objectMapper = new ObjectMapper();
            Config config = objectMapper.readValue(reader, Config.class);

            // We use BouncyCastle to handle the decryption
            // (https://www.bouncycastle.org/java.html)
            Security.addProvider(new BouncyCastleProvider());

            // Using BouncyCastle's PEMParser we convert the
            // encrypted private key into a keypair object
            PEMParser pemParser = new PEMParser(new StringReader(config.boxAppSettings.appAuth.privateKey));
            Object keyPair = pemParser.readObject();
            pemParser.close();

            // Finally, we decrypt the key using the passphrase
            char[] passphrase = config.boxAppSettings.appAuth.passphrase.toCharArray();
            JceOpenSSLPKCS8DecryptorProviderBuilder decryptBuilder = new JceOpenSSLPKCS8DecryptorProviderBuilder()
                    .setProvider("BC");
            InputDecryptorProvider decryptProvider = decryptBuilder.build(passphrase);
            PrivateKeyInfo keyInfo = ((PKCS8EncryptedPrivateKeyInfo) keyPair).decryptPrivateKeyInfo(decryptProvider);

            // In the end, we will use this key in the next steps
            PrivateKey key = (new JcaPEMKeyConverter()).getPrivateKey(keyInfo);

            // We will need the authenticationUrl again later,
            // so it is handy to define here
            String authenticationUrl = "https://api.box.com/oauth2/token";

            // Rather than constructing the JWT assertion manually, we are
            // using the org.jose4j.jwt library.
            JwtClaims claims = new JwtClaims();
            claims.setIssuer(config.boxAppSettings.clientID);
            claims.setAudience(authenticationUrl);
            claims.setSubject(config.enterpriseID);
            claims.setClaim("box_sub_type", "enterprise");
            // This is an identifier that helps protect against
            // replay attacks
            claims.setGeneratedJwtId(64);
            // We give the assertion a lifetime of 45 seconds
            // before it expires
            claims.setExpirationTimeMinutesInTheFuture(0.75f);

            // With the claims in place, it's time to sign the assertion
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(key);
            // The API support "RS256", "RS384", and "RS512" encryption
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
            jws.setHeader("typ", "JWT");
            jws.setHeader("kid", config.boxAppSettings.appAuth.publicKeyID);
            String assertion = jws.getCompactSerialization();

            // We are using the excellent org.apache.http package
            // to simplify the API call

            // Create the params for the request
            HashMap<String, String> params = new HashMap<>();
            // This specifies that we are using a JWT assertion
            // to authenticate
            params.put("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
            // Our JWT assertion

            params.put("assertion", assertion);
            // The OAuth 2 client ID and secret
            params.put("client_id", config.boxAppSettings.clientID);
            params.put("client_secret", config.boxAppSettings.clientSecret);

            String paramString = objectMapper.writeValueAsString(params);
            // Make the POST call to the authentication endpoint
//            CloseableHttpClient httpClient = HttpClientBuilder.create().disableCookieManagement().build();
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(authenticationUrl)).setHeader("Content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(paramString)).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Token token = objectMapper.readValue(response.body(), Token.class);
            String accessToken = token.access_token;
            return accessToken;

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}

// Define a class that we can parse
// the json into
@Getter @Setter
@NoArgsConstructor
class Config {

    @Setter @Getter
    @NoArgsConstructor
    static class BoxAppSettings {

        @Setter @Getter
        @NoArgsConstructor
        static class AppAuth {
            String privateKey;
            String passphrase;
            String publicKeyID;
        }

        String clientID;
        String clientSecret;
        AppAuth appAuth;
    }

    BoxAppSettings boxAppSettings;
    String enterpriseID;
}

// Parse the JSON using Gson to a Token object
@Getter @Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Token {
    String access_token;
    long expires_in;
}
