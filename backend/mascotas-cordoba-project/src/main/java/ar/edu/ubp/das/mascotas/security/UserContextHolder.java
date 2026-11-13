package ar.edu.ubp.das.mascotas.security;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;

/**
 * Almacena el contexto de sesión del usuario autenticado durante el ciclo de vida del request.
 */
public final class UserContextHolder {

    private static final ThreadLocal<SesionUsuarioBE> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(SesionUsuarioBE sesion) {
        if (sesion == null) {
            CONTEXT.remove();
        } else {
            CONTEXT.set(sesion);
        }
    }

    public static SesionUsuarioBE get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

