package com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs;


import java.time.LocalDateTime;

public record PqrsResponseDTO(
         Integer idPqrs,
         String type,
         String status,
         String priority,
         String subject,
         String description,
         String response,
         Integer idUser,
         Integer assignedTo,

         LocalDateTime createdAt,
         LocalDateTime updatedAt,
         Integer createdBy,
         Integer updatedBy

)
{}
