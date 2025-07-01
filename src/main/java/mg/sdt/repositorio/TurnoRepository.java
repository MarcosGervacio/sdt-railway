package mg.sdt.repositorio;

import mg.sdt.modelo.Turno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Integer> {
    List<Turno> findByEstado(String estado);
    Page<Turno> findByEstado(String estado, Pageable pageable);
    List<Turno> findByUserIdAndEstado(Integer userId, String estado);
    Page<Turno> findByUserEmail(String Email, Pageable pageable);
    List<Turno> findByFechaTurnoBetween(LocalDateTime desde, LocalDateTime hasta);
    @Query(value = "SELECT DISTINCT DATE(fecha_turno) FROM turno WHERE estado = 'disponible'", nativeQuery = true)
    List<java.sql.Date> findFechasConTurnosDisponibles();

}
