package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found with id: " + id));
    }

    @Transactional
    public Student updateStudent(Long id, Student student) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException
                                ("Student not found with id: " + id));
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        existingStudent.setFaculty(student.getFaculty());
        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new EntityNotFoundException
                                ("Student not found with id: " + studentId));
        if (student.getFaculty() == null) {
            throw new EntityNotFoundException
                    ("Faculty not found for student id: " + studentId);
        }
        return student.getFaculty();
    }

    public Integer countAllStudents() {
        return studentRepository.countAllStudents();
    }

    public Double findAverageAge() {
        return studentRepository.findAverageAge();
    }

    public List<Student> findLastFiveStudents() {
        return studentRepository.findLastFiveStudents
                (PageRequest.of(0, 5));
    }
}
