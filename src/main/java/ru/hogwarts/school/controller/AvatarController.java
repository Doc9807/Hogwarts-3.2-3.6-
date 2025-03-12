package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@Tag(name = "Avatar Management", description = "Operations related to avatar management")
@RestController
@RequestMapping("/avatar")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @Operation(summary = "Upload avatar for a student")
    @PostMapping("/upload/{studentId}")
    public ResponseEntity<Avatar> uploadAvatar(
            @PathVariable Long studentId,
            @RequestParam MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().build();
        }

        Avatar avatar = avatarService.uploadAvatar(studentId, file);
        return ResponseEntity.ok(avatar);
    }

    @Operation(summary = "Get avatar from database")
    @GetMapping("/db/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatarByStudentId(studentId);
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(avatar.getMediaType()))
                .body(avatar.getData());
    }

    @Operation(summary = "Get avatar from directory")
    @GetMapping("/file/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) throws IOException {
        Avatar avatar = avatarService.getAvatarByStudentId(studentId);

        java.nio.file.Path path = java.nio.file.Paths.get(avatar.getFilePath());
        if (!java.nio.file.Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = java.nio.file.Files.readAllBytes(path);
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(avatar.getMediaType()))
                .body(data);
    }
}
