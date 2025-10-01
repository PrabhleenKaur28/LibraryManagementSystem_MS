package com.sb_project.user_service.service;

import com.sb_project.user_service.dto.UserRequestDTO;
import com.sb_project.user_service.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> findAllUsers();

    UserResponseDTO findById(Long id);

    UserResponseDTO addNewUser(UserRequestDTO request);

    UserResponseDTO updateUser(Long id, UserRequestDTO request);

    void removeUser(Long id);

    void borrowBook(Long userId, Long bookId);

    void returnBook(Long userId, Long bookId);
}
