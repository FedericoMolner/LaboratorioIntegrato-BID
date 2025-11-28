# --- STAGE 1: Build ---
FROM maven:3.9.4-eclipse-temurin-17 as builder
WORKDIR /workspace

# Copiamo prima solo il pom per sfruttare la cache
COPY pom.xml .
# Poi il codice sorgente
COPY src ./src

# Compiliamo saltando i test per velocità
RUN mvn clean package -DskipTests

# --- STAGE 2: Runtime ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiamo il JAR generato. 
# L'asterisco *.jar è fondamentale: se cambi versione nel pom.xml, questo continua a funzionare!
COPY --from=builder /workspace/target/*.jar app.jar

# --- FIX PER LE CREDENZIALI GOOGLE E L'ERRORE BAD SUBSTITUTION ---
# Invece di scrivere tutto nell'ENTRYPOINT, creiamo uno script bash pulito.
# Questo script controlla se c'è il JSON delle credenziali e lo salva su file.
RUN echo '#!/bin/bash' > /app/run.sh && \
    echo 'if [ -n "$GOOGLE_CREDENTIALS_JSON" ]; then' >> /app/run.sh && \
    echo '  echo "Trovata variabile GOOGLE_CREDENTIALS_JSON: creo il file temporaneo..."' >> /app/run.sh && \
    echo '  echo "$GOOGLE_CREDENTIALS_JSON" > /tmp/gcp-creds.json' >> /app/run.sh && \
    echo '  export GOOGLE_APPLICATION_CREDENTIALS=/tmp/gcp-creds.json' >> /app/run.sh && \
    echo 'fi' >> /app/run.sh && \
    echo 'exec java -jar /app/app.jar' >> /app/run.sh && \
    chmod +x /app/run.sh

EXPOSE 8080

# Usiamo bash per lanciare lo script, così evitiamo errori di sintassi
ENTRYPOINT ["/bin/bash", "/app/run.sh"]