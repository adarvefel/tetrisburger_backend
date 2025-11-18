package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.exception.PqrsAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.DeleteSoftPqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.DeleteSoftPqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteSoftPqrsUseCase implements DeleteSoftPqrs {

    PqrsPort pqrsPort;

    public DeleteSoftPqrsUseCase(PqrsPort pqrsPort) {
        this.pqrsPort = pqrsPort;
    }

    @Override
    public void handle(DeleteSoftPqrsCommand deleteSoftPqrsCommand) {

        Pqrs pqrs = pqrsPort.findById(deleteSoftPqrsCommand.idPqrs())
                .orElseThrow(() -> new PqrsAlreadyDeletedException("No se encontro PQRS con el id:" + deleteSoftPqrsCommand.idPqrs()));

        if (pqrs.getDeletedBy() != null && pqrs.getDeletedAt() != null) {
            throw new PqrsAlreadyDeletedException("Pqrs no encontrada o ya eliminada previamente.");
        }
        pqrsPort.softDeletePqrs(deleteSoftPqrsCommand.idPqrs(), deleteSoftPqrsCommand.idUser());
    }

}
