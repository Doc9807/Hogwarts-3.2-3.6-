package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.MathService;

@RestController
@RequestMapping("/math")
@Tag(name = "Math Operations")
public class MathController {
    private final MathService mathService;

    @Autowired
    public MathController(MathService mathService) {
        this.mathService = mathService;
    }

    @Operation(
            summary = "Calculate optimized sum",
            description = "Calculates sum of numbers from 1 to 1,000,000 using arithmetic progression formula"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sum calculated",
            content = @Content(schema = @Schema(type = "integer"))
    )
    @GetMapping("/sum")
    public ResponseEntity<Long> calculateSum() {
        return ResponseEntity.ok(mathService.calculateSum());
    }
}
