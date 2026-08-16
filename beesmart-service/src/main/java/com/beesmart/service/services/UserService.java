package com.beesmart.service.services;

import com.beesmart.service.dto.RegisterRequest;
import com.beesmart.service.dto.UserRequest;
import com.beesmart.service.exceptions.BadRequestException;
import com.beesmart.service.exceptions.ForbiddenException;
import com.beesmart.service.exceptions.NotFoundException;
import com.beesmart.service.model.Role;
import com.beesmart.service.model.User;
import com.beesmart.service.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Account management.
 *
 * Administration is deliberately restricted to BEEKEEPER accounts: every mutating method
 * refuses to touch an ADMIN row. That satisfies "CRUD users, ie only beekeepers" and, as a
 * side effect, structurally prevents an administrator from deleting or demoting itself.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Public self-registration. The role is forced, so a client cannot make itself an admin. */
    @Transactional
    public User register(RegisterRequest request) {
        String username = normalize(request.getUsername());

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username '" + username + "' is already taken");
        }

        User user = new User(
                username,
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getEmail(),
                Role.BEEKEEPER);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    /** Admin listing - beekeepers only, never other administrators. */
    public List<User> findAllBeekeepers() {
        return userRepository.findByRoleOrderByIdAsc(Role.BEEKEEPER);
    }

    public User findBeekeeper(Long id) {
        return requireBeekeeper(id);
    }

    @Transactional
    public User create(UserRequest request) {
        String username = normalize(request.getUsername());

        if (username == null || username.isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().length() < 5) {
            throw new BadRequestException("Password must be at least 5 characters");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username '" + username + "' is already taken");
        }

        User user = new User(
                username,
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getEmail(),
                Role.BEEKEEPER);
        user.setActive(request.getActive() == null || request.getActive());
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserRequest request) {
        User user = requireBeekeeper(id);

        String username = normalize(request.getUsername());
        if (username != null && !username.isEmpty() && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new BadRequestException("Username '" + username + "' is already taken");
            }
            user.setUsername(username);
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        // A blank password on update means "keep the current one" rather than blanking the hash.
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (request.getPassword().trim().length() < 5) {
                throw new BadRequestException("Password must be at least 5 characters");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public User setActive(Long id, boolean active) {
        User user = requireBeekeeper(id);
        user.setActive(active);
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = requireBeekeeper(id);
        userRepository.delete(user);
    }

    /** Loads a user by id and refuses if it is not a beekeeper. */
    private User requireBeekeeper(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (user.getRole() != Role.BEEKEEPER) {
            throw new ForbiddenException("Administrator accounts cannot be managed here");
        }
        return user;
    }

    private String normalize(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }
}
