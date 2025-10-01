package com.sb_project.user_service.service;

import com.sb_project.user_service.dto.BookDTO;
import com.sb_project.user_service.dto.UserRequestDTO;
import com.sb_project.user_service.dto.UserResponseDTO;
import com.sb_project.user_service.entity.User;
import com.sb_project.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    // ----------------- GET ALL USERS -----------------
    @Override
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ----------------- GET USER BY ID -----------------
    @Override
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }

    // ----------------- ADD NEW USER -----------------
    @Override
    public UserResponseDTO addNewUser(UserRequestDTO request) {
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setMembershipType(request.membershipType());

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    // ----------------- UPDATE EXISTING USER -----------------
    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setMembershipType(request.membershipType());

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    // ----------------- DELETE USER -----------------
    @Override
    public void removeUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Return all borrowed books before deletion
        if (user.getBorrowedBookIds() != null && !user.getBorrowedBookIds().isEmpty()) {
            for (Long bookId : user.getBorrowedBookIds()) {
                try {
                    restTemplate.postForObject(
                            "http://localhost:8080/books/" + bookId + "/return/" + id,
                            null,
                            Void.class
                    );
                } catch (Exception e) {
                    System.out.println("Failed to return book with id " + bookId + ": " + e.getMessage());
                }
            }
        }

        userRepository.delete(user);
    }

    // ----------------- BORROW A BOOK -----------------
    @Override
    public void borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!user.getBorrowedBookIds().contains(bookId)) {
            user.getBorrowedBookIds().add(bookId);
        }

        userRepository.save(user);
    }

    // ----------------- RETURN A BOOK -----------------
    @Override
    public void returnBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.getBorrowedBookIds().remove(bookId);
        userRepository.save(user);
    }

    // ----------------- HELPER: MAP ENTITY TO RESPONSE -----------------
    private UserResponseDTO mapToResponseDTO(User user) {
        List<String> bookTitles;

        if (user.getBorrowedBookIds() != null && !user.getBorrowedBookIds().isEmpty()) {
            bookTitles = user.getBorrowedBookIds().stream().map(bookId -> {
                try {
                    BookDTO book = restTemplate.getForObject(
                            "http://localhost:8080/books/" + bookId,
                            BookDTO.class
                    );
                    return book != null ? book.name() : "Unknown Book";
                } catch (Exception e) {
                    return "Unknown Book"; // fallback if BookService call fails
                }
            }).toList();
        } else {
            bookTitles = List.of();
        }

        return new UserResponseDTO(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getMembershipType(),
                bookTitles
        );
    }
}
