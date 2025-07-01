package mg.sdt.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    @NotNull
    private String nombre;
    @NotNull
    @Column(nullable = false)
    private Double precio;
    @NotNull
    @Column(nullable = false)
    private String estado;
}
