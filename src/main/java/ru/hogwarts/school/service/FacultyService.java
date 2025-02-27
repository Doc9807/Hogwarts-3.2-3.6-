package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> facultyMap = new HashMap<>();
    private Long nextId = 1L;

    public Faculty createFaculty(String name, String color) {
        Faculty faculty = new Faculty(nextId, name, color);
        facultyMap.put(nextId, faculty);
        nextId++;
        return faculty;
    }

    public Faculty getFaculty(Long id) {
        return facultyMap.get(id);
    }

    public Faculty updateFaculty(Long id, String name, String color) {
        Faculty faculty = facultyMap.get(id);
        if (faculty == null) {
            throw new EntityNotFoundException("Faculty not found with id: " + id);
        }
        faculty.setName(name);
        faculty.setColor(color);
        return faculty;
    }

    public void deleteFaculty(Long id) {
        if (!facultyMap.containsKey(id)) {
            throw new EntityNotFoundException("Faculty not found with id: " + id);
        }
        facultyMap.remove(id);
    }

    public List<Faculty> getAllFaculties() {
        return new ArrayList<>(facultyMap.values());
    }

    public List<Faculty> getFacultiesByColor(String color) {
        return facultyMap.values()
                .stream()
                .filter(faculty -> faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }
}
