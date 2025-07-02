package mg.sdt.servicio;

import mg.sdt.modelo.Role;
import mg.sdt.modelo.User;
import mg.sdt.repositorio.UserRepository;
import mg.sdt.securiy.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;

    @Transactional
    public String registerUser(String email, String password, String nombre, String apellido, String telefono) {
        // Verifica si el usuario ya está registrado
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crea el usuario y genera un token de verificación
        User user = new User();
        user.setEmail(email);
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setTelefono(telefono);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(false);
        user.setRole(Role.ROLE_USER); // rol USER por defecto
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        userRepository.save(user);

        // Enviar correo de verificación
        sendVerificationEmail(email, token);

        return "Registro exitoso. Revisa tu correo para activar tu cuenta.";
    }

    public void sendVerificationEmail(String email, String token) {
        String url = "https://just-grace-production.up.railway.app/api/auth/verify?token=" + token;
        String subject = "Confirma tu cuenta";
        String content = "<p>Haz clic en el siguiente enlace para activar tu cuenta:</p>"
                + "<a href=\"" + url + "\">Activar cuenta</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo");
        }
    }

    @Transactional
    public String verifyUser(String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Token inválido");
        }

        User user = optionalUser.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Cuenta activada correctamente";
    }


    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!user.getEnabled()) {
            throw new RuntimeException("Cuenta no activada");
        }


        return jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getNombre()); // pasamos email y role

    }


    public String generarTokenResetPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(30)); // expira en 30 min
        userRepo.save(user);

        sendRecoverPassword(user.getEmail(), token);
        return token;
    }

    public void sendRecoverPassword(String email, String token) {
        String url = "https://just-grace-production.up.railway.app/reset-password?token=" + token;
        String subject = "Recuperación de contraseña";
        String content = "<p>Haz clic en el siguiente enlace para restablecer tu contraseña:</p>"
                + "<a href=\"" + url + "\">Restablecer contraseña</a>"
                + "<br><p>Si no solicitaste este cambio, puedes ignorar este mensaje.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de recuperación");
        }
    }


}
