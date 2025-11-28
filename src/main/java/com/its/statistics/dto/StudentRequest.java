package com.its.statistics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Lombok removed to avoid build-time annotation processing issues; explicit methods provided

/**
 * DTO per ricevere i dati di uno studente nelle richieste POST/PUT
 * Usa Lombok per generare automaticamente getters, setters, toString, equals e hashCode
 * Include validazioni per garantire dati corretti
 */
public class StudentRequest {

    /**
     * Nome dello studente - obbligatorio, tra 2 e 100 caratteri
     */
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 2, max = 100, message = "Il nome deve essere tra 2 e 100 caratteri")
    private String name;

    /**
     * Cognome dello studente - obbligatorio, tra 2 e 100 caratteri
     */
    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(min = 2, max = 100, message = "Il cognome deve essere tra 2 e 100 caratteri")
    private String surname;

    /**
     * Email dello studente - obbligatoria e deve essere valida
     */
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email deve essere valida")
    private String email;

    public StudentRequest() {
    }

    public StudentRequest(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
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