package ru.hogwarts.school.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/students";
    }

    @BeforeEach
    public void cleanup() {
        studentRepository.deleteAll();
    }

    @Test
    public void testCreateStudent() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(15);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Harry Potter", response.getBody().getName());
        assertEquals(15, response.getBody().getAge());
    }

    @Test
    public void testGetStudentById() {
        Student student = new Student();
        student.setName("Hermione Granger");
        student.setAge(15);
        Student created = studentRepository.save(student);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + created.getId(), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hermione Granger", response.getBody().getName());
    }

    @Test
    public void testUpdateStudent() {
        Student student = new Student();
        student.setName("Ron Weasley");
        student.setAge(15);
        Student created = studentRepository.save(student);

        student.setName("Ron Weasley Updated");
        student.setAge(16);

        restTemplate.put(getBaseUrl() + "/" + created.getId(), student);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + created.getId(), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ron Weasley Updated", response.getBody().getName());
    }

    @Test
    public void testDeleteStudent() {
        Student student = new Student();
        student.setName("Neville Longbottom");
        student.setAge(15);
        Student created = studentRepository.save(student);

        restTemplate.delete(getBaseUrl() + "/" + created.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + created.getId(), Student.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllStudents() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(15);
        studentRepository.save(student);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                getBaseUrl(), Student[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        assertEquals("Harry Potter", response.getBody()[0].getName());
    }

    @Test
    public void testGetFacultyByStudentId() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties",
                faculty,
                Faculty.class
        );
        Faculty savedFaculty = facultyResponse.getBody();

        Student student = new Student();
        student.setName("Draco Malfoy");
        student.setAge(15);
        student.setFaculty(savedFaculty);

        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(
                getBaseUrl(),
                student,
                Student.class
        );
        Student created = studentResponse.getBody();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + created.getId() + "/faculty",
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gryffindor", response.getBody().getName());
        assertEquals("Red", response.getBody().getColor());
    }

    @Test
    public void testPrintParallel_Success() {
        List<Student> students = IntStream.range(0, 6)
                .mapToObj(i -> {
                    Student s = new Student();
                    s.setName("Student " + i);
                    s.setAge(11 + i);
                    return s;
                })
                .collect(Collectors.toList());
        studentRepository.saveAll(students);

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/print-parallel", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Printing student names in parallel mode started", response.getBody());
    }

    @Test
    public void testPrintParallel_NotEnoughStudents() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/print-parallel", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testPrintSynchronized_Success() {
        List<Student> students = IntStream.range(0, 6)
                .mapToObj(i -> {
                    Student s = new Student();
                    s.setName("Student " + i);
                    s.setAge(11 + i);
                    return s;
                })
                .collect(Collectors.toList());
        studentRepository.saveAll(students);

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/print-synchronized", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Printing student names in synchronized mode started", response.getBody());
    }
}
