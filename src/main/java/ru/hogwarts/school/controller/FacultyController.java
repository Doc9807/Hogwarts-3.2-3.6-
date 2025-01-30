package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestParam String name, @RequestParam String color) {
        Faculty createdFaculty = facultyService.createFaculty(name, color);
        return ResponseEntity.ok(createdFaculty);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Long id) {
        Faculty faculty = facultyService.getFaculty(id);
        if (faculty != null) {
            return ResponseEntity.ok(faculty);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @RequestParam String name, @RequestParam String color) {
        Faculty updatedFaculty = facultyService.updateFaculty(id, name, color);
        if (updatedFaculty != null) {
            return ResponseEntity.ok(updatedFaculty);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        List<Faculty> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    @GetMapping("/filterByColor")
    public ResponseEntity<List<Faculty>> getFacultiesByColor(@RequestParam String color) {
        List<Faculty> faculties = facultyService.getFacultiesByColor(color);
        return ResponseEntity.ok(faculties);
    }
}