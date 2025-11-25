package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.exception.PqrsAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.DeleteSoftPqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.DeleteSoftPqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;

@Service
@Transactional
public class DeleteSoftPqrsUseCase implements DeleteSoftPqrs {

    PqrsPort pqrsPort;
    UserRepository userRepository;

    public DeleteSoftPqrsUseCase(PqrsPort pqrsPort, UserRepository userRepository) {
        this.pqrsPort = pqrsPort;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(DeleteSoftPqrsCommand deleteSoftPqrsCommand) {

        Pqrs pqrs = pqrsPort.findById(deleteSoftPqrsCommand.idPqrs())
                .orElseThrow(() -> new PqrsAlreadyDeletedException("No se encontro PQRS con el id:" + deleteSoftPqrsCommand.idPqrs()));

        if (pqrs.getDeletedBy() != null && pqrs.getDeletedAt() != null) {
            throw new PqrsAlreadyDeletedException("Pqrs no encontrada o ya eliminada previamente.");
        }

        User user = userRepository.findUserById(deleteSoftPqrsCommand.idUser())
                .orElseThrow(()->new UsernameNotFoundException("No se contro el usuario con id: " + deleteSoftPqrsCommand.idUser()));

        boolean isOwnerOfPqrs = pqrs.getIdUser().equals(user.getIdUser());
        boolean idAdminOrEmployee = user.getRole().isAdministrativeRol();

        if (!isOwnerOfPqrs && !idAdminOrEmployee) {
            throw new PqrsAlreadyDeletedException("Usuario no autorizado para eliminar este PQRS.");
        }

        pqrsPort.softDeletePqrs(deleteSoftPqrsCommand.idPqrs(), user.getIdUser());

    }

}
