package model;

import javax.persistence.Entity;          // Per l'annotazione @Entity
import jakarta.persistence.GeneratedValue;   // Per l'annotazione @GeneratedValue
import jakarta.persistence.GenerationType; // Per GenerationType
import jakarta.persistence.Id;             // Per l'annotazione @Id

// Sostituisci "student" con il nome della tua tabella se diverso
@Entity 
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chiave primaria, generata dal DB

    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String corso;
    private Integer annoIscrizione;

    // Costruttori
    public Student() {
    }

    public Student(String nome, String cognome, String codiceFiscale, String email, String corso, Integer annoIscrizione) {
        this.nome