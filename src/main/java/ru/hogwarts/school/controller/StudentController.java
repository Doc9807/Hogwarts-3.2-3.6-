package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@Tag(name = "Student Management",
        description = "Operations related to student management")
@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Create a new student")
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.ok(createdStudent);
    }

    @Operation(summary = "Get student by ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @Operation(summary = "Update student information")
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
                                                 @Valid @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updatedStudent);
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
        return ResponseEntity.ok(faculty);
    }

    @Operation(summary = "Get the total number of students")
    @GetMapping("/count")
    public ResponseEntity<Integer> getStudentCount() {
        Integer count = studentService.countAllStudents();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get the average age of students")
    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge() {
        Double averageAge = studentService.findAverageAge();
        return ResponseEntity.ok(averageAge);
    }

    @Operation(summary = "Get the last five students")
    @GetMapping("/last-five")
    public ResponseEntity<List<Student>> getLastFiveStudents() {
        List<Student> students = studentService.findLastFiveStudents();
        return ResponseEntity.ok(students);
    }

    @Operation(
            summary = "Get student names starting with A",
            description = "Returns sorted list of uppercase names starting with 'А'"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of names found",
            content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))
    )
    @GetMapping("/names-starting-with-a")
    public ResponseEntity<List<String>> getNamesStartingWithA() {
        return ResponseEntity.ok(studentService.getStudentNamesStartingWithA());
    }

    @Operation(
            summary = "Print student names in parallel threads",
            description = "Prints first 6 student names using parallel threads"
    )
    @GetMapping("/print-parallel")
    public ResponseEntity<String> printParallel() {
        studentService.printStudentsParallel();
        return ResponseEntity.ok("Printing student names in parallel mode started");
    }

    @Operation(
            summary = "Print student names with synchronization",
            description = "Prints first 6 student names using synchronized threads"
    )
    @GetMapping("/print-synchronized")
    public ResponseEntity<String> printSynchronized() {
        studentService.printStudentsSynchronized();
        return ResponseEntity.ok("Printing student names in synchronized mode started");
    }
}
