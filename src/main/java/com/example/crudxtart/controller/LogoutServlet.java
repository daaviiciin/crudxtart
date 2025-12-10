package com.example.crudxtart.controller;

import java.io.IOException;
import java.util.logging.Logger;

import com.example.crudxtart.utils.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LogoutServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-LOGOUT-";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doPost Logout - inicio");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = req.getSession(false);
            
            if (session != null) {
                String userId = session.getAttribute("user_id") != null 
                    ? session.getAttribute("user_id").toString() 
                    : "desconocido";
                
                logger.info("[" + CODIGO_LOG + "002] Cerrando sesión para usuario ID: " + userId);
                
                // Invalidar la sesion
                session.invalidate();
                
                logger.info("[" + CODIGO_LOG + "003] Sesión invalidada correctamente");
            } else {
                logger.warning("[" + CODIGO_LOG + "004] No hay sesión activa para cerrar");
            }

            // Respuesta exitosa
            resp.getWriter().write(JsonUtil.toJson(new Response(true, "Sesión cerrada correctamente")));

        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "005] ERROR en logout: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(new Response(false, new ErrorMsg("Error al cerrar sesión"))));
        }
    }

    // ============================================================
    // Objetos respuesta JSON
    // ============================================================
    private static class Response {
        final boolean success;
        final Object data;

        Response(boolean success, Object data) {
            this.success = success;
            this.data = data;
        }

        public boolean isSuccess() { return success; }
        public Object getData() { return data; }
    }

    private static class ErrorMsg {
        final String error;

        ErrorMsg(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}

