package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "Faculty API",
                version = "1.0.0"
        )
)
@RestController
@RequestMapping("/faculty")
@Tag(name = "Faculty Management", description = "Operations related to faculty management")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Operation(summary = "Create a new faculty")
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@Parameter(required = true)
                                                 @RequestParam String name,
                                                 @Parameter(required = true)
                                                 @RequestParam String color) {
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

    @Operation(summary = "Get faculties by color")
    @GetMapping("/filterByColor")
    public ResponseEntity<List<Faculty>> getFacultiesByColor(@RequestParam String color) {
        List<Faculty> faculties = facultyService.getFacultiesByColor(color);
        return ResponseEntity.ok(faculties);
    }
}
