# Progetto BID - Connector Java

Questo progetto implementa la parte BID (Business Intelligence & Data) di un'applicazione, fornendo un'API RESTful in Java con Spring Boot e un'interfaccia web semplice in HTML/CSS/JS per la gestione dei dati degli studenti. Utilizza un database H2 in memoria per lo sviluppo.

## Come avviare l'applicazione

Assicurati di avere **JDK 17** e **Maven** installati e configurati correttamente.

1.  **Naviga alla directory principale del progetto:**
    ```bash
    cd C:\Users\Ashna.Kaur\Desktop\Lab-Integrato\LaboratorioIntegrato-BID\Connector-java\Connector-java
    ```

2.  **Compila il progetto:**
    ```bash
    mvn clean install
    ```

3.  **Avvia l'applicazione Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```
    L'applicazione sarà avviata sulla porta `8080`.

## Informazioni Utili

### Accesso all'Applicazione Web
L'interfaccia web frontend è accessibile tramite il browser:
-   **URL Applicazione:** `http://localhost:8080/html/index.html`

### Accesso alla Console H2 Database
La console del database H2 è disponibile per la gestione dei dati in memoria:
-   **URL Console H2:** `http://localhost:8080/h2-console`
-   **JDBC URL:** `jdbc:h2:mem:testdb`
-   **Username:** `sa` (default per H2 in memoria)
-   **Password:** (lasciare vuoto per H2 in memoria)

### Endpoints REST API
Gli endpoint per la gestione degli studenti sono disponibili sotto `/api/students`:

-   **GET /api/students**: Recupera tutti gli studenti.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/api/students')
            .then(response => response.json())
            .then(data => console.log('Elenco studenti:', data))
            .catch(err => console.error('Errore:', err));
        ```
    -   **Risposta di esempio (JSON):**
        ```json
        [
          {
            "id": 1,
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
-   **GET /api/students/{id}**: Recupera uno studente specifico tramite ID.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/api/students/1')
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
-   **POST /api/students**: Crea un nuovo studente.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/api/students', {
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
-   **PUT /api/students/{id}**: Aggiorna uno studente esistente tramite ID.
    -   **Esempio di chiamata con JavaScript (fetch):**
        ```javascript
        fetch('http://localhost:8080/api/students/1', {
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
-   **DELETE /api/students/{id}**: Elimina uno studente specifico tramite ID.
- **DELETE /api/students/{id}**: Elimina uno studente specifico tramite ID.
    -   **Esempio di chiamata con JSON (tramite fetch JavaScript):**
        ```javascript
        fetch('http://localhost:8080/api/students/1', {
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
