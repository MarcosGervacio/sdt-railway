package mg.sdt.modelo;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
public class TurnoDTO {
    private Integer id;
    private LocalDateTime fechaTurno;

    public TurnoDTO(Turno turno) {
        this.id = turno.getId();
        this.fechaTurno = turno.getFechaTurno();
    }
}
