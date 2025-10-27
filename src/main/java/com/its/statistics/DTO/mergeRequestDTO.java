package main.java.com.its.statistics.DTO;

import model.Student;
import java.util.List;

/**
 * Data Transfer Object (DTO) utilizzato dall'endpoint POST /api/studenti/unisci
 * per trasportare i dati necessari all'operazione di merge dei contatti.
 */
public class MergeRequestDTO {
    
    // 1. Lista degli ID di tutti i record coinvolti (sia primario che secondari/da eliminare)
    private List<Long> idsToMerge;
    
    // 2. L'oggetto Studente che contiene i dati finali desiderati
    // Questo oggetto deve contenere l'ID del record da mantenere (primaryStudentId)
    // e tutti i campi aggiornati (nome, corso, ecc.)
    private Student primaryData; 
    
    // ********************************************************************
    // COSTRUTTORE VUOTO (richiesto da Jackson/Spring per la deserializzazione JSON)
    // ********************************************************************
    public MergeRequestDTO() {
    }

    // ********************************************************************
    // GETTERS E SETTERS (Obbligatori per l'accesso da parte del Controller)
    // ********************************************************************

    public List<Long> getIdsToMerge() {
        return idsToMerge;
    }

    public void setIdsToMerge(List<Long> idsToMerge) {
        this.idsToMerge = idsToMerge;
    }

    public Student getPrimaryData() {
        return primaryData;
    }

    public void setPrimaryData(Student primaryData) {
        this.primaryData = primaryData;
    }
}