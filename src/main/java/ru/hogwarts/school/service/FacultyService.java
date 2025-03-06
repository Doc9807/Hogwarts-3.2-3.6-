package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(String name, String color) {
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + id));
    }

    public Faculty updateFaculty(Long id, String name, String color) {
        Faculty faculty = facultyRepository.
                findById(id).
                orElse(null);
        if (faculty != null) {
            faculty.setName(name);
            faculty.setColor(color);
            return facultyRepository.save(faculty);
        }
        return null;
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public List<Faculty> getFacultiesByNameOrColor(String nameOrColor) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }
//    Тут я не совсем уверен, по идее мне следовало сделать в getFacultiesByNameOrColor вот так:
//  public List<Faculty> getFacultiesByNameOrColor(String name, String color) {
//    if (name != null && color != null) {
//        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
//    } else if (name != null) {
//        return facultyRepository.findByNameIgnoreCase(name);
//    } else if (color != null) {
//        return facultyRepository.findByColorIgnoreCase(color);
//    } else {
//        return Collections.emptyList();
//    }
//}
//    Однако мне попался способ упростить данное действо и мне показался вариант в 52-54 гениальным
//     и более простым, но если надо, то усложню.

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() ->
                new EntityNotFoundException("Faculty not found with id: " + facultyId));
        return faculty.getStudents();
    }
}
