package com.example.crudxtart.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utilidad para trabajar con sesiones HTTP
 */
public class SessionUtil {

    private SessionUtil() {
        // Clase estática, no instanciable
    }

    //Obtiene el ID del usuario de la sesion
    public static Integer getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("user_id");
            if (userId instanceof Integer) {
                return (Integer) userId;
            }
        }
        return null;
    }

    // Obtiene el email del usuario de la sesion
    public static String getUserEmail(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("user_email");
        }
        return null;
    }

    // Obtiene el nombre del usuario de la sesion
    public static String getUserNombre(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("user_nombre");
        }
        return null;
    }

    // Obtiene el rol del usuario de la sesion
    public static String getUserRol(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("user_rol");
        }
        return null;
    }

    // Obtiene el tipo de usuario (empleado/cliente) de la sesion
    public static String getUserTipo(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("user_tipo");
        }
        return null;
    }

    // Verifica si el usuario tiene un rol específico
    public static boolean hasRole(HttpServletRequest req, String role) {
        String userRol = getUserRol(req);
        return userRol != null && userRol.equalsIgnoreCase(role);
    }

    // Verifica si el usuario es administrador
    public static boolean isAdmin(HttpServletRequest req) {
        return hasRole(req, "ADMIN");
    }

    // Verifica si el usuario es empleado (no cliente)
    public static boolean isEmpleado(HttpServletRequest req) {
        String tipo = getUserTipo(req);
        return "empleado".equalsIgnoreCase(tipo);
    }

    // Verifica si el usuario es cliente
    public static boolean isCliente(HttpServletRequest req) {
        String tipo = getUserTipo(req);
        return "cliente".equalsIgnoreCase(tipo);
    }
}

