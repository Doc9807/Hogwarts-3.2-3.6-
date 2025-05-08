package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private static final int REQUIRED_STUDENTS_COUNT = 6;

    @Transactional
    public Student createStudent(Student student) {
        log.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        log.info("Was invoked method for get student with id = {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("There is not student with id = {}", id);
                    return new EntityNotFoundException("Student not found with id: " + id);
                });
    }

    @Transactional
    public Student updateStudent(Long id, Student student) {
        log.info("Was invoked method for update student with id = {}", id);
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("There is not student with id = {}", id);
                    return new EntityNotFoundException("Student not found with id: " + id);
                });
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        existingStudent.setFaculty(student.getFaculty());
        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        log.info("Was invoked method for delete student with id = {}", id);
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        log.info("Was invoked method for get all students");
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        log.info("Was invoked method for get students by age between {} and {}", minAge, maxAge);
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        log.info("Was invoked method for get faculty by student id = {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("There is not student with id = {}", studentId);
                    return new EntityNotFoundException("Student not found with id: " + studentId);
                });
        if (student.getFaculty() == null) {
            log.error("Faculty not found for student id = {}", studentId);
            throw new EntityNotFoundException("Faculty not found for student id: " + studentId);
        }
        return student.getFaculty();
    }

    public Integer countAllStudents() {
        log.info("Was invoked method for count all students");
        return studentRepository.countAllStudents();
    }

    public Double findAverageAge() {
        log.info("Was invoked method for find average age of students");
        return studentRepository.findAverageAge();
    }

    public List<Student> findLastFiveStudents() {
        log.info("Was invoked method for find last five students");
        return studentRepository.findLastFiveStudents(PageRequest.of(0, 5));
    }

    public List<String> getStudentNamesStartingWithA() {
        log.info("Getting student names starting with 'A'");
        return studentRepository.findAllNamesStartingWithA();
    }

    public List<String> getFirstSixStudentsNames() {
        log.info("Was invoked method for get first six students names");
        return studentRepository.findAll().stream()
                .limit(REQUIRED_STUDENTS_COUNT)
                .map(Student::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public void printStudentsParallel() {
        log.info("Was invoked method for print students in parallel");
        List<String> names = getFirstSixStudentsNames();
        validateNamesSize(names);

        System.out.println(Thread.currentThread().getName() + ": " + names.get(0));
        System.out.println(Thread.currentThread().getName() + ": " + names.get(1));

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + names.get(2));
            System.out.println(Thread.currentThread().getName() + ": " + names.get(3));
        }).start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + names.get(4));
            System.out.println(Thread.currentThread().getName() + ": " + names.get(5));
        }).start();
    }

    @Transactional
    public void printStudentsSynchronized() {
        log.info("Was invoked method for print students synchronized");
        List<String> names = getFirstSixStudentsNames();
        validateNamesSize(names);

        printNamesSynchronized(names.subList(0, 2));

        new Thread(() -> printNamesSynchronized(names.subList(2, 4))).start();

        new Thread(() -> printNamesSynchronized(names.subList(4, 6))).start();
    }

    private synchronized void printNamesSynchronized(List<String> names) {
        names.forEach(name ->
                System.out.println(Thread.currentThread().getName() + ": " + name)
        );
    }

    private void validateNamesSize(List<String> names) {
        if (names.size() < REQUIRED_STUDENTS_COUNT) {
            log.error("Not enough students (required {}, got {})", REQUIRED_STUDENTS_COUNT, names.size());
            throw new IllegalArgumentException("Need at least " + REQUIRED_STUDENTS_COUNT + " students");
        }
    }
}
