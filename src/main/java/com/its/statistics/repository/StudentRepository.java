package main.java.com.its.statistics.repository;

import model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // Import necessario per le ricerche esatte (come findByCodiceFiscale)

@Repository
// JpaRepository<[Entity Class], [Primary Key Type]>
public interface StudentRepository extends JpaRepository<Student, Long> {

    // 1. RICERCA GENERICO (Per il campo 'query' nel Service)
    // Cerca per parte del nome o del cognome (ignora maiuscole/minuscole)
    // Usa i campi aggiornati: getNome() e getCognome()
    List<Student> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);

    // 2. FILTRI PER LA GESTIONE OPERATIVA (Usati da Power Apps)
    
    // Filtra per il campo standardizzato del Corso ITS
    List<Student> findByCorsoITS(String corsoITS);
    
    // Filtra per il campo standardizzato dello Stato Candidatura
    List<Student> findByStatoCandidatura(String statoCandidatura);

    // 3. RICERCA PER IDENTIFICATIVO (Cruciale per il MERGE e la VALIDAZIONE)

    // Ricerca esatta per Codice Fiscale (deve tornare zero o un risultato)
    // Optional gestisce il caso in cui non venga trovato
    Optional<Student> findByCodiceFiscale(String codiceFiscale);
    
    // Ricerca per email (utile per l'identificazione di duplicati)
    Optional<Student> findByEmail(String email);

    // Ricerca per Numero di Matricola (se usato come riferimento unico)
    Optional<Student> findByNumeroMatricola(String numeroMatricola);
}