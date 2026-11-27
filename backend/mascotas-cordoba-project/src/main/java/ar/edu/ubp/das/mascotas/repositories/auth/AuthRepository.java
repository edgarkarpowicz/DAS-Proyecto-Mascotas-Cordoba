package ar.edu.ubp.das.mascotas.repositories.auth;

import ar.edu.ubp.das.mascotas.BE.auth.UsuarioBE;
import ar.edu.ubp.das.mascotas.repositories.common.BaseStoredProcRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthRepository extends BaseStoredProcRepository {

    /**
     * Autentica a un usuario por CUIL y clave distinguiendo su perfil
     * (CIUDADANO, REFUGIO, VETERINARIA o MUNICIPALIDAD) mediante dbo.autenticar_usuario.
     */
    public Optional<UsuarioBE> autenticarUsuario(String cuil, String clave) {
        return queryProcOptional(
                "exec dbo.autenticar_usuario ?, ?",
                UsuarioBE.class,
                "Error de base de datos al autenticar usuario",
                cuil,
                clave
        );
    }

}
