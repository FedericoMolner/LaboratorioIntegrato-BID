# Progetto BID - Connector Java

Questo modulo è un semplice connettore REST in Java (Spring Boot) che collega la parte DAI (dove i dati sono memorizzati su BigQuery) con la parte BAD (consumatori come PowerApps, Metabase, etc.).
Offre inoltre un'interfaccia web minima per sviluppo e test.

## Sommario
- Panoramica e scopo
- Come avviare l'app (LOCAL/BIGQUERY)
- Configurazioni principali
- Endpoints (Studenti + BigQuery Connector + Metabase Embed)
- Test e sviluppo
- Esempio PowerApps & sicurezza

---

## Panoramica
Architettura e design:
- Layer controller → service → repository
- `StudentService` supporta due modalità tramite property `connector.mode`: `BIGQUERY` (default) o `LOCAL`.
  - `BIGQUERY`: il servizio opera esclusivamente su BigQuery; se le credenziali mancano l'app fallisce all'avvio (utile per evitare fallback inattesi in produzione).
  - `LOCAL`: il servizio usa H2 (o altro DB JPA configurato) per sviluppo e test locale.

---

## Requisiti
- Java 17 (consigliato per compatibilità con Spring Boot 3.x)
- Maven 3.8+
- (Opzionale) GCP Service Account JSON con permessi BigQuery

---

## Come avviare l'applicazione
Apri una shell e posizionati in:
```bash
cd Connector-java/Connector-java
```

Per sviluppo con H2 (LOCAL):
```bash
mvn -U -DskipTests=false test
mvn spring-boot:run -Dspring-boot.run.arguments="--connector.mode=LOCAL"
```

Per avviare come connettore BigQuery (production):
Assicurati di impostare i valori (o passare come variabili env):
```properties
connector.mode=BIGQUERY
google.project.id=YOUR_PROJECT_ID
google.credentials.path=/absolute/path/to/credentials.json
bigquery.dataset=YOUR_DATASET
bigquery.table=students
app.api.key=REPLACE_WITH_SECURE_KEY        # opzionale per /bq endpoints
app.metabase.url=https://metabase.example.com
app.metabase.embed-secret=...              # se vuoi usare embedding
```
Poi avvia con:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--connector.mode=BIGQUERY --google.project.id=... --google.credentials.path=/path/creds.json"
```

---

## Configurazioni principali
- `connector.mode` — `BIGQUERY`|`LOCAL` (default: `BIGQUERY` — la proprietà è presente in `application.properties`)
- `google.project.id` — Google Cloud project ID
- `google.credentials.path` — percorso alla chiave JSON del service account
- `bigquery.dataset` — il dataset BigQuery che contiene la tabella
- `bigquery.table` — la tabella BigQuery
- `app.api.key` — API Key che protegge `/bq/**` (opzionale, ma raccomandata in produzione)
- `app.metabase.url` e `app.metabase.embed-secret` — per embeddare dashboard Metabase

---

## Endpoints principali

Student CRUD (usati da applicazioni e interfaccia):
- GET  /students — restituisce tutti gli studenti
- GET  /students/{id} — restituisce uno studente specifico tramite ID.
- POST /students — crea uno studente (body JSON: name/surname/email)
- PUT  /students/{id} — aggiorna lo studente
- DELETE /students/{id} — elimina lo studente
- GET  /students/health — health check

BigQuery / Connector endpoints (per DAI/BAD/PowerApps):
- GET  /bq/test — semplice test (es. SELECT CURRENT_DATE())
- GET  /bq/students?limit=100 — legge gli studenti dalla tabella BigQuery
- POST /bq/students — inserisce uno studente su BigQuery (richiede `X-API-KEY` se configurato)
- POST /bq/local/students — inserisce uno studente nel DB locale (per sviluppo/test)

Metabase embedding
- GET /embed/dashboard/{id} — ottiene un URL firmato per embeddare una dashboard Metabase

Note:
- Endpoint `/bq/**` sono protetti con `X-API-KEY` se `app.api.key` è impostata.
- In produzione, preferire OAuth2 / Azure AD per protezione e auditing.

---

## Esempi (curl/JS)
Creazione studente (POST /students):
```bash
curl -X POST http://localhost:8080/students \
  -H 'Content-Type: application/json' \
  -d '{"name":"Mario","surname":"Rossi","email":"mario.rossi@example.com"}'
```

Lettura studenti (GET /students):
```bash
curl http://localhost:8080/students
```

Inserimento verso BigQuery (POST /bq/students):
```bash
curl -H "X-API-KEY: <your-key>" -H "Content-Type: application/json" -X POST -d '{"name":"Mario","surname":"Rossi","email":"mario.rossi@example.com"}' http://localhost:8080/bq/students
```

Embeddare Metabase in frontend (JS):
```javascript
fetch('/embed/dashboard/4')
  .then(r => r.json())
  .then(d => {
      const iframe = document.createElement('iframe');
      iframe.src = d.url;
      iframe.style.width = '100%'; iframe.style.height = '600px';
      document.querySelector('#embed-area').appendChild(iframe);
  });
```

---

## Tests
- Esegui tutti i test (unit + integration) localmente:
```bash
mvn -U -DskipTests=false test
```
I test di integrazione che coinvolgono `/bq/**` sono impostati per eseguire in `LOCAL` mode se `connector.mode=LOCAL`.
Per forzare la modalità `LOCAL`:
```bash
mvn -DskipTests=false -Dconnector.mode=LOCAL test
```

---

## PowerApps / DAI / BAD - integrazione
Se stai collegando PowerApps alla nostra app:
1. Definisci un Custom Connector che punti alle route `/bq/students` del nostro servizio.
2. Se in produzione usi API key, passa `X-API-KEY: <key>` nelle intestazioni. Se usi OAuth2, configura il connector con il provider OIDC.
3. Verifica con `GET /bq/test` e `GET /bq/students`.

---

## Suggerimenti per la produzione
- Usare managed identities (o Secret Manager / Azure KeyVault / GCP Secret Manager) per gestire la chiave BigQuery.
- Evitare di committare `google.credentials.path` in repo; usare CI secrets e variabili d'ambiente.
- Aggiungi log e metriche (Prometheus/Datadog) e circuit breakers se richiesto (per query BigQuery grosse e latenza).

---

## Contribuire & CI
Se vuoi che aggiunga un workflow GitHub Actions per build/test deploy, o integrazione con staging BigQuery, posso schedulare questi elementi e aggiungere check di sicurezza (scan dei secrets, linting, static analysis).

Grazie — se vuoi procedo con la creazione di un GitHub Actions workflow (build + test) o con l'integrazione di OAuth2 per `/bq/**`.
# Progetto BID - Connector Java

Questa repository fornisce una semplice applicazione Spring Boot per fungere da connettore tra la parte DAI (che espone dati su BigQuery) e la parte BAD (che consumerà i dati), con una UI minimale per sviluppo.

📌 Scopo:
- Fornire API REST per la gestione di studenti (CRUD)
- Se configurata in modalità BIGQUERY, agisce come connettore esclusivo verso BigQuery (read/write)
- In modalità LOCAL, fornisce una versione locale (H2) per sviluppo e test
- Genera URL firmati per embeddare dashboard Metabase

---

## Prerequisiti
- Java 17 (compatibile, si consiglia Java 17 per build/test)
- Maven 3.8+
- (Opzionale, per BigQuery) Account service Google Cloud con chiave JSON e permessi BigQuery

---

## Quick start - Sviluppo locale (LOCAL)
1. Spostati nella cartella del modulo:
```bash
cd Connector-java/Connector-java
```
2. Esegui build e test (LOCAL):
```bash
mvn -U -DskipTests=false test
```
3. Avvia l'applicazione (LOCAL):
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--connector.mode=LOCAL"
```
L'applicazione sarà visibile su `http://localhost:8080`.

NOTE: la modalità di default `connector.mode` è `BIGQUERY` nel file di configurazione; per lo sviluppo consiglio di avviare con `--connector.mode=LOCAL` o impostare `connector.mode=LOCAL` nel file `application.properties`.

---

## Quick start - Modalità BIGQUERY (production connector)
1. Prepara il service account e la chiave JSON (scaricala dal GCP console). Non committare mai la chiave su Git.
2. Configura le proprietà (o usa variabili d'ambiente):
```properties
connector.mode=BIGQUERY
google.project.id=YOUR_PROJECT_ID
google.credentials.path=/absolute/path/to/credentials.json
bigquery.dataset=YOUR_DATASET
bigquery.table=students
```
3. Avvia in modalità `BIGQUERY`:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--connector.mode=BIGQUERY --google.project.id=... --google.credentials.path=/path/creds.json"
```

Comportamento in `BIGQUERY` mode:
- L'app cercherà di creare un client BigQuery. Se le credenziali o il `project.id` mancano, l'app fallisce all'avvio — Questo è voluto per assicurare che in produzione l'app sia un connettore verso BigQuery e non usi fallback locale.

---

## Architettura & Componenti principali
- `Student` (model): entità JPA per la demo.
- `StudentRepository`: Spring Data JPA repository per H2 / DB locale.
- `StudentService`: layer che implementa la logica CRUD e decide, in funzione di `connector.mode`, se usare BigQuery o il DB locale.
- `StudentController` (REST): espone endpoint CRUD per `/students`.
- `BigQueryConfig`: crea il client BigQuery solo se `connector.mode=BIGQUERY` e `google.credentials.path` è impostato.
- `BigQueryController`: endpoints `/bq/*` per lettura/scrittura dedicati a integrazioni DAI/BAD (ad es. PowerApps).
- `MetabaseEmbedService` / `MetabaseEmbedController`: firmare URL per embedding responsabile per Metabase.
- `ApiKeyFilter`: filtro semplice che blocca chiamate ai `/bq/**` se non viene passato l'header `X-API-KEY` con il valore concordato (`app.api.key`).

---

## Endpoints API (RIASSUNTO)

Student CRUD (usable from both LOCAL and BIGQUERY modes):
- GET  /students                 -> ottieni tutti (H2 o BigQuery a seconda della modalità)
- GET  /students/{id}            -> ottieni singolo studente
- POST /students                 -> crea studente
- PUT  /students/{id}            -> aggiorna studente
- DELETE /students/{id}          -> elimina studente

BigQuery / Connector endpoints (richiedono `X-API-KEY` se impostato):
- GET  /bq/test                  -> Test query (DEBUG e utilità)
- GET  /bq/students?limit=100    -> Leggi studenti da BigQuery
- POST /bq/students              -> Inserisci studente su BigQuery (o fallback a locale se in `LOCAL`)
- POST /bq/local/students        -> Inserisci studente nel DB locale (dev/test)

Metabase embedding:
- GET /embed/dashboard/{id}      -> Ottiene URL firmato per inserire un iframe Metabase

Note: in produzione proteggi `/bq/**` con API Key o OIDC/OAuth2.

---

## Configurazione (application.properties)
- server.port: porta (default: 8080)
- connector.mode: `BIGQUERY` | `LOCAL` (default: BIGQUERY)
- google.project.id
- google.credentials.path
- bigquery.dataset
- bigquery.table
- app.api.key: semplice API key per proteggere le rotte `/bq/*`
- app.metabase.embed-secret, app.metabase.url: per embedding Metabase

## Test
- Esegui tutti i test unitari e d'integrazione:
```bash
mvn -U -DskipTests=false test
```

- Se vuoi eseguire i test di integrazione con `connector.mode=LOCAL`, esempio:
```bash
mvn -Dtest=**/*IntegrationTest -DskipTests=false test -Dconnector.mode=LOCAL
```

Quando `connector.mode=LOCAL`, i test useranno un database H2 in memoria e non richiederanno credenziali BigQuery.

---

## Come usare questi endpoint con PowerApps (esempio di flusso)
1. Crea un Custom Connector in PowerApps che punti all'URL della tua app (es. `https://your-service.blob.azure.net` o `http://localhost:8080`).
2. Mappa gli endpoint GET /bq/students e POST /bq/students nel Custom Connector.
3. Se in produzione proteggerai le rotte con OIDC/OAuth, configura PowerApps per usare il flusso OAuth. Se sarai con API Key, imposta header `X-API-KEY` nelle chiamate del Custom Connector.

---

## Best practices e Considerazioni
- Non salvare credenziali nel repo; usa variabili d'ambiente o Secret Manager.
- In produzione usa OAuth2 o Azure AD come meccanismo di autenticazione per garantire sicurezza e gestione utenti.
- Considera pattern asincroni per la sincronizzazione delle scritture verso BigQuery (pub/sub, queue, CDC) se i volumi sono elevati.

---

## Metodi di sviluppo rapidi e debug
- Per eseguire in background su Windows: `start-server.ps1` (fornisce un esempio di start e attende il messaggio 'Started').
- Verifica l'health endpoint: `GET /students/health`.

---

## Contatti & Supporto
Se vuoi che al progetto vengano aggiunti: GitHub Actions per CI/CD, integrazione con GCP BigQuery in staging, o policy di sicurezza migliori (OAuth2), fammelo sapere — posso aggiungerlo come prossima attività.

---

Grazie per l'attenzione — se vuoi procedo a generare il GitHub Actions per build/test oppure ad aggiungere il supporto per OIDC/OAuth2 per la sicurezza dei `/bq/` endpoints.
# Progetto BID - Connector Java

Questo progetto implementa la parte BID (Business Intelligence & Data) di un'applicazione, fornendo un'API RESTful in Java con Spring Boot e un'interfaccia web semplice in HTML/CSS/JS per la gestione dei dati degli studenti. Per lo sviluppo locale utilizza un database H2 (file-based) di default; puoi passare a modalità in-memory se desideri.

## Come avviare l'applicazione

Assicurati di avere **JDK 17** e **Maven** installati e configurati correttamente.

1.  **Naviga alla directory principale del progetto:**
    ```bash
    cd C:\Users\Ashna.Kaur\Desktop\Lab-Integrato\LaboratorioIntegrato-BID\Connector-java\Connector-java
    ```

2.  **Compila il progetto:**
    ```bash
    ```

3.  **Avvia l'applicazione Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```
    L'applicazione sarà avviata sulla porta `8080`.
Gli endpoint per la gestione degli studenti sono disponibili sotto `/students`:
   **GET /students**: Recupera tutti gli studenti.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/students')
            .then(response => response.json())
            .then(data => console.log('Elenco studenti:', data))
            .catch(err => console.error('Errore:', err));
        ```
    Alternative:
    - Usa lo script di avvio locale: `powershell -ExecutionPolicy Bypass -File .\start-server.ps1 -JarPath target\Connector-java-0.0.1-SNAPSHOT.jar -WaitSeconds 30`

## Informazioni Utili

### Accesso all'Applicazione Web
L'interfaccia web frontend è accessibile tramite il browser:
-   **URL Applicazione:** `http://localhost:8080/html/index.html`
   **GET /students/{id}**: Recupera uno studente specifico tramite ID.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/students/1')
            .then(response => response.json())
            .then(data => console.log('Dettagli studente:', data))
            .catch(err => console.error('Errore:', err));
        ```

La console del database H2 è disponibile per la gestione dei dati locali:
-   **URL Console H2:** `http://localhost:8080/h2-console`
-   **JDBC URL (file):** `jdbc:h2:file:./data/testdb`
-   **Username:** `sa` (default)
-   **Password:** (lasciare vuoto se non specificata)

### Endpoints REST API
Gli endpoint per la gestione degli studenti sono disponibili sotto `/students`:

-   **GET /students**: Recupera tutti gli studenti.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/students')
            .then(response => response.json())
            .then(data => console.log('Elenco studenti:', data))
            .catch(err => console.error('Errore:', err));
        ```
   **POST /students**: Crea un nuovo studente.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/students', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "name": "Nome",
                "surname": "Cognome",
                "email": "email@example.com"
            })
        })
        .then(response => response.json())
        .then(data => console.log('Studente creato:', data))
        .catch(err => console.error('Errore:', err));
        ```
    -   **Risposta di esempio (JSON):**
        [
          {
            "id": 1,

        ---

        ## Docker & CI / CD
        Se preferisci un'immagine container e GitHub Actions per build & publish, è incluso un workflow in `.github/workflows/ci-cd.yml` che:
        - costruisce il progetto (Maven)
        - esegue test
        - costruisce e pubblica l'immagine su GitHub Container Registry (GHCR)

        Sotto `scripts/` troverai utility per test locali e per costruire/pushare l'immagine:
        - `scripts/run-local.sh` / `scripts/run-local.bat` — build+run con Docker in LOCAL mode
        - `scripts/build-and-push.sh` / `.bat` — build + push su GHCR (usa env GITHUB_TOKEN)

        Render supporta deploy da Dockerfile o immagine pubblicata su registry: puoi configurarlo per usare la nostra `Dockerfile` o impostare la 'Image' su ghcr.io/<owner>/connector-java:latest.
            "name": "Mario",
            "surname": "Rossi",
            "email": "mario.rossi@example.com"
          },
          {
            "id": 2,
            "name": "Lucia",
            "surname": "Bianchi",
            "email": "lucia.bianchi@example.com"
          }
        ]
        ```
-   **GET /students/{id}**: Recupera uno studente specifico tramite ID.
    -   **Esempio di chiamata con JavaScript (fetch):**
   **PUT /students/{id}**: Aggiorna uno studente esistente tramite ID.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/students/1', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "name": "NuovoNome",
                "surname": "NuovoCognome",
                "email": "nuova.email@example.com"
            })
        })
        .then(response => response.json())
        .then(data => console.log('Studente aggiornato:', data))
        .catch(err => console.error('Errore:', err));
        ```
        ```javascript
            .then(response => response.json())
            .then(data => console.log('Dettagli studente:', data))
            .catch(err => console.error('Errore:', err));
        ```
    -   **Risposta di esempio (JSON):**
        ```json
        {
            "id": 1,
            "name": "Mario",
            "surname": "Rossi",
            "email": "mario.rossi@example.com"
        }
        ```
-   **POST /students**: Crea un nuovo studente.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
   **DELETE /students/{id}**: Elimina uno studente specifico tramite ID.
    -   **Esempio di chiamata con JSON (tramite fetch JavaScript):**
        ```javascript
        fetch('http://localhost:8080/students/1', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                console.log('Studente eliminato con successo');
            } else {
                console.error('Errore durante l\'eliminazione dello studente');
            }
        });
        ```
        fetch('http://localhost:8080/students', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "name": "Nome",
                "surname": "Cognome",
                "email": "email@example.com"
            })
        })
        .then(response => response.json())
        .then(data => console.log('Studente creato:', data))
        .catch(err => console.error('Errore:', err));
        ```
    -   **Esempio Body (JSON):**
        ```json
        {
            "name": "Nome",
            "surname": "Cognome",
            "email": "email@example.com"
        }
        ```
-   **PUT /students/{id}**: Aggiorna uno studente esistente tramite ID.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/students/1', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "name": "NuovoNome",
                "surname": "NuovoCognome",
                "email": "nuova.email@example.com"
            })
        })
        .then(response => response.json())
        .then(data => console.log('Studente aggiornato:', data))
        .catch(err => console.error('Errore:', err));
        ```
    -   **Esempio Body (JSON):**
        ```json
        {
            "name": "NuovoNome",
            "surname": "NuovoCognome",
            "email": "nuova.email@example.com"
        }
        ```
-   **DELETE /students/{id}**: Elimina uno studente specifico tramite ID.
- **DELETE /students/{id}**: Elimina uno studente specifico tramite ID.
    -   **Esempio di chiamata con JSON (tramite fetch JavaScript):**
        ```javascript
        fetch('http://localhost:8080/students/1', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                console.log('Studente eliminato con successo');
            } else {
                console.error('Errore durante l\'eliminazione dello studente');
            }
        });
        ```


---
**Nota:** L'applicazione è attualmente in esecuzione. Per fermarla, premi `Ctrl+C` nel terminale dove è stata avviata.

## Metabase embedding (dashboard integration)
Questa applicazione può generare URL firmate per embeddare dashboard da Metabase in modo sicuro.

1. Ottieni il "Embed secret" da Metabase (Admin -> Settings -> Embedding -> Signed Embedding).
2. Imposta `app.metabase.url` e `app.metabase.embed-secret` in `application.properties` o come variabili d'ambiente.
3. L'endpoint `GET /embed/dashboard/{id}` restituisce `{ "url": "https://..." }` con URL firmato per l'ID di dashboard richiesto.

Esempio con `fetch` dal frontend:
```javascript
fetch('/embed/dashboard/4')
    .then(r => r.json()).then(d => {
        const iframe = document.createElement('iframe');
        iframe.src = d.url;
        iframe.style.width = '100%';
        iframe.style.height = '600px';
        document.querySelector('.table-container').appendChild(iframe);
    });
```

## BigQuery integration (opzionale)
Se vuoi usare BigQuery come sorgente di verità per analytics (Metabase o l'app), segui questi passi:

1. Crea Service Account con permessi BigQuery e scarica la chiave JSON.
2. Imposta queste proprietà in `application.properties`:
```
app.bigquery.project-id=YOUR_PROJECT_ID
app.bigquery.credentials-file=C:/path/to/bq-key.json

### Connector mode
The application can run in two modes:
- BIGQUERY (default): the app is a connector only and reads/writes from BigQuery. The app will fail to start if BigQuery credentials are not available.
- LOCAL: used for development & tests — it uses H2 as fallback storage and does not require BigQuery credentials.

To set the mode, configure `connector.mode` in `application.properties` or as an environment variable. For example to run in LOCAL mode:

```
connector.mode=LOCAL
```

For production (BigQuery-only):

```
connector.mode=BIGQUERY
google.project.id=YOUR_PROJECT_ID
google.credentials.path=/absolute/path/to/credentials.json
```
```
3. La classe `BigQueryService` mostra come impostare il client e eseguire query SQL.

- Endpoints utili per integrazione DAI/BAD/PowerApps
Note:
- Se vuoi che le modifiche fatte nella app arrivino su BigQuery, devi implementare sync (sincrono o asincrono). L'app contiene un servizio di base che può essere usato come riferimento.
- Non memorizzare segreti (chiavi) nel codice o nel repository; preferisci variabili d'ambiente o Secret Manager.

### Endpoints per integrazione DAI/BAD/PowerApps (BigQuery)

 L'app espone un set di endpoint per fungere da tramite tra la parte DAI/BAD e BigQuery:

 - `GET /bq/test` – Testa la connessione a BigQuery; esegue una query semplice e restituisce il risultato (utile per debug).
 - `GET /bq/students?limit=100` – Legge gli studenti dalla tabella BigQuery configurata (impostata con `app.bigquery.dataset` e `app.bigquery.table`). Ritorna una lista JSON di risultati.
 - `POST /bq/students` – Inserisce uno studente su BigQuery. Body JSON d'esempio:
```
{
    "name": "Nome",
    "surname": "Cognome",
    "email": "email@esempio.it"
}
```
 Risponde `201` in caso di successo.

Questi endpoint sono pensati per essere consumati da PowerApps o altre interfacce. In ambiente di produzione è opportuno proteggere gli endpoint con autenticazione (es. API Key o OAuth2 - Azure AD). Per sviluppo, puoi impostare `app.api.key` e inviare `X-API-KEY` nelle richieste per accedere alle rotte `/bq/**`.

Esempio di chiamata con `curl`:
```
curl -H "X-API-KEY: your-api-key" -H "Content-Type: application/json" -X POST -d '{"name":"Mario","surname":"Rossi","email":"mario.rossi@its.it"}' http://localhost:8080/bq/students
```

