package com.example.crudxtart.controller;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Filtro de autenticación que valida sesión HTTP en todas las peticiones
// excepto en /login y /logout
@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());
    private static final String CODIGO_LOG = "FILTER-AUTH-";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("[" + CODIGO_LOG + "001] AuthFilter inicializado");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Remover el context path para obtener la ruta relativa
        String relativePath = path.substring(contextPath.length());

        // Permitir acceso sin autenticacion a login y logout
        if (relativePath.equals("/login") || relativePath.equals("/logout")) {
            chain.doFilter(request, response);
            return;
        }

        // Validar sesion para el resto de endpoints
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null || session.getAttribute("user_id") == null) {
            logger.warning("[" + CODIGO_LOG + "002] Acceso no autorizado a: " + relativePath);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write(
                "{\"success\":false,\"data\":{\"error\":\"Sesión no válida. Por favor, inicie sesión.\"}}"
            );
            return;
        }

        // Sesion válida, continuar con la peticion
        Integer userId = (Integer) session.getAttribute("user_id");
        String userRol = (String) session.getAttribute("user_rol");
        logger.fine("[" + CODIGO_LOG + "003] Usuario autenticado - ID: " + userId + ", Rol: " + userRol);
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("[" + CODIGO_LOG + "004] AuthFilter destruido");
    }
}

