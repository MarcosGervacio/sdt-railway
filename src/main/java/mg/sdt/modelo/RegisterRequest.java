package mg.sdt.modelo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private String password;
}