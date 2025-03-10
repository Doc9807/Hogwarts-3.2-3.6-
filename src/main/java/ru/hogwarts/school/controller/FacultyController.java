package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
@Tag(name = "Faculty Management", description = "Operations related to faculty management")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Operation(summary = "Create a new faculty")
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(
            @Parameter(required = true) @RequestParam String name,
            @Parameter(required = true) @RequestParam String color) {
        Faculty createdFaculty = facultyService.createFaculty(name, color);
        return ResponseEntity.ok(createdFaculty);
    }

    @Operation(summary = "Get faculty by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Long id) {
        Faculty faculty = facultyService.getFaculty(id);
        System.out.println("Вошли");
        if (faculty != null) {
            return ResponseEntity.ok(faculty);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update an existing faculty")
    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id,
                                                 @RequestParam String name,
                                                 @RequestParam String color) {
        Faculty updatedFaculty = facultyService.updateFaculty(id, name, color);
        if (updatedFaculty != null) {
            return ResponseEntity.ok(updatedFaculty);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a faculty")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all faculties")
    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        List<Faculty> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    @Operation(summary = "Get faculties by name or color (case-insensitive)")
    @GetMapping("/search")
    public ResponseEntity<List<Faculty>> getFacultiesByNameOrColor
            (@RequestParam String nameOrColor) {
        List<Faculty> faculties = facultyService.getFacultiesByNameOrColor(nameOrColor);
        return ResponseEntity.ok(faculties);
    }

    @Operation(summary = "Get students by faculty ID")
    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsByFacultyId(@PathVariable Long id) {
        List<Student> students = facultyService.getStudentsByFacultyId(id);
        if (students != null && !students.isEmpty()) {
            return ResponseEntity.ok(students);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
