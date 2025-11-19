package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.exception.PqrsAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.model.PqrsType;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.UpdatePqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.UpdatePqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional

public class UpdatePqrsUseCase implements UpdatePqrs {

    private final PqrsPort pqrsPort;

    public UpdatePqrsUseCase(PqrsPort pqrsPort) {
        this.pqrsPort = pqrsPort;
    }

    @Override
    public Pqrs handle(UpdatePqrsCommand updatePqrsCommand) {

        Pqrs pqrs = pqrsPort.findById(updatePqrsCommand.idPqrs())
                .orElseThrow(()->new PqrsAlreadyDeletedException("Pqrs no encontrada en la base de datos con id: " + updatePqrsCommand.idPqrs()));

        if (pqrs.getIdUser() != updatePqrsCommand.idUser()) {
            throw new PqrsAlreadyDeletedException("El usuario actual no es el creador de la pqrs.");
        }

        if (updatePqrsCommand.type() != null) {
            pqrs.setType(PqrsType.valueOf(updatePqrsCommand.type()));
        }

        if (updatePqrsCommand.subject() != null) {
            pqrs.setSubject(updatePqrsCommand.subject());
        }

        if (updatePqrsCommand.description() != null) {
            pqrs.setDescription(updatePqrsCommand.description());
        }

        Pqrs pqrsSaved = pqrsPort.savePqrs(pqrs);

        return pqrsSaved;
    }
}
