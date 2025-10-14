package ru.practicum.exception;


import lombok.Data;

@Data
public class ErrorDto {
    String error;
    String message;

    public ErrorDto(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
