package mg.sdt.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "turno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Servicio servicio;

    private LocalDateTime fechaReservada;
    @Column(name = "fecha_turno")
    private LocalDateTime fechaTurno;
    private String domicilio;
    private String estado;
    private Double precioAplicado;
}
