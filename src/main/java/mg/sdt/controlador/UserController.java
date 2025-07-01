package mg.sdt.controlador;

import lombok.RequiredArgsConstructor;
import mg.sdt.modelo.User;
import mg.sdt.servicio.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/clientes")
    public ResponseEntity<List<User>> obtenerClientes() {
        return ResponseEntity.ok(userService.obtenerClientes());
    }
}
