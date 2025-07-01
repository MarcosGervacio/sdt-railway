package mg.sdt.controlador;


import lombok.RequiredArgsConstructor;
import mg.sdt.modelo.Servicio;
import mg.sdt.servicio.ServicioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicio")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping()
    public ResponseEntity<List<Servicio>> obtenerServicios() {
        return ResponseEntity.ok(servicioService.obtenerServicios());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<Servicio> crearServicio(@RequestBody Servicio servicio) {
        return ResponseEntity.ok(servicioService.crearServicio(servicio));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarServicio(@PathVariable Integer id) {
        servicioService.eliminarServicio(id);
        return ResponseEntity.ok("Servicio eliminado con exito");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> modificarServicio(@PathVariable Integer id, @RequestBody Servicio servicio) {
        servicioService.modificarServicio(id, servicio);
        return ResponseEntity.ok("Servicio modificado con exito");
    }

}
