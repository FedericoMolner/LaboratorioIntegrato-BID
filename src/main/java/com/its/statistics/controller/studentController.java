package main.java.com.its.statistics.controller;
import model.Student;
import service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/studenti")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // GET /api/studenti -> Elencare tutti
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }
    
    // GET /api/studenti/ricerca?query=mario -> Filtrare e Ricercare
    @GetMapping("/ricerca")
    public List<Student> searchStudents(@RequestParam String query) {
        return studentService.searchStudents(query);
    }

    // GET /api/studenti/123 -> Visualizzare Singolo
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/studenti -> Inserire nuovo (o aggiornare se l'ID è nel body)
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.save(student);
    }
    
    // PUT /api/studenti/123 -> Modificare (Aggiornamento completo)
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        return studentService.findById(id)
            .map(existingStudent -> {
                // Mappa i campi dal DTO al record esistente
                existingStudent.setNome(studentDetails.getNome());
                existingStudent.setCorso(studentDetails.getCorso());
                // ... altri campi ...
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
    
    // POST /api/studenti/unisci -> Unire Contatti
    // Il payload sarà un DTO che contiene la lista di ID da unire e i dati principali
    @PostMapping("/unisci")
    public ResponseEntity<Student> mergeStudents(@RequestBody MergeRequestDTO mergeRequest) {
        // Qui si userebbe mergeRequest.getIdsToMerge() e mergeRequest.getPrimaryData()
        Student merged = studentService.mergeStudents(mergeRequest.getIdsToMerge(), mergeRequest.getPrimaryData());
        return ResponseEntity.ok(merged);
    }
    
    // Nota: È necessario creare un DTO (Data Transfer Object) separato per la richiesta di Merge.
}