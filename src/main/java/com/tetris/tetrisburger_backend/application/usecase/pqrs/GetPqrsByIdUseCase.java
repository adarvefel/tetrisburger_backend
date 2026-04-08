package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.exception.PqrsAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.GetPqrsById;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.GetPqrsByIdCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.GetPqrsByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetPqrsByIdUseCase implements GetPqrsById {

    private final PqrsPort pqrsPort;
    private final UserRepository userRepository;

    public GetPqrsByIdUseCase(PqrsPort pqrsPort, UserRepository userRepository) {
        this.pqrsPort = pqrsPort;
        this.userRepository = userRepository;
    }

    @Override
    public Pqrs handle(GetPqrsByIdCommand getPqrsByIdCommand) {
        Pqrs pqrs = pqrsPort.findById(getPqrsByIdCommand.idPqrs())
                .orElseThrow(() -> new PqrsAlreadyDeletedException("No se encontro PQRS con el id:" + getPqrsByIdCommand.idPqrs()));

        if (pqrs.getDeletedBy() != null && pqrs.getDeletedAt() != null) {
            throw new PqrsAlreadyDeletedException("Pqrs no encontrada o ya eliminada previamente.");
        }

        User user = userRepository.findUserById(getPqrsByIdCommand.idUser())
                .orElseThrow(()->new UsernameNotFoundException("No se contro el usuario con id: " + getPqrsByIdCommand.idUser()));

        boolean isOwnerOfPqrs = pqrs.getIdUser().equals(user.getIdUser());
        boolean idAdminOrEmployee = user.getRole().isAdministrativeRol();

        if (!isOwnerOfPqrs && !idAdminOrEmployee) {
            throw new PqrsAlreadyDeletedException("Usuario no autorizado para ver este PQRS.");
        }

        return pqrs;

    }

}
