package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.exception.PqrsAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.model.PqrsPriority;
import com.tetris.tetrisburger_backend.domain.model.PqrsStatus;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.UpdatePqrsByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.UpdatePqrsByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional

public class UpdatePqrsByAdminUseCase implements UpdatePqrsByAdmin {

    private final PqrsPort pqrsPort;

    public UpdatePqrsByAdminUseCase(PqrsPort pqrsPort) {
        this.pqrsPort = pqrsPort;
    }

    @Override
    public Pqrs handle(UpdatePqrsByAdminCommand updatePqrsByAdminCommand) {

        Pqrs pqrs = pqrsPort.findById(updatePqrsByAdminCommand.idPqrs())
                .orElseThrow(()->new PqrsAlreadyDeletedException("No se encontro pqrs con id:" + updatePqrsByAdminCommand.idPqrs()));


        if (updatePqrsByAdminCommand.status() != null) {
            pqrs.setStatus(PqrsStatus.valueOf(updatePqrsByAdminCommand.status()));

        }

        if (updatePqrsByAdminCommand.priority() != null) {
            pqrs.setPriority(PqrsPriority.valueOf(updatePqrsByAdminCommand.priority()));
        }

        if (updatePqrsByAdminCommand.response() != null) {
            pqrs.setResponse(updatePqrsByAdminCommand.response());
        }

        pqrs.setAssignedTo(updatePqrsByAdminCommand.assignedTo());

        pqrs.setUpdatedBy(updatePqrsByAdminCommand.assignedTo());
        pqrs.setUpdatedAt(LocalDateTime.now());

        Pqrs pqrsSaved = pqrsPort.savePqrs(pqrs);

        return pqrsSaved;
    }
}
