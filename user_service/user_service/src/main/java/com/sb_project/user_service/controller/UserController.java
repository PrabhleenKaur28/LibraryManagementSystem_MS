package com.sb_project.user_service.controller;

import com.sb_project.user_service.dto.UserRequestDTO;
import com.sb_project.user_service.dto.UserResponseDTO;
import com.sb_project.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ----------------- GET ALL USERS -----------------
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // ----------------- GET USER BY ID -----------------
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // ----------------- CREATE NEW USER -----------------
    @PostMapping
    public ResponseEntity<UserResponseDTO> addUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(201).body(userService.addNewUser(request));
    }

    // ----------------- UPDATE EXISTING USER -----------------
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // ----------------- DELETE USER -----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.removeUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // ----------------- BORROW A BOOK -----------------
    @PostMapping("/{userId}/borrow/{bookId}")
    public ResponseEntity<Void> borrowBook(@PathVariable Long userId, @PathVariable Long bookId) {
        userService.borrowBook(userId, bookId);
        return ResponseEntity.ok().build();
    }

    // ----------------- RETURN A BOOK -----------------
    @PostMapping("/{userId}/return/{bookId}")
    public ResponseEntity<Void> returnBook(@PathVariable Long userId, @PathVariable Long bookId) {
        userService.returnBook(userId, bookId);
        return ResponseEntity.ok().build();
    }

    // ----------------- GLOBAL EXCEPTION HANDLER -----------------
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(404).body(
                Map.of(
                        "status", 404,
                        "message", ex.getMessage(),
                        "path", "/users"
                )
        );
    }
}
