# TLS y mTLS: laboratorio local

Requisitos: Java 21 (`keytool`), Bash y un curl con soporte de PKCS12. En Windows, usa Git Bash con JAVA_HOME apuntando al JDK 21. Los certificados se generan localmente, duran 30 días y nunca se versionan.

```bash
bash scripts/certificates.sh
mvn clean package
docker compose up -d keycloak
java -jar target/secure-api-demo-1.0.0.jar --spring.profiles.active=tls
curl --cacert certs/ca.crt https://localhost:8443/actuator/health
```

Detén el proceso TLS y arranca con autenticación de cliente obligatoria:

```bash
java -jar target/secure-api-demo-1.0.0.jar --spring.profiles.active=mtls
# Debe fallar durante el handshake: falta certificado de cliente.
curl --cacert certs/ca.crt https://localhost:8443/actuator/health
# Debe devolver UP:
curl --cacert certs/ca.crt --cert-type P12 \
  --cert certs/client.p12:local-demo-password \
  https://localhost:8443/actuator/health
```

Los endpoints `/api/**` siguen necesitando un JWT válido además del certificado. Agrega `-H "Authorization: Bearer $TOKEN"`. No uses `-k`: confiar explícitamente en la CA es parte del experimento.

El script se niega a sobrescribir una CA existente. El perfil TLS/mTLS aplica al backend iniciado localmente; Compose y la consola Angular usan HTTP en loopback por defecto. Si tu curl usa Schannel y no admite este formato de certificado, utiliza un curl con OpenSSL. La autenticación mTLS de transporte no se traduce automáticamente en roles de negocio.
