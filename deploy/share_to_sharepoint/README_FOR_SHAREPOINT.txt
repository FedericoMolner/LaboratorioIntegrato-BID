README for sharing Connector Java on SharePoint

1) What is included:
   - connector-java-0.0.1-SNAPSHOT.jar (Spring Boot fat JAR)
   - README_FOR_SHAREPOINT.txt (this file)

2) How to run (download the JAR locally):
   A) Run with Java (requires Java 17 installed):
      java -jar connector-java-0.0.1-SNAPSHOT.jar --connector.mode=LOCAL

   B) Run with Docker (no Java install required):
      docker run -d --rm --name connector-java -p 8080:8080 -v "%cd%\connector-java-0.0.1-SNAPSHOT.jar:/app/app.jar" azul/zulu-openjdk:17 java -jar /app/app.jar --connector.mode=LOCAL

3) Verify the service is running (health endpoint):
   curl http://localhost:8080/students/health

4) Notes (BigQuery / credentials):
   - If running in BIGQUERY mode, you must provide credentials as mount or env var.
   - Do not commit credentials in the repo or upload them to SharePoint.

5) Contact/Support: Ask Federico (owner) for details or the API key for `/bq/*` endpoints.
