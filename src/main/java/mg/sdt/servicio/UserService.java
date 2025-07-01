package mg.sdt.servicio;

import mg.sdt.modelo.User;
import mg.sdt.repositorio.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public List<User> obtenerClientes() {
        return userRepo.findAll();
    }

}
