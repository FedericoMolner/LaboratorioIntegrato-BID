package service;

import model.Student;
import repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    // 1. Elencare/Visualizzare tutti
    public List<Student> findAll() {
        return studentRepo.findAll();
    }

    // 2. Visualizzare singolo
    public Optional<Student> findById(Long id) {
        return studentRepo.findById(id);
    }
    
    // 3. Inserire/Modificare
    public Student save(Student student) {
        // Aggiungere logica di validazione qui (es. controllo CF unico)
        return studentRepo.save(student);
    }
    
    // 4. Eliminare
    public void deleteById(Long id) {
        studentRepo.deleteById(id);
    }
    
    // 5. Filtrare/Ricercare
    public List<Student> searchStudents(String query) {
        // Implementazione semplice della ricerca usando l'OR sui campi
        return studentRepo.findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(query, query);
    }

    // 6. Logica Cruciale: Unire (Merge) i contatti
    public Student mergeStudents(List<Long> idsToMerge, Student datiPrincipali) {
        // Implementazione complessa:
        // 1. Trovare tutti i record da unire (studentRepo.findAllById(idsToMerge)).
        // 2. Scegliere o creare il record principale (usando 'datiPrincipali').
        // 3. Eliminare i record secondari dal DB.
        // 4. Aggiornare e salvare il record principale con i dati uniti.
        
        // Questo è il punto in cui collaborare con BAD per definire le regole di merge!
        return studentRepo.save(datiPrincipali);
    }
}