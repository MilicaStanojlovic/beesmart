package com.beesmart.service.controllers;

import com.beesmart.service.dto.AuthResponse;
import com.beesmart.service.dto.LoginRequest;
import com.beesmart.service.dto.MessageResponse;
import com.beesmart.service.dto.RegisterRequest;
import com.beesmart.service.dto.UserResponse;
import com.beesmart.service.model.User;
import com.beesmart.service.security.JwtUtil;
import com.beesmart.service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * POST /api/auth/login
     * Body: { "username": "admin", "password": "admin123" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String username = request.getUsername() == null
                ? "" : request.getUsername().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("This account has been deactivated"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid username or password"));
        }

        User user = userService.findByUsername(username);
        return ResponseEntity.ok(toAuthResponse(user));
    }

    /**
     * POST /api/auth/register
     * Creates a BEEKEEPER account and logs it straight in.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAuthResponse(user));
    }

    /** GET /api/auth/me - the currently authenticated user. */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getUsername(),
                user.getFullName(), user.getRole());
    }
}
