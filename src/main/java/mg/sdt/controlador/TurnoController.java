package mg.sdt.controlador;


import lombok.RequiredArgsConstructor;
import mg.sdt.modelo.ReservarTurnoRequest;
import mg.sdt.modelo.Turno;
import mg.sdt.modelo.TurnoDTO;
import mg.sdt.servicio.TurnoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turno")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    // endpoints de ADMINISTRADORES
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<Turno> crearTurno(@RequestBody Turno turno) {
        return ResponseEntity.ok(turnoService.crearTurno(turno));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarTurno(@PathVariable Integer id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.ok("Turno eliminado correctamente.");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/rechazar/{id}")
    public ResponseEntity<String> rechazarTurno(@PathVariable Integer id) {
        turnoService.rechazarTurno(id);
        return ResponseEntity.ok("Turno rechazado correctamente.");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/completado/{id}")
    public ResponseEntity<String> completarTurno(@PathVariable Integer id) {
        turnoService.completarTurno(id);
        return ResponseEntity.ok("Turno completado correctamente.");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/completados")
    public ResponseEntity<List<Turno>> obtenerTurnosCompletados() {
        return ResponseEntity.ok(turnoService.obtenerTurnosCompletados());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/completadosPag")
    public ResponseEntity<Page<Turno>> obtenerTurnosCompletados(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(turnoService.obtenerTurnosCompletadosPag(page, size));
    }




    // endpoints de USUARIOS

    @PostMapping("/reservar/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Turno> reservarTurno(
            @PathVariable Integer id,
            @RequestBody ReservarTurnoRequest request,
            Principal principal) {

        Turno reservado = turnoService.reservarTurno(
                id,
                principal.getName(),
                request.getDomicilio(),
                request.getServicioId()
        );
        return ResponseEntity.ok(reservado);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/cancelar/{id}")
    public ResponseEntity<String> cancelarTurno(@PathVariable Integer id, Principal principal) {
        turnoService.cancelarTurno(id, principal.getName());
        return ResponseEntity.ok("Turno cancelado correctamente.");
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/usuario/{email}")
    public ResponseEntity<Page<Turno>> turnosPorUsuario(@PathVariable String email,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorUsuario(email, page, size));
    }


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/disponibles")
    public ResponseEntity<List<Turno>> obtenerTurnosDisponibles() {
        return ResponseEntity.ok(turnoService.obtenerTurnosDisponibles());
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/reservados")
    public ResponseEntity<List<Turno>> obtenerTurnosReservados() {
        return ResponseEntity.ok(turnoService.obtenerTurnosReservados());
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/disponiblesPorFecha")
    public List<TurnoDTO> obtenerTurnosPorFecha(@RequestParam LocalDate fecha) {
        return turnoService.obtenerTurnosDisponiblesPorFecha(fecha);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/fechas")
    public List<String> obtenerFechasConTurnos() {
        return turnoService.obtenerFechasConTurnosDisponibles();
    }


}
