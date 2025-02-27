package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> studentMap = new HashMap<>();
    private Long nextId = 1L;

    public Student createStudent(String name, int age) {
        Student student = new Student(nextId, name, age);
        studentMap.put(nextId, student);
        nextId++;
        return student;
    }

    public Student getStudent(Long id) {
        return studentMap.get(id);
    }

    public Student updateStudent(Long id, String name, int age) {
        Student student = studentMap.get(id);
        if (student != null) {
            student.setName(name);
            student.setAge(age);
            return student;
        }
        return null;
    }

    public void deleteStudent(Long id) {
        studentMap.remove(id);
    }

    public List<Student> getStudentsByAge(int age) {
        return studentMap.values()
                .stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentMap.values());
    }
}