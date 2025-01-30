package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FacultyService {
    private final Map<Long, Faculty> facultyMap = new HashMap<>();
    private Long idCounter = 1L;

    public Faculty createFaculty(String name, String color) {
        Faculty faculty = new Faculty(idCounter, name, color);
        facultyMap.put(idCounter, faculty);
        idCounter++;
        return faculty;
    }

    public Faculty getFaculty(Long id) {
        return facultyMap.get(id);
    }

    public Faculty updateFaculty(Long id, String name, String color) {
        Faculty faculty = facultyMap.get(id);
        if (faculty != null) {
            faculty.setName(name);
            faculty.setColor(color);
            return faculty;
        }
        return null;
    }

    public void deleteFaculty(Long id) {
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
