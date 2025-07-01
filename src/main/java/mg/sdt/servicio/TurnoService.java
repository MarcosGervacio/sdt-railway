package mg.sdt.servicio;

import mg.sdt.modelo.Servicio;
import mg.sdt.modelo.Turno;
import mg.sdt.modelo.TurnoDTO;
import mg.sdt.modelo.User;
import mg.sdt.repositorio.ServicioRepository;
import mg.sdt.repositorio.TurnoRepository;
import mg.sdt.repositorio.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ServicioRepository servicioRepo;

    public Turno crearTurno(Turno turno) {
        if (turno.getFechaTurno() == null) {
            throw new RuntimeException("La fecha del turno es obligatoria.");
        }

        if (turno.getFechaTurno().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede crear un turno en el pasado.");
        }

        turno.setEstado("disponible");
        turno.setUser(null);
        turno.setFechaReservada(null);
        turno.setDomicilio(null);
        turno.setServicio(null);
        return turnoRepo.save(turno);
    }

    public Turno reservarTurno(Integer idTurno, String emailUsuario, String domicilio, Integer servicioId) {
        Turno turno = turnoRepo.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (domicilio == null || domicilio.trim().isEmpty()) {
            throw new RuntimeException("El domicilio es obligatorio");
        }

        if (!"disponible".equals(turno.getEstado())) {
            throw new RuntimeException("El turno ya está reservado");
        }

        User usuario = userRepo.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        List<Turno> turnosReservados = turnoRepo.findByUserIdAndEstado(usuario.getId(), "reservado");

        if (!turnosReservados.isEmpty()) {
            throw new RuntimeException("Ya tenés un turno reservado activo.");
        }

        turno.setUser(usuario);
        turno.setEstado("reservado");
        turno.setFechaReservada(LocalDateTime.now());
        turno.setDomicilio(domicilio);
        turno.setServicio(servicio);
        turno.setPrecioAplicado(servicio.getPrecio());
        return turnoRepo.save(turno);
    }

    public void eliminarTurno(Integer idTurno){
        Turno turno = turnoRepo.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        turnoRepo.delete(turno);
    }

    public void rechazarTurno(Integer idTurno){
        Turno turno = turnoRepo.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        turno.setEstado("disponible");
        turno.setUser(null);
        turno.setFechaReservada(null);
        turno.setDomicilio(null);
        turno.setServicio(null);
        turnoRepo.save(turno);
    }

    public void cancelarTurno(Integer idTurno, String emailUsuario){
        Turno turno = turnoRepo.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        User usuario = userRepo.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!turno.getUser().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tenés permiso para cancelar este turno.");
        }

        turno.setEstado("disponible");
        turno.setUser(null);
        turno.setFechaReservada(null);
        turno.setDomicilio(null);
        turno.setServicio(null);
        turnoRepo.save(turno);
    }

    public void completarTurno(Integer idTurno){
        Turno turno = turnoRepo.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setEstado("completado");
        turnoRepo.save(turno);
    }

    public List<Turno> obtenerTurnosDisponibles() {
        return turnoRepo.findByEstado("disponible");
    }

    public List<Turno> obtenerTurnosReservados() {
        return turnoRepo.findByEstado("reservado");
    }

    public List<Turno> obtenerTurnosCompletados() {
        return turnoRepo.findByEstado("completado");
    }

    public Page<Turno> obtenerTurnosCompletadosPag(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaTurno").descending());
        return turnoRepo.findByEstado("completado", pageable);
    }

    public List<TurnoDTO> obtenerTurnosDisponiblesPorFecha(LocalDate fecha) {
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.plusDays(1).atStartOfDay();

        List<Turno> turnos = turnoRepo.findByFechaTurnoBetween(desde, hasta);

        return turnos.stream()
                .filter(t -> "disponible".equals(t.getEstado()))
                .map(TurnoDTO::new)
                .toList();
    }

    public List<String> obtenerFechasConTurnosDisponibles() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return turnoRepo.findFechasConTurnosDisponibles().stream()
                .map(sqlDate -> sqlDate.toLocalDate().format(formatter))
                .toList();
    }

    public Page<Turno> obtenerTurnosPorUsuario(String email, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaTurno").descending());
        return turnoRepo.findByUserEmail(email, pageable);
    }

}
