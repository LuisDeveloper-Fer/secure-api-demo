package dev.portfolio;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.*;
import java.net.*;
import java.net.http.*;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT) class SecurityTest {
 static RSAKey key;static HttpServer jwks;
 static{try{key=new RSAKeyGenerator(2048).keyID("test").generate();jwks=HttpServer.create(new InetSocketAddress(0),0);jwks.createContext("/jwks",e->{byte[] bytes=new JWKSet(key.toPublicJWK()).toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);e.getResponseHeaders().set("Content-Type","application/json");e.sendResponseHeaders(200,bytes.length);e.getResponseBody().write(bytes);e.close();});jwks.start();}catch(Exception e){throw new ExceptionInInitializerError(e);}}
 @DynamicPropertySource static void props(DynamicPropertyRegistry r){r.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",()->"http://localhost:"+jwks.getAddress().getPort()+"/jwks");r.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",()->"https://issuer.test");}
 @LocalServerPort int port;
 String token(String role,String audience,long expires,RSAKey signingKey)throws Exception{
  var claims=new JWTClaimsSet.Builder().subject("alice").issuer("https://issuer.test").audience(audience).issueTime(Date.from(Instant.now().minusSeconds(120))).expirationTime(Date.from(Instant.now().plusSeconds(expires))).claim("roles",List.of(role)).build();
  var jwt=new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("test").build(),claims);jwt.sign(new RSASSASigner(signingKey));return jwt.serialize();
 }
 int get(String path,String token)throws Exception{var b=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path));if(token!=null)b.header("Authorization","Bearer "+token);return HttpClient.newHttpClient().send(b.GET().build(),HttpResponse.BodyHandlers.discarding()).statusCode();}
 @AfterAll static void stop(){jwks.stop(0);}
 @Test void rolesAndOwnershipAreEnforced()throws Exception{var reader=token("reader","backend-api",300,key);assertThat(get("/api/me",null)).isEqualTo(401);assertThat(get("/api/me",reader)).isEqualTo(200);assertThat(get("/api/reports",reader)).isEqualTo(403);assertThat(get("/api/accounts/alice",reader)).isEqualTo(200);assertThat(get("/api/accounts/bob",reader)).isEqualTo(403);assertThat(get("/api/reports",token("admin","backend-api",300,key))).isEqualTo(200);}
 @Test void rejectsExpiredWrongAudienceAndForgedTokens()throws Exception{assertThat(get("/api/me",token("reader","wrong",300,key))).isEqualTo(401);assertThat(get("/api/me",token("reader","backend-api",-120,key))).isEqualTo(401);assertThat(get("/api/me",token("reader","backend-api",300,new RSAKeyGenerator(2048).generate()))).isEqualTo(401);}
}
