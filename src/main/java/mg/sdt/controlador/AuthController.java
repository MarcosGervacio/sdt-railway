package mg.sdt.controlador;

import mg.sdt.modelo.LoginRequest;
import mg.sdt.modelo.RegisterRequest;
import mg.sdt.modelo.ResetPasswordRequest;
import mg.sdt.modelo.User;
import mg.sdt.repositorio.UserRepository;
import mg.sdt.servicio.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) { // recibe JSON
        if (request.getEmail() == null || request.getPassword() == null || request.getNombre() == null || request.getApellido() == null || request.getTelefono() == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Faltan datos"));
        }
        String message = authService.registerUser(request.getEmail(), request.getPassword(), request.getNombre(), request.getApellido(), request.getTelefono());
        return ResponseEntity.ok(Collections.singletonMap("message", message));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyUser(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) { // recibe JSON
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Faltan datos"));
        }

        String token = authService.loginUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> solicitarReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        authService.generarTokenResetPassword(email);
        return ResponseEntity.ok("Se envió un correo con el enlace de recuperación");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepo.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        userRepo.save(user);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }


}

