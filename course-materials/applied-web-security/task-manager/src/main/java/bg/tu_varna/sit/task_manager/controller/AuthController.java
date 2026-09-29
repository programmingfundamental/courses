package bg.tu_varna.sit.task_manager.controller;

import bg.tu_varna.sit.task_manager.model.dto.request.LoginRequest;
import bg.tu_varna.sit.task_manager.model.dto.request.RefreshRequest;
import bg.tu_varna.sit.task_manager.model.dto.request.RegisterRequest;
import bg.tu_varna.sit.task_manager.model.dto.response.AuthResponse;
import bg.tu_varna.sit.task_manager.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/***
 * Добавено в лабораторно упражнение 11
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(AuthResponse.builder().message("Register successfully!").build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.login(request, httpServletRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(AuthResponse.builder().message("Log out successfully!").build());
    }
}
