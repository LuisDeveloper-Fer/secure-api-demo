#!/usr/bin/env sh
set -eu
mkdir -p certs
if [ -e certs/ca.p12 ]; then echo 'Certificates already exist; refusing to replace them.'; exit 1; fi
PASS=local-demo-password
keytool -genkeypair -alias ca -dname 'CN=Backend Lab CA' -ext bc:c -keyalg RSA -keysize 3072 -validity 30 -keystore certs/ca.p12 -storepass "$PASS"
keytool -exportcert -alias ca -keystore certs/ca.p12 -storepass "$PASS" -rfc -file certs/ca.crt
for NAME in server client; do
 keytool -genkeypair -alias "$NAME" -dname "CN=$NAME" -keyalg RSA -keysize 2048 -validity 30 -keystore "certs/$NAME.p12" -storepass "$PASS"
 keytool -certreq -alias "$NAME" -keystore "certs/$NAME.p12" -storepass "$PASS" -file "certs/$NAME.csr"
 if [ "$NAME" = server ]; then EXT='SAN=dns:localhost,dns:api,ip:127.0.0.1'; EKU=serverAuth; else EXT='SAN=dns:lab-client'; EKU=clientAuth; fi
 keytool -gencert -alias ca -keystore certs/ca.p12 -storepass "$PASS" -infile "certs/$NAME.csr" -outfile "certs/$NAME.crt" -rfc -validity 30 -ext "$EXT" -ext "EKU=$EKU" -ext KU=digitalSignature,keyEncipherment
 keytool -importcert -noprompt -alias ca -keystore "certs/$NAME.p12" -storepass "$PASS" -file certs/ca.crt
 keytool -importcert -alias "$NAME" -keystore "certs/$NAME.p12" -storepass "$PASS" -file "certs/$NAME.crt"
done
keytool -importcert -noprompt -alias ca -keystore certs/truststore.p12 -storepass "$PASS" -file certs/ca.crt
