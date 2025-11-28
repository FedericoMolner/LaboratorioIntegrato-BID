package com.its.statistics.dto;

// Lombok removed to avoid build-time annotation processing issues; explicit methods provided

/**
 * DTO per rappresentare uno studente nelle risposte API
 * Usa Lombok per generare automaticamente getters, setters, toString, equals e hashCode
 */
public class StudentDTO {
    
    /**
     * ID dello studente (UUID generato automaticamente)
     * Cambiato da Long a String per compatibilità con BigQuery
     */
    private String id;
    
    /**
     * Nome dello studente
     */
    private String name;
    
    /**
     * Cognome dello studente
     */
    private String surname;
    
    /**
     * Email dello studente
     */
    private String email;

    public StudentDTO() {
    }

    public StudentDTO(String id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}