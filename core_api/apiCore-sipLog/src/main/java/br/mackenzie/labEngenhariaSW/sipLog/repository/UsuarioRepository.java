package br.mackenzie.labEngenhariaSW.sipLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.entity.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByKeycloakId(String keycloakId);
}