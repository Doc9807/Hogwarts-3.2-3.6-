package ru.hogwarts.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @Min(value = 11, message = "Age must be at least 11")
    private int age;
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "student")
    private Avatar avatar;

    public Student() {
    }

    public Student(Long id, String name, int age,
                   Faculty faculty) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.faculty = faculty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age
                && Objects.equals(id, student.id)
                && Objects.equals(name, student.name)
                && Objects.equals(faculty, student.faculty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, faculty);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", faculty=" + (faculty != null ? faculty.getName() : "null") +
                '}';
    }
}
