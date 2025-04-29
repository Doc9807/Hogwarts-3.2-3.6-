package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.dto.MyApiResponse;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Avatar Management", description = "Endpoints for managing student avatars")
@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private static final long MAX_AVATAR_SIZE_KB = 300;
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/gif");
    private static final String ALLOWED_TYPES_STRING = "JPEG, PNG, GIF";

    private static final String EXAMPLE_RESPONSE = """
            {
                "id": 1,
                "filePath": "avatars/1_avatar.jpg",
                "mediaType": "image/jpeg",
                "fileSize": 12345,
                "student": {
                    "id": 1,
                    "name": "John Doe"
                }
            }
            """;

    private final AvatarService avatarService;

    @Autowired
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Operation(
            summary = "Upload avatar for student",
            description = "Max file size: 300KB, Allowed types: " + ALLOWED_TYPES_STRING,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Avatar uploaded successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Avatar.class),
                                    examples = @ExampleObject(value = EXAMPLE_RESPONSE)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file (size/type)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Student not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode= "500",
                            description= "Internal server error",
                            content= @Content(
                                    mediaType= MediaType.APPLICATION_JSON_VALUE,
                                    schema= @Schema(implementation= ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping(value="/{studentId}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Avatar>> uploadAvatar(
            @Parameter(description="ID of student", required=true, example="1")
            @PathVariable Long studentId,
            @Parameter(description="Avatar image file (max 300KB)", required=true)
            @RequestParam MultipartFile file) {

        try {
            if (file.getSize() > MAX_AVATAR_SIZE_KB * 1024) {
                throw new IllegalArgumentException("File size exceeds maximum limit of 300KB");
            }
            if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
                throw new IllegalArgumentException("Only JPG, PNG and GIF images are allowed");
            }

            Avatar avatar= avatarService.uploadAvatar(studentId, file);
            return ResponseEntity.ok(MyApiResponse.of(avatar));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MyApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(MyApiResponse.error(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(MyApiResponse.error("Internal server error"));
        }
    }

    @Operation(
            summary= "Upload avatar asynchronously",
            description= "Same as regular upload but processed in background",
            responses= {
                    @ApiResponse(
                            responseCode= "200",
                            description= "Avatar uploaded successfully",
                            content= @Content(
                                    mediaType= MediaType.APPLICATION_JSON_VALUE,
                                    schema= @Schema(implementation= Avatar.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode= "400",
                            description= "Invalid file (size/type)",
                            content= @Content(
                                    mediaType= MediaType.APPLICATION_JSON_VALUE,
                                    schema= @Schema(implementation= ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode= "404",
                            description= "Student not found",
                            content= @Content(
                                    mediaType= MediaType.APPLICATION_JSON_VALUE,
                                    schema= @Schema(implementation= ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode= "500",
                            description= "Internal server error",
                            content= @Content(
                                    mediaType= MediaType.APPLICATION_JSON_VALUE,
                                    schema= @Schema(implementation= ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping(value="/async/{studentId}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<MyApiResponse<Avatar>>> uploadAvatarAsync(
            @PathVariable Long studentId,
            @RequestParam MultipartFile file) {

        return avatarService.uploadAvatarAsync(studentId, file)
                .thenApply(avatar -> ResponseEntity.ok(MyApiResponse.of(avatar)))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof EntityNotFoundException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(MyApiResponse.error("Student not found"));
                    } else if (cause instanceof IllegalArgumentException) {
                        return ResponseEntity.badRequest()
                                .body(MyApiResponse.error(cause.getMessage()));
                    }
                    return ResponseEntity.internalServerError()
                            .body(MyApiResponse.error("Internal server error"));
                });
    }

    @Operation(
            summary = "Get avatar from database",
            description = "Returns avatar image data with proper content type"
    )
    @GetMapping("/db/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromDb(
            @Parameter(description = "ID of student", required = true, example = "1")
            @PathVariable Long studentId) {
        try {
            Avatar avatar = avatarService.getAvatarByStudentId(studentId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                    .body(avatar.getData());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get avatar from file system",
            description = "Returns avatar image data from stored file"
    )
    @GetMapping("/file/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromFile(
            @Parameter(description = "ID of student", required = true, example = "1")
            @PathVariable Long studentId) {
        try {
            Avatar avatar = avatarService.getAvatarByStudentId(studentId);
            Path path = Path.of(avatar.getFilePath());
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }
            byte[] data = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                    .body(data);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Get avatars with pagination",
            description = "Returns page of avatar metadata (without image data)"
    )
    @GetMapping
    public Page<Avatar> getAvatars(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return avatarService.getAvatars(page, size);
    }
}
