package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile avatar) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        String filePath = "avatars/" + studentId + "_" + avatar.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, avatar.getBytes());

        Avatar studentAvatar = new Avatar();
        studentAvatar.setFilePath(filePath);
        studentAvatar.setFileSize(avatar.getSize());
        studentAvatar.setMediaType(avatar.getContentType());
        studentAvatar.setData(avatar.getBytes());
        studentAvatar.setStudent(student);

        return avatarRepository.save(studentAvatar);
    }

    public Avatar getAvatarByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found with id: " + studentId));
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Avatar not found for student id: " + studentId));
    }
}
