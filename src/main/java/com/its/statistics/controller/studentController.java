package main.java.com.its.statistics.controller;

import model.Student;
import service.StudentService;
import dto.MergeRequestDTO; // Importa il DTO definito sopra
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import necessario per i codici di stato (400, 500)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Import consolidato per tutte le annotazioni
import java.util.List;

@RestController
@RequestMapping("/api/studenti")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // ********************************************************************
    // CRUD DI BASE
    // ********************************************************************

    // GET /api/studenti -> Elencare tutti
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }
    
    // GET /api/studenti/123 -> Visualizzare Singolo
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/studenti -> Inserire nuovo (Il Service gestirà la Normalizzazione/Validazione)
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.save(student);
    }
    
    // PUT /api/studenti/123 -> Modificare (Aggiornamento completo)
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        return studentService.findById(id)
            .map(existingStudent -> {
                // Mappa i campi aggiornati usando il Modello Canonico (CORSOITS, non solo corso)
                existingStudent.setNome(studentDetails.getNome());
                existingStudent.setCognome(studentDetails.getCognome());
                existingStudent.setCodiceFiscale(studentDetails.getCodiceFiscale()); 
                existingStudent.setCorsoITS(studentDetails.getCorsoITS()); // CORRETTO al nuovo campo
                existingStudent.setStatoCandidatura(studentDetails.getStatoCandidatura()); // Nuovo campo essenziale
                // TODO: Mappare qui tutti gli altri campi modificabili dall'utente.
                
                return ResponseEntity.ok(studentService.save(existingStudent));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/studenti/123 -> Eliminare
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // ********************************************************************
    // FUNZIONALITÀ AVANZATE
    // ********************************************************************

    /**
     * GET /api/studenti/ricerca?query=mario&corso=BAD&stato=ACCETTATO
     * Gestisce la ricerca per testo libero e i filtri avanzati.
     */
    @GetMapping("/ricerca")
    public List<Student> searchStudents(
            // La query generica (nome/cognome/matricola)
            @RequestParam(required = false) String query,
            // I filtri specifici richiesti per la gestione operativa
            @RequestParam(required = false) String corso,
            @RequestParam(required = false) String stato
    ) {
        // Chiama il Service per gestire la logica combinata di filtro e ricerca
        return studentService.filterAndSearch(query, corso, stato);
    }
    
    /**
     * POST /api/studenti/unisci -> Unire Contatti
     * Endpoint per la logica complessa di Merge.
     */
    @PostMapping("/unisci")
    public ResponseEntity<Student> mergeStudents(@RequestBody MergeRequestDTO mergeRequest) {
        try {
            // Controllo preliminare sul payload (minimo indispensabile)
            if (mergeRequest.getIdsToMerge() == null || mergeRequest.getIdsToMerge().isEmpty() || mergeRequest.getPrimaryData() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request
            }

            // Chiama la logica di merge nel Service
            // Si passa la lista degli ID da unire e l'oggetto Student con i dati finali e l'ID primario.
            Student merged = studentService.mergeStudents(
                mergeRequest.getIdsToMerge(), 
                mergeRequest.getPrimaryData()
            );
            
            return ResponseEntity.ok(merged);
            
        } catch (IllegalArgumentException e) {
            // Cattura eccezioni specifiche (es. Record primario non trovato) lanciate dal Service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        } catch (Exception e) {
            // Gestione di errori generici (es. problemi di connessione DB)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }
}