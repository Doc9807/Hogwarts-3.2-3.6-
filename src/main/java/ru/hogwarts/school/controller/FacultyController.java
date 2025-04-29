package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@Tag(name = "Faculty Management", description = "Operations related to faculty management")
@RestController
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Operation(summary = "Create a new faculty")
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@Valid @RequestBody Faculty faculty) {
        Faculty createdFaculty = facultyService.createFaculty(faculty.getName(), faculty.getColor());
        return ResponseEntity.ok(createdFaculty);
    }

    @Operation(summary = "Get faculty by ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Faculty getFacultyById(@PathVariable Long id) {
        Faculty faculty = facultyService.getFaculty(id);
        if (faculty == null) {
            throw new EntityNotFoundException("Faculty not found");
        }
        return faculty;
    }

    @Operation(summary = "Update an existing faculty")
    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id,
                                                 @Valid @RequestBody Faculty faculty) {
        Faculty updatedFaculty = facultyService.updateFaculty(id, faculty.getName(), faculty.getColor());
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

    @Operation(
            summary = "Get longest faculty name",
            description = "Returns the name of faculty with maximum length"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Longest faculty name found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No faculties available",
                    content = @Content
            )
    })
    @GetMapping("/longest-name")
    public ResponseEntity<String> getLongestFacultyName() {
        return ResponseEntity.ok(facultyService.getLongestFacultyName());
    }
}