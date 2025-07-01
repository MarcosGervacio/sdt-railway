package mg.sdt.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private Boolean enabled = false;  // Indica si el usuario ha activado su cuenta

    private String verificationToken; // Token para verificar el email

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // campo para los roles

    private String resetToken;
    private LocalDateTime tokenExpiration;
}