package com.its.statistics.service;

import com.google.cloud.bigquery.*;
import com.its.statistics.dto.StudentDTO;
import com.its.statistics.dto.StudentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.its.statistics.repository.StudentRepository;
import com.its.statistics.model.Student;

@Service
public class StudentService {

    @Autowired(required = false)
    private BigQuery bigQuery;

    @Autowired
    private StudentRepository studentRepository;

    @Value("${bigquery.dataset}")
    private String dataset;

    @Value("${bigquery.table}")
    private String table;

    @Value("${connector.mode:BIGQUERY}")
    private String connectorMode;

    /**
     * Recupera tutti gli studenti da BigQuery
     */
    public List<StudentDTO> getAllStudents() {
        // Use BigQuery only if connector.mode is BIGQUERY and bigQuery is configured
        if (!"BIGQUERY".equalsIgnoreCase(connectorMode) || bigQuery == null || bigQuery.getOptions() == null || bigQuery.getOptions().getProjectId() == null || bigQuery.getOptions().getProjectId().isEmpty()) {
            List<StudentDTO> students = new ArrayList<>();
            for (Student s : studentRepository.findAll()) {
                students.add(new StudentDTO(String.valueOf(s.getId()), s.getName(), s.getSurname(), s.getEmail()));
            }
            return students;
        }
        String query = String.format(
            "SELECT id, name, surname, email FROM `%s.%s.%s`",
            bigQuery.getOptions().getProjectId(),
            dataset,
            table
        );

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .setUseLegacySql(false)
                .build();

        try {
            TableResult result = bigQuery.query(queryConfig);
            List<StudentDTO> students = new ArrayList<>();

            for (FieldValueList row : result.iterateAll()) {
                students.add(mapRowToDTO(row));
            }

            return students;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Errore durante il recupero degli studenti da BigQuery", e);
        }
    }

    @javax.annotation.PostConstruct
    public void validateStartup() {
        // If running in BIGQUERY mode, BigQuery bean and configuration must be available
        if ("BIGQUERY".equalsIgnoreCase(connectorMode)) {
            if (bigQuery == null) {
                throw new IllegalStateException("connector.mode=BIGQUERY but BigQuery bean is not configured. Provide google.credentials.path and google.project.id");
            }
        }
    }

    /**
     * Recupera uno studente per ID
     */
    public StudentDTO getStudentById(String id) {
        if (!"BIGQUERY".equalsIgnoreCase(connectorMode) || bigQuery == null || bigQuery.getOptions() == null || bigQuery.getOptions().getProjectId() == null || bigQuery.getOptions().getProjectId().isEmpty()) {
            try {
                Long longId = Long.valueOf(id);
                Student s = studentRepository.findById(longId).orElseThrow(() -> new RuntimeException("Studente non trovato con id: " + id));
                return new StudentDTO(String.valueOf(s.getId()), s.getName(), s.getSurname(), s.getEmail());
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID non valido per DB locale: " + id);
            }
        }
        String query = String.format(
            "SELECT id, name, surname, email FROM `%s.%s.%s` WHERE id = @id",
            bigQuery.getOptions().getProjectId(),
            dataset,
            table
        );

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .setUseLegacySql(false)
                .addNamedParameter("id", QueryParameterValue.string(id))
                .build();

        try {
            TableResult result = bigQuery.query(queryConfig);
            
            if (result.getTotalRows() == 0) {
                throw new RuntimeException("Studente non trovato con id: " + id);
            }

            FieldValueList row = result.iterateAll().iterator().next();
            return mapRowToDTO(row);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Errore durante il recupero dello studente da BigQuery", e);
        }
    }

    /**
     * Crea un nuovo studente
     */
    public StudentDTO createStudent(StudentRequest request) {
        // Se BigQuery non è configurato, salva nel DB locale
        if (!"BIGQUERY".equalsIgnoreCase(connectorMode) || bigQuery == null || bigQuery.getOptions() == null || bigQuery.getOptions().getProjectId() == null || bigQuery.getOptions().getProjectId().isEmpty()) {
            Student student = new Student(request.getName(), request.getSurname(), request.getEmail());
            Student saved = studentRepository.save(student);
            return new StudentDTO(String.valueOf(saved.getId()), saved.getName(), saved.getSurname(), saved.getEmail());
        }

        String id = UUID.randomUUID().toString();
        
        String query = String.format(
            "INSERT INTO `%s.%s.%s` (id, name, surname, email) VALUES (@id, @name, @surname, @email)",
            bigQuery.getOptions().getProjectId(),
            dataset,
            table
        );

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .setUseLegacySql(false)
                .addNamedParameter("id", QueryParameterValue.string(id))
                .addNamedParameter("name", QueryParameterValue.string(request.getName()))
                .addNamedParameter("surname", QueryParameterValue.string(request.getSurname()))
                .addNamedParameter("email", QueryParameterValue.string(request.getEmail()))
                .build();

        try {
            bigQuery.query(queryConfig);
            
            // Ritorna il nuovo studente creato
            return new StudentDTO(id, request.getName(), request.getSurname(), request.getEmail());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Errore durante la creazione dello studente in BigQuery", e);
        }
    }

    /**
     * Aggiorna uno studente esistente
     */
    public StudentDTO updateStudent(String id, StudentRequest request) {
        // Se BigQuery non è configurato fallback al DB
        if (!"BIGQUERY".equalsIgnoreCase(connectorMode) || bigQuery == null || bigQuery.getOptions() == null || bigQuery.getOptions().getProjectId() == null || bigQuery.getOptions().getProjectId().isEmpty()) {
            try {
                Long longId = Long.valueOf(id);
                Student existing = studentRepository.findById(longId).orElseThrow(() -> new RuntimeException("Studente non trovato con id: " + id));
                if (request.getName() != null && !request.getName().isEmpty()) existing.setName(request.getName());
                if (request.getSurname() != null && !request.getSurname().isEmpty()) existing.setSurname(request.getSurname());
                if (request.getEmail() != null && !request.getEmail().isEmpty()) existing.setEmail(request.getEmail());
                Student saved = studentRepository.save(existing);
                return new StudentDTO(String.valueOf(saved.getId()), saved.getName(), saved.getSurname(), saved.getEmail());
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID non valido per DB locale: " + id);
            }
        }

        // Verifica che lo studente esista
        getStudentById(id);

        List<String> updates = new ArrayList<>();
        List<QueryParameterValue> parameters = new ArrayList<>();
        
        if (request.getName() != null && !request.getName().isEmpty()) {
            updates.add("name = @name");
            parameters.add(QueryParameterValue.string(request.getName()));
        }
        if (request.getSurname() != null && !request.getSurname().isEmpty()) {
            updates.add("surname = @surname");
            parameters.add(QueryParameterValue.string(request.getSurname()));
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            updates.add("email = @email");
            parameters.add(QueryParameterValue.string(request.getEmail()));
        }

        if (updates.isEmpty()) {
            throw new RuntimeException("Nessun campo da aggiornare");
        }

        String query = String.format(
            "UPDATE `%s.%s.%s` SET %s WHERE id = @id",
            bigQuery.getOptions().getProjectId(),
            dataset,
            table,
            String.join(", ", updates)
        );

        QueryJobConfiguration.Builder queryBuilder = QueryJobConfiguration.newBuilder(query)
                .setUseLegacySql(false)
                .addNamedParameter("id", QueryParameterValue.string(id));

        // Aggiungi i parametri dinamicamente
        if (request.getName() != null && !request.getName().isEmpty()) {
            queryBuilder.addNamedParameter("name", QueryParameterValue.string(request.getName()));
        }
        if (request.getSurname() != null && !request.getSurname().isEmpty()) {
            queryBuilder.addNamedParameter("surname", QueryParameterValue.string(request.getSurname()));
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            queryBuilder.addNamedParameter("email", QueryParameterValue.string(request.getEmail()));
        }

        try {
            bigQuery.query(queryBuilder.build());
            
            // Ritorna lo studente aggiornato
            return getStudentById(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Errore durante l'aggiornamento dello studente in BigQuery", e);
        }
    }

    /**
     * Elimina uno studente
     */
    public void deleteStudent(String id) {
        // Se BigQuery non è configurato fallback al DB
        if (!"BIGQUERY".equalsIgnoreCase(connectorMode) || bigQuery == null || bigQuery.getOptions() == null || bigQuery.getOptions().getProjectId() == null || bigQuery.getOptions().getProjectId().isEmpty()) {
            try {
                Long longId = Long.valueOf(id);
                Student existing = studentRepository.findById(longId).orElseThrow(() -> new RuntimeException("Studente non trovato con id: " + id));
                studentRepository.delete(existing);
                return;
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID non valido per DB locale: " + id);
            }
        }

        // Verifica che lo studente esista
        getStudentById(id);

        String query = String.format(
            "DELETE FROM `%s.%s.%s` WHERE id = @id",
            bigQuery.getOptions().getProjectId(),
            dataset,
            table
        );

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .setUseLegacySql(false)
                .addNamedParameter("id", QueryParameterValue.string(id))
                .build();

        try {
            bigQuery.query(queryConfig);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Errore durante l'eliminazione dello studente da BigQuery", e);
        }
    }

    /**
     * Mappa una riga di BigQuery a un DTO
     */
    private StudentDTO mapRowToDTO(FieldValueList row) {
        return new StudentDTO(
            row.get("id").getStringValue(),
            row.get("name").getStringValue(),
            row.get("surname").getStringValue(),
            row.get("email").getStringValue()
        );
    }

    /**
     * Esegue una query SQL su BigQuery e ritorna la lista dei valori della prima colonna
     */
    public List<String> runQuerySQL(String sql) {
        // Fallback: se BigQuery non è configurato, restituisci una risposta di default (es. data corrente)
        if (bigQuery == null || bigQuery.getOptions() == null || bigQuery.getOptions().getProjectId() == null || bigQuery.getOptions().getProjectId().isEmpty()) {
            return List.of(java.time.LocalDate.now().toString());
        }

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(sql)
                .setUseLegacySql(false)
                .build();

        try {
            TableResult result = bigQuery.query(queryConfig);
            List<String> values = new ArrayList<>();
            for (FieldValueList row : result.iterateAll()) {
                values.add(row.get(0).getValue().toString());
            }
            return values;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Errore durante l'esecuzione della query su BigQuery", e);
        }
    }

    /**
     * Ritorna il projectId configurato per BigQuery, o null se non disponibile
     */
    public String getProjectId() {
        if (bigQuery == null || bigQuery.getOptions() == null) return null;
        return bigQuery.getOptions().getProjectId();
    }

    /**
     * Metodo helper: inserisce uno studente su BigQuery se configurato, altrimenti nel DB locale
     */
    public boolean insertStudent(StudentRequest request, String dataset, String table) {
        try {
            createStudent(request);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}