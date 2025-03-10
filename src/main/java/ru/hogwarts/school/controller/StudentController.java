package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@Tag(name = "Student Management", description = "Operations related to student management")
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "Create a new student")
    @PostMapping
    public ResponseEntity<Student> createStudent(
            @Parameter(required = true) @RequestParam String name,
            @Parameter(required = true) @RequestParam int age) {
        Student createdStudent = studentService.createStudent(name, age);
        return ResponseEntity.ok(createdStudent);
    }

    @Operation(summary = "Get student by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update student information")
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam int age) {
        Student updatedStudent = studentService.updateStudent(id, name, age);
        if (updatedStudent != null) {
            return ResponseEntity.ok(updatedStudent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a student")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all students")
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Get students by age range")
    @GetMapping("/ageBetween")
    public ResponseEntity<List<Student>> getStudentsByAgeBetween(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        List<Student> students = studentService.getStudentsByAgeBetween(minAge, maxAge);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Get faculty by student ID")
    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getFacultyByStudentId(
            @Parameter(required = true) @PathVariable Long id) {
        Faculty faculty = studentService.getFacultyByStudentId(id);
        if (faculty != null) {
            return ResponseEntity.ok(faculty);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
