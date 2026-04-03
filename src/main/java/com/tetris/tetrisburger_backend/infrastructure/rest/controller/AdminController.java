package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.exception.UnauthorizedException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.*;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.SearchUsersByEmailQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.validator.ImageValidator;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "User Management", description = "Gestión de usuarios por administradores")
public class AdminController {

    private final CreateUserByAdmin createUserByAdmin;
    private final ListUser listUser;
    private final GetUserById getUserById;
    private final UpdateUserByAdmin updateUserByAdmin;
    private final UpdateUserImageByAdmin updateUserImageByAdmin;
    private final DeleteUserByAdmin deleteUserByAdmin;
    private final SearchUsersByEmail searchUsersByEmail;
    private final UserRestDtoMapper mapper;
    private final ImageStoragePort imageStoragePort;
    private final ImageValidator imageValidator;

    public AdminController(
            CreateUserByAdmin createUserByAdmin,
            ListUser listUser,
            GetUserById getUserById,
            UpdateUserByAdmin updateUserByAdmin,
            UpdateUserImageByAdmin updateUserImageByAdmin,
            DeleteUserByAdmin deleteUserByAdmin,
            SearchUsersByEmail searchUsersByEmail,
            UserRestDtoMapper mapper,
            ImageStoragePort imageStoragePort,
            ImageValidator imageValidator
    ) {
        this.createUserByAdmin = createUserByAdmin;
        this.listUser = listUser;
        this.getUserById = getUserById;
        this.updateUserByAdmin = updateUserByAdmin;
        this.updateUserImageByAdmin = updateUserImageByAdmin;
        this.deleteUserByAdmin = deleteUserByAdmin;
        this.searchUsersByEmail = searchUsersByEmail;
        this.mapper = mapper;
        this.imageStoragePort = imageStoragePort;
        this.imageValidator = imageValidator;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateUserByAdminResponseDTO> createUser(
            @Valid @RequestPart("data") CreateUserByAdminRequestDTO requestDTO,
            @RequestPart(value = "userImage", required = false) MultipartFile userImage,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);
        boolean imageWasSent = (userImage != null && !userImage.isEmpty());

        if (imageWasSent) {
            imageValidator.validate(userImage);
        }

        CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(
                requestDTO,
                userImage,
                adminId
        );

        User createdUser = createUserByAdmin.handle(command);

        String imageUrl = resolveImageUrlFromUser(createdUser);
        ImageStatus imageStatus = resolveImageStatus(imageWasSent, createdUser.getUserImageKey());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toCreateUserByAdminResponseDTO(createdUser, imageUrl, imageStatus));
    }

    @GetMapping
    public ResponseEntity<ListUserResponseDTO> listAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idUser") String sortBy
    ) {
        ListUsersQuery query = new ListUsersQuery(page, size, sortBy);
        PageResponse<User> pageResponse = listUser.execute(query);

        List<UserResponseDTO> contentWithUrls = pageResponse.content().stream()
                .map(u -> {
                    String imageUrl = resolveImageUrlFromUser(u);
                    ImageStatus imageStatus = resolveImageStatus(false, u.getUserImageKey());
                    return mapper.toUserResponseDTO(u, imageUrl, imageStatus);
                })
                .toList();

        ListUserResponseDTO responseDTO = new ListUserResponseDTO(
                contentWithUrls,
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        User user = getUserById.handle(id);

        String imageUrl = resolveImageUrlFromUser(user);
        ImageStatus imageStatus = resolveImageStatus(false, user.getUserImageKey());

        return ResponseEntity.ok(mapper.toUserResponseDTO(user, imageUrl, imageStatus));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateUserByAdminResponseDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserByAdminRequestDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        UpdateUserByAdminCommand command = mapper.toUpdateUserByAdminCommand(id, updateDTO, adminId);
        User updatedUser = updateUserByAdmin.handle(command);

        String imageUrl = resolveImageUrlFromUser(updatedUser);
        ImageStatus imageStatus = resolveImageStatus(false, updatedUser.getUserImageKey());

        return ResponseEntity.ok(mapper.toUpdateUserByAdminResponseDTO(updatedUser, imageUrl, imageStatus));
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateUserByAdminResponseDTO> updateUserImage(
            @PathVariable Integer id,
            @RequestPart("userImage") MultipartFile userImage,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        imageValidator.validate(userImage);

        UpdateUserImageByAdminCommand command = mapper.toUpdateUserImageByAdminCommand(id, userImage, adminId);
        User user = updateUserImageByAdmin.handle(command);

        String imageUrl = resolveImageUrlFromUser(user);
        ImageStatus imageStatus = ImageStatus.PENDING;

        return ResponseEntity.ok(mapper.toUpdateUserByAdminResponseDTO(user, imageUrl, imageStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteUserByAdminDTO> deleteUserByAdmin(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        DeleteUserByAdminCommand command = new DeleteUserByAdminCommand(id, adminId);
        deleteUserByAdmin.handle(command);

        return ResponseEntity.ok(mapper.toDeleteUserByAdminDTO(id));
    }

    @GetMapping("/by-email")
    public ResponseEntity<PageResponse<UserResponseDTO>> getUsersByEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idUser") String sortBy
    ) {
        SearchUsersByEmailQuery query = new SearchUsersByEmailQuery(email);
        PaginationRequest request = new PaginationRequest(page, size, sortBy);

        PageResponse<User> result = searchUsersByEmail.handle(query, request);

        List<UserResponseDTO> dtoList = result.content().stream()
                .map(u -> {
                    String imageUrl = resolveImageUrlFromUser(u);
                    ImageStatus imageStatus = resolveImageStatus(false, u.getUserImageKey());
                    return mapper.toUserResponseDTO(u, imageUrl, imageStatus);
                })
                .toList();

        return ResponseEntity.ok(new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalPages(),
                result.totalPages()
        ));
    }

    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new UnauthorizedException("Usuario no autenticado correctamente");
    }

    private String resolveImageUrlFromUser(User user) {
        if (user == null || user.getUserImageKey() == null || user.getUserImageKey().isBlank()) {
            return null;
        }
        return imageStoragePort.getImageUrl(user.getUserImageKey());
    }

    private ImageStatus resolveImageStatus(boolean imageWasSent, String imageKey) {
        if (!imageWasSent) {
            return (imageKey == null || imageKey.isBlank()) ? ImageStatus.NONE : ImageStatus.UPLOADED;
        }

        if (imageKey == null || imageKey.isBlank()) {
            return ImageStatus.PENDING;
        }

        return ImageStatus.UPLOADED;
    }
}