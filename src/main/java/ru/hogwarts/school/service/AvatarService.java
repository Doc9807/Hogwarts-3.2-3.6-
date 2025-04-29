package ru.hogwarts.school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AvatarService {
    private static final long MAX_FILE_SIZE = 1024 * 300; // 300KB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".gif"
    );

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile avatar) throws IOException {
        log.info("Uploading avatar for student id: {}", studentId);

        validateAvatarFile(avatar);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Student not found with id: {}", studentId);
                    return new EntityNotFoundException("Student not found with id: " + studentId);
                });

        String safeFilename = generateSafeFilename(studentId, avatar.getOriginalFilename());
        String filePath = "avatars/" + safeFilename;

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

    @Async("taskExecutor")
    public CompletableFuture<Avatar> uploadAvatarAsync(Long studentId, MultipartFile avatar) {
        try {
            return CompletableFuture.completedFuture(uploadAvatar(studentId, avatar));
        } catch (IOException e) {
            CompletableFuture<Avatar> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Cacheable(value = "avatars", key = "#studentId")
    public Avatar getAvatarByStudentId(Long studentId) {
        log.info("Getting avatar for student id: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Student not found with id: {}", studentId);
                    return new EntityNotFoundException("Student not found with id: " + studentId);
                });

        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    log.error("Avatar not found for student id: {}", studentId);
                    return new EntityNotFoundException("Avatar not found for student id: " + studentId);
                });
    }

    public Page<Avatar> getAvatars(int page, int size) {
        log.info("Getting avatars page: {}, size: {}", page, size);
        return avatarRepository.findAll(PageRequest.of(page, size));
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("File size too large: {} bytes", file.getSize());
            throw new IllegalArgumentException("File size exceeds maximum limit of 300KB");
        }

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            log.warn("Invalid file type: {}", file.getContentType());
            throw new IllegalArgumentException("Only JPG, PNG and GIF images are allowed");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || ALLOWED_EXTENSIONS.stream().noneMatch(filename::endsWith)) {
            log.warn("Invalid file extension: {}", filename);
            throw new IllegalArgumentException("Invalid file extension. Allowed: .jpg, .jpeg, .png, .gif");
        }
    }

    private String generateSafeFilename(Long studentId, String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "avatar_" + studentId + "_" + UUID.randomUUID() + extension;
    }
}
