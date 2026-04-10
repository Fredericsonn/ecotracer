FROM eclipse-temurin:17-jre

WORKDIR /usr/src/app

COPY eco-auth/target/eco-auth-1.0.0.jar app.jar
COPY eco-auth/vault-ca.crt /tmp/vault-ca.crt

RUN mkdir -p /opt/certs && \
    keytool -importcert \
      -alias vault-ca \
      -file /tmp/vault-ca.crt \
      -keystore /opt/certs/vault-truststore.jks \
      -storepass changeit \
      -noprompt && \
    rm -f /tmp/vault-ca.crt

EXPOSE 8081

ENTRYPOINT ["java", \
  "-Dspring.cloud.vault.ssl.trust-store=file:/opt/certs/vault-truststore.jks", \
  "-Dspring.cloud.vault.ssl.trust-store-password=changeit", \
  "-jar", "app.jar"]