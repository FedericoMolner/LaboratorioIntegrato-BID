package repository;

import model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// JpaRepository<[Entity Class], [Primary Key Type]>
public interface StudentRepo extends JpaRepository<Student, Long> {
    
    // Metodo personalizzato per la funzionalità "Filtra e Ricerca"
    // Spring Data JPA crea automaticamente la query SQL da questo nome di metodo
    List<Student> findByCorso(String corso);
    
    // Metodo per cercare per parte del nome/cognome (utile per la ricerca)
    List<Student> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);
}