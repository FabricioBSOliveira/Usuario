package com.Fabricio.Usuario.infrastructure.exception.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String error;
    private String path;
}
