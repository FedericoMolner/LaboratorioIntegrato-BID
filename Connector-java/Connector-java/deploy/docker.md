# Deploy con Docker — Connector Java

Questa guida mostra come usare Docker (locale o cloud) per eseguire l'app Connector Java. È pensata sia per test locali sia per deploy su Render o altro provider che supporta Docker.

## Prerequisiti
- Docker installato
- Maven installato (per build locale) oppure usare la multi-stage Dockerfile che builda inside the container
- (Opzionale) file `creds.json` con la service account GCP, o la stringa JSON come env var

---

## 1) Build e test locali
1. Build jar
```bash
cd Connector-java/Connector-java
mvn -U -DskipTests clean package
```

2. Build image Docker (locale)
```bash
docker build -t connector-java:local .
```

3. Esegui container in LOCAL (non usa BigQuery):
```bash
docker run --rm -p 8080:8080 -e connector.mode=LOCAL connector-java:local
```

4. Debug: visita `http://localhost:8080/html/index.html` o controlla `GET /students`

---

## 2) Testare con BigQuery (local)
Hai due opzioni:

A) Montare la chiave JSON nel container (consigliato per test locali solo):
```bash
docker run --rm -p 8080:8080 -v $(pwd)/secrets/gcp.json:/tmp/gcp.json:ro \
  -e connector.mode=BIGQUERY \
  -e google.project.id=YOUR_PROJECT_ID \
  -e google.credentials.path=/tmp/gcp.json \
  -e bigquery.dataset=YOUR_DATASET \
  -e bigquery.table=students \
  connector-java:local
```

B) Passare la JSON come env var (se la stringa è grande assicurati che la tua shell la supporti):
```bash
export GOOGLE_CREDENTIALS_JSON=$(cat secrets/gcp.json | jq -r @json)
docker run --rm -p 8080:8080 -e connector.mode=BIGQUERY -e google.project.id=YOUR_PROJECT_ID \
  -e GOOGLE_CREDENTIALS_JSON="$GOOGLE_CREDENTIALS_JSON" connector-java:local
```

> Nota: la Dockerfile è già configurata per scrivere `GOOGLE_CREDENTIALS_JSON` in `/tmp/gcp-creds.json` e passare quel path con `--google.credentials.path`.

---

## 3) Eseguire con docker-compose (SVILUPPO)
- Esempio di `docker-compose.yml` incluso. È pensato per `LOCAL` default: per provare BigQuery edit the file or use CLI env overrides:
```bash
docker compose up --build
```

Per avviare con BigQuery (usando il monte file):
```bash
docker compose up --build -d
# Override or use env file with GOOGLE_CREDENTIALS_JSON, connector.mode=BIGQUERY...
```

---

## 4) Deploy su registries e utilizzo remoto (Render, GCR, ACR)
- Build image e push su un registry:
```bash
docker tag connector-java:local registry.example.com/your-namespace/connector-java:latest
docker push registry.example.com/your-namespace/connector-java:latest
```
- Su Render, crea un Web Service e scegli il deploy dal Dockerfile o immagine docker.
- Imposta le env vars come in `render.env.example` in Render Settings.
- Start command (Render expects `CMD` or entrypoint from Dockerfile). Dockerfile entrypoint writes JSON from env var into a file and starts the jar.

---

## 5) Verifiche post-deploy
- Health check: `curl https://<host>/students/health`
- List students: `curl https://<host>/students`
- BigQuery test (con API key se abilitato):
  `curl -H "X-API-KEY: <your-api-key>" https://<host>/bq/test`

---

## Troubleshooting rapida
- Se il servizio non si avvia: leggere logs docker
- Errori JSON o credenziali: assicurati che `GOOGLE_CREDENTIALS_JSON` sia una singola variabile env ben formata o che il file montato sia accessibile

---

Se vuoi, aggiungo un GitHub Actions workflow per build & push image su registry (ACR/GCR/Hub) e un esempio `render.yaml` per auto deploy.