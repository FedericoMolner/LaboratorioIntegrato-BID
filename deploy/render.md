# Deploy su Render — Guida rapida (Connector Java)

Questa guida contiene i passaggi minimi per usare Render (render.com) e impostare le env vars necessarie all'esecuzione, specialmente per BigQuery.

## File `render.env.example`
- Il file `render.env.example` contiene tutte le variabili di ambiente necessarie.
- Non inserire `GOOGLE_CREDENTIALS_JSON` nel repository. Invece:
  - Incolla la JSON nel valore di una env var mascherata su Render (mask/secret), oppure usa Render secrets se disponibili.

## Variabili importanti
- connector.mode=BIGQUERY|LOCAL (default consigliato in produzione: BIGQUERY)
- google.project.id
- google.credentials.path (usato come percorso del file nel container, ma preferiamo la versione JSON come env)
- bigquery.dataset, bigquery.table
- GOOGLE_CREDENTIALS_JSON (il JSON della service account in forma stringa - mascherata)
- app.api.key (opzionale, protegge le rotte /bq/**)
- app.metabase.url, app.metabase.embed-secret (se usi embedding Metabase)

## Procedura su Render (Web Service dall'interfaccia GitHub)
1. Connetti il repository GitHub su Render e crea un nuovo Web Service (o Web Service Docker se preferisci Dockerfile).
2. Imposta il `Build Command` (per native Java build):
   ```bash
   mvn -DskipTests clean package
   ```
3. Imposta il `Start Command`. Per BIGQUERY (scrivere la JSON su file a runtime):
   ```bash
   bash -lc 'echo "$GOOGLE_CREDENTIALS_JSON" > /tmp/gcp-creds.json && java $JAVA_OPTS -jar target/connector-java-0.0.1-SNAPSHOT.jar --google.credentials.path=/tmp/gcp-creds.json --connector.mode=$connector.mode'
   ```
   Per LOCAL (sviluppo/test):
   ```bash
   bash -lc 'java $JAVA_OPTS -jar target/connector-java-0.0.1-SNAPSHOT.jar --connector.mode=LOCAL'
   ```
### Deploy usando un'immagine pubblicata (GHCR / DockerHub)

Se preferisci gestire un'immagine nel registry, puoi generare e pubblicare una immagini e configurare Render per usare essa invece del Dockerfile repository. Con GitHub Actions incluso (`.github/workflows/ci-cd.yml`) puoi pushare su GHCR e poi impostare su Render l'immagine `ghcr.io/<owner>/connector-java:latest`.
4. Configura le `Environment` variables su Render (Settings → Environment). Copia le chiavi dal `render.env.example` e inserisci i valori:
   - connector.mode, google.project.id, bigquery.dataset, bigquery.table, app.api.key, app.metabase.*
   - Per le credenziali GCP: crea `GOOGLE_CREDENTIALS_JSON` e incolla il JSON come valore mascherato.
5. Salva e deploy.

## Note su sicurezza
- Il valore `GOOGLE_CREDENTIALS_JSON` deve essere segreto in Render. Render mostra la variabile come masked value; il team di deployment deve avere accessi limitati.
- In produzione preferisci invece di mettere la JSON direttamente in un secret manager (GCP Secret Manager) e montarla in runtime tramite un job o sidecar.

## Esempi di verifica post-deploy
- Health check:
  ```bash
  curl https://<your-render-app>.onrender.com/students/health
  ```
- Test BigQuery (API protetta con API key):
  ```bash
  curl -H "X-API-KEY: <your-api-key>" https://<your-render-app>.onrender.com/bq/test
  ```

## Troubleshooting
- Se l'avvio fallisce:
  - Vedi i logs in Render Dashboard
  - Verifica che `GOOGLE_CREDENTIALS_JSON` non sia vuoto quando connector.mode=BIGQUERY.
  - Controlla le permission del service account su GCP (bigquery.dataEditor / bigquery.dataViewer as needed).

## Automazione & CI
- Per pipeline automatizzata (GH Actions) possiamo:
  - Buildare e testare la jar, quindi pushare su Render (Render trigger via GitHub integration).
  - Alternativamente buildare e pushare image su registry privato e usare il Docker Deploy in Render.

---

Se vuoi, creo anche:
- Un `Dockerfile` pronto per Render e GitHub Actions workflow per build/push/deploy automatizzato; oppure
- Un file `render.yaml` contenente le env vars e un esempio di setting da importare.

Dimmi quale preferisci e procedo con l’implementazione.  