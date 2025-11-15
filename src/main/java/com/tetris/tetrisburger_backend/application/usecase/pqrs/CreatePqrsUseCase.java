package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.model.PqrsPriority;
import com.tetris.tetrisburger_backend.domain.model.PqrsStatus;
import com.tetris.tetrisburger_backend.domain.model.PqrsType;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.CreatePqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class CreatePqrsUseCase implements CreatePqrs {

    private final PqrsPort pqrsPort;

    public CreatePqrsUseCase(PqrsPort pqrsPort) {
        this.pqrsPort = pqrsPort;
    }

    @Override
    public Pqrs handle(CreatePqrsCommand createPqrsCommand) {

        Pqrs pqrs = new Pqrs();

        pqrs.setType(PqrsType.valueOf(createPqrsCommand.type()));
        pqrs.setStatus(PqrsStatus.RECEIVED);
        pqrs.setPriority(PqrsPriority.MEDIUM);
        pqrs.setSubject(createPqrsCommand.subject());
        pqrs.setDescription(createPqrsCommand.description());
        pqrs.setIdUser(createPqrsCommand.idUser());

        Pqrs pqrsSaved = pqrsPort.savePqrs(pqrs);

        return pqrsSaved;
    }
}
