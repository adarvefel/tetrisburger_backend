package com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs;



public record PqrsResponseDTO(
         Integer idPqrs,
         String type,
         String status,
         String priority,
         String subject,
         String description,
         String response,
         Integer idUser,
         Integer assignedTo
)
{}
