package mg.sdt.servicio;

import mg.sdt.modelo.Servicio;
import mg.sdt.repositorio.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepo;

    public List<Servicio> obtenerServicios(){
        return servicioRepo.findAll();
    }

    public Servicio crearServicio(Servicio servicio){
        servicio.setEstado("activo");
        return servicioRepo.save(servicio);
    }

    public void eliminarServicio(Integer idServicio){
        Servicio servicio = servicioRepo.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setEstado("eliminado");
        servicioRepo.save(servicio);
    }

    public Servicio modificarServicio(Integer idServicio, Servicio servicio){
        Servicio servicioEncontrado = servicioRepo.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicioEncontrado.setNombre(servicio.getNombre());
        servicioEncontrado.setPrecio(servicio.getPrecio());
        return servicioRepo.save(servicioEncontrado);
    }

}
