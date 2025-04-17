package ru.hogwarts.school.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAgeBetween(int minAge, int maxAge);

    @Query("SELECT COUNT(s) FROM Student s")
    Integer countAllStudents();

    @Query("SELECT AVG(s.age) FROM Student s")
    Double findAverageAge();

    @Query("SELECT s FROM Student s ORDER BY s.id DESC")
    List<Student> findLastFiveStudents(Pageable pageable);

    @Query("SELECT s.name FROM Student s WHERE UPPER(s.name) LIKE 'А%' ORDER BY s.name")
    List<String> findAllNamesStartingWithA();
}
