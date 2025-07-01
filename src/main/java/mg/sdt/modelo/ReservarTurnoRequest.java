package mg.sdt.modelo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservarTurnoRequest {
    private String domicilio;
    private Integer servicioId;
}