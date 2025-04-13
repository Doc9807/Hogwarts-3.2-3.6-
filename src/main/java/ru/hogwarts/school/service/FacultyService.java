package ru.hogwarts.school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

@Service
@Slf4j
public class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(String name, String color) {
        log.info("Was invoked method for create faculty with name: {} and color: {}", name, color);
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        log.info("Was invoked method for get faculty with id: {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Faculty not found with id: {}", id);
                    return new EntityNotFoundException("Faculty not found with id: " + id);
                });
    }

    public Faculty updateFaculty(Long id, String name, String color) {
        log.info("Was invoked method for update faculty with id: {}", id);
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Faculty not found with id: {}", id);
                    return new EntityNotFoundException("Faculty not found with id: " + id);
                });
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        log.info("Was invoked method for delete faculty with id: {}", id);
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        log.info("Was invoked method for get all faculties");
        return facultyRepository.findAll();
    }

    public List<Faculty> getFacultiesByNameOrColor(String nameOrColor) {
        log.info("Was invoked method for get faculties by name or color: {}", nameOrColor);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        log.info("Was invoked method for get students by faculty id: {}", facultyId);
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> {
                    log.error("Faculty not found with id: {}", facultyId);
                    return new EntityNotFoundException("Faculty not found with id: " + facultyId);
                });
        return faculty.getStudents();
    }
}