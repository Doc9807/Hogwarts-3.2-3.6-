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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        return "http://localhost:" + port + "/student";
    }

    @BeforeEach
    public void cleanup() {
        studentRepository.deleteAll(); // Очистка базы данных перед каждым тестом
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

        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hermione Granger", response.getBody().getName());
        assertEquals(15, response.getBody().getAge());
    }

    @Test
    public void testGetStudentByIdNotFound() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/999", Student.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateStudent() {
        Student student = new Student();
        student.setName("Ron Weasley");
        student.setAge(15);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
        Long studentId = createResponse.getBody().getId();

        student.setName("Ron Weasley Updated");
        student.setAge(16);

        restTemplate.put(getBaseUrl() + "/" + studentId, student);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ron Weasley Updated", response.getBody().getName());
        assertEquals(16, response.getBody().getAge());
    }

    @Test
    public void testDeleteStudent() {
        Student student = new Student();
        student.setName("Neville Longbottom");
        student.setAge(15);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
        Long studentId = createResponse.getBody().getId();

        restTemplate.delete(getBaseUrl() + "/" + studentId);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId, Student.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllStudents() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(15);
        restTemplate.postForEntity(getBaseUrl(), student, Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                getBaseUrl(), Student[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Harry Potter", response.getBody()[0].getName());
    }

    @Test
    public void testGetStudentsByAgeBetween() {
        Student student1 = new Student();
        student1.setName("Harry Potter");
        student1.setAge(15);
        restTemplate.postForEntity(getBaseUrl(), student1, Student.class);

        Student student2 = new Student();
        student2.setName("Hermione Granger");
        student2.setAge(16);
        restTemplate.postForEntity(getBaseUrl(), student2, Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/ageBetween?minAge=14&maxAge=16", Student[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    public void testGetFacultyByStudentId() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);
        Faculty savedFaculty = facultyResponse.getBody();

        Student student = new Student();
        student.setName("Draco Malfoy");
        student.setAge(15);
        student.setFaculty(savedFaculty);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId + "/faculty", Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gryffindor", response.getBody().getName());
    }
}
