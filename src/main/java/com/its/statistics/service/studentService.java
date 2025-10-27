package service;

import model.Student;
import repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import per la gestione delle transazioni
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Import per la gestione delle liste

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    // ********************************************************************
    // 1. OPERAZIONI CRUD DI BASE
    // ********************************************************************

    public List<Student> findAll() {
        return studentRepo.findAll();
    }

    public Optional<Student> findById(Long id) {
        return studentRepo.findById(id);
    }

    /**
     * Salva o aggiorna un record, applicando la normalizzazione e la validazione.
     */
    @Transactional // Aggiunto! Gestisce la transazione del DB
    public Student save(Student student) {
        // PASSO CRUCIALE: Normalizzazione e Validazione
        Student normalizedStudent = normalizeAndValidate(student); 

        // TODO: Aggiungere qui la logica per il controllo di CF unico prima di salvare
        
        return studentRepo.save(normalizedStudent);
    }
    
    @Transactional // Aggiunto!
    public void deleteById(Long id) {
        studentRepo.deleteById(id);
    }
    
    // ********************************************************************
    // 2. LOGICA DI NORMALIZZAZIONE E VALIDAZIONE (Aggiunto!)
    // ********************************************************************
    
    /**
     * Normalizza e valida i dati in ingresso secondo il Modello Canonico.
     */
    private Student normalizeAndValidate(Student student) {
        // Esempio 1: Pulizia e standardizzazione dell'identificativo
        if (student.getCodiceFiscale() != null) {
            student.setCodiceFiscale(student.getCodiceFiscale().toUpperCase().trim());
        }
        if (student.getEmail() != null) {
            student.setEmail(student.getEmail().toLowerCase().trim());
        }

        // Esempio 2: Normalizzazione dei campi di classificazione (Corso, Stato)
        if (student.getStatoCandidatura() != null) {
            String stato = student.getStatoCandidatura().toUpperCase().trim();
            // Mappatura di valori incoerenti a un valore standard
            if (stato.equals("SI") || stato.contains("ACCETT")) {
                student.setStatoCandidatura("ACCETTATO");
            } else if (stato.equals("NO") || stato.contains("RIFIUT")) {
                student.setStatoCandidatura("RIFIUTATO");
            }
            // TODO: Mappare anche CorsoITS e SedeCorso
        }

        // TODO: Aggiungere qui la validazione che lancia un'eccezione se i dati sono invalidi
        // Esempio: if (student.getNome() == null || student.getCognome() == null) throw new ValidationException("Nome e Cognome obbligatori.");

        return student;
    }


    // ********************************************************************
    // 3. FUNZIONALITÀ AVANZATE
    // ********************************************************************

    // 5. Filtrare/Ricercare (Richiede l'implementazione nel Repository)
    public List<Student> searchStudents(String query) {
        // Implementazione semplice della ricerca usando l'OR sui campi
        // Questo richiede che StudentRepo abbia il metodo corretto
        return studentRepo.findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(query, query);
    }

    // 6. Logica Cruciale: Unire (Merge) i contatti
    /**
     * Unisce più record identificati come duplicati in un singolo record.
     * @param idsToMerge ID dei record coinvolti nel merge.
     * @param datiPrincipali L'entità Student che rappresenta il record finale, con l'ID del record primario.
     * @return Il record studente consolidato e salvato.
     */
    @Transactional // Aggiunto! La logica deve essere atomica (o tutto o niente)
    public Student mergeStudents(List<Long> idsToMerge, Student datiPrincipali) {
        
        if (idsToMerge == null || datiPrincipali == null || datiPrincipali.getId() == null) {
            // Lancia un'eccezione chiara se i parametri non sono validi
            throw new IllegalArgumentException("L'ID del record primario e la lista degli ID da unire sono obbligatori.");
        }

        // 1. Trova il record primario da mantenere
        // Si suppone che datiPrincipali contenga l'ID del record principale esistente
        Student primaryStudent = studentRepo.findById(datiPrincipali.getId())
                .orElseThrow(() -> new IllegalArgumentException("Record primario con ID " + datiPrincipali.getId() + " non trovato."));

        // 2. Identifica i record secondari (quelli da eliminare)
        List<Long> secondaryIds = idsToMerge.stream()
                .filter(id -> !id.equals(primaryStudent.getId())) // Assicurati di non eliminare il record principale
                .collect(Collectors.toList());

        // 3. Consolidamento Dati (Logica da definire)
        List<Student> secondaryStudents = studentRepo.findAllById(secondaryIds);
        
        // Esempio di logica di consolidamento: prende i campi non nulli dai record secondari
        for (Student secondary : secondaryStudents) {
            // Se il telefono primario è vuoto, prendi quello del secondario
            if (primaryStudent.getTelefono() == null && secondary.getTelefono() != null) {
                 primaryStudent.setTelefono(secondary.getTelefono());
            }
            // Se l'email del record principale è diversa, potresti registrarla in una nota (logica complessa)
        }
        
        // 4. Aggiorna il record principale con i dati inviati da BAD/Controller
        // Questo metodo usa i dati passati nel parametro `datiPrincipali` per aggiornare l'entità primaria
        primaryStudent.setNome(datiPrincipali.getNome());
        primaryStudent.setCorsoITS(datiPrincipali.getCorsoITS());
        // ... aggiorna tutti i campi che possono essere modificati dall'utente.

        // 5. Salva il record primario consolidato (che ora è il risultato del merge)
        Student mergedStudent = studentRepo.save(primaryStudent);

        // 6. Elimina i record secondari (Duplicati)
        studentRepo.deleteAllById(secondaryIds);
        
        return mergedStudent;
    }
}