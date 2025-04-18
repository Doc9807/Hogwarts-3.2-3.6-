package ru.hogwarts.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyApiResponse<T> {

    @Schema(description="Data payload")
    private T data;

    @Schema(description="Error message if any")
    private String error;

    public MyApiResponse() {}

    public MyApiResponse(T data, String error) {
        this.data= data;
        this.error= error;
    }

    public static <T> MyApiResponse<T> of(T data) {
        return new MyApiResponse<>(data, null);
    }

    public static <T> MyApiResponse<T> error(String errorMessage) {
        return new MyApiResponse<>(null, errorMessage);
    }
}