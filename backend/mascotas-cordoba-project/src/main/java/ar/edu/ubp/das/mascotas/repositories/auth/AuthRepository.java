package ar.edu.ubp.das.mascotas.repositories.auth;

import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Autentica a un usuario por CUIL y clave, validando que pertenezca
     * a un refugio habilitado en el programa mediante el procedimiento almacenado.
     */
    public Optional<UsuarioRefugioBE> autenticarRefugio(String cuil, String clave) {
        List<UsuarioRefugioBE> resultados = jdbcTemplate.query(
                "exec dbo.autenticar_usuario_refugio ?, ?",
                new BeanPropertyRowMapper<>(UsuarioRefugioBE.class),
                cuil,
                clave
        );

        if (resultados.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(resultados.get(0));
    }

}
