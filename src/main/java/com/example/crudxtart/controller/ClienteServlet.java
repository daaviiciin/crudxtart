package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.utils.JsonUtil;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    // logger
    private static final Logger logger = Logger.getLogger(ClienteServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-CLI-";

    @Inject
    private ClienteService clienteService;

    @Inject
    private EmpleadoService empleadoService;

    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // LOG de entrada
        logger.info("[" + CODIGO_LOG + "001] doGet - inicio. id=" + req.getParameter("id"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // GET /clientes?id=7
            String idParam = req.getParameter("id");
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Cliente cli = clienteService.findClienteById(id);

                if (cli == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Cliente no encontrado");
                    return;
                }

                sendSuccess(resp, cli);
                return;
            }

            // GET /clientes?nombre=X&email=Y...
            String nombre = req.getParameter("nombre");
            String email = req.getParameter("email");
            String telefono = req.getParameter("telefono");

            List<Cliente> clientes = (nombre != null || email != null || telefono != null)
                    ? clienteService.findClientesByFilters(nombre, email, telefono)
                    : clienteService.findAllClientes();

            sendSuccess(resp, clientes);

        } catch (Exception ex) {
            // LOG de error GET genérico
            logger.severe("[" + CODIGO_LOG + "008] ERROR doGet Clientes: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // POST (crear)
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // LOG de entrada
        logger.info("[" + CODIGO_LOG + "002] doPost - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req); // disparará el LOG 005
            Cliente cli = JsonUtil.fromJson(body, Cliente.class);

            // Asegurar que el ID sea null para crear nuevo registro
            cli.setId_cliente(null);

            // Si no viene fecha_alta, establecerla automáticamente
            if (cli.getFecha_alta() == null) {
                cli.setFecha_alta(java.time.LocalDate.now());
            }

            // Manejar empleado_responsable si viene en el JSON
            // El frontend puede enviar: {"empleado_responsable": {"id_empleado": 4}}
            if (cli.getEmpleado_responsable() != null &&
                    cli.getEmpleado_responsable().getId_empleado() != null) {

                // Cargar el empleado desde la base de datos
                Empleado emp = empleadoService.findEmpleadoById(
                        cli.getEmpleado_responsable().getId_empleado());
                if (emp != null) {
                    cli.setEmpleado_responsable(emp);
                } else {
                    // Si el empleado no existe, establecer a null
                    cli.setEmpleado_responsable(null);
                }
            } else {
                cli.setEmpleado_responsable(null);
            }

            Cliente creado = clienteService.createCliente(cli);

            // Verificar que el ID se generó correctamente
            if (creado.getId_cliente() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID del cliente");
                return;
            }

            sendSuccess(resp, creado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            // LOG de error de formato JSON
            logger.severe("[" + CODIGO_LOG + "009] ERROR JSON doPost Clientes: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            // LOG de error genérico POST
            logger.severe("[" + CODIGO_LOG + "010] ERROR doPost Clientes: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // PUT (actualizar)
    // ============================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // LOG de entrada
        logger.info("[" + CODIGO_LOG + "003] doPut - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Cliente cli = JsonUtil.fromJson(body, Cliente.class);

            if (cli.getId_cliente() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_cliente es obligatorio para actualizar");
                return;
            }

            // Asegurar que empleado_responsable sea null (no debe venir en JSON)
            cli.setEmpleado_responsable(null);

            // Cargar el cliente existente para preservar campos no enviados
            Cliente existente = clienteService.findClienteById(cli.getId_cliente());
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Cliente no encontrado");
                return;
            }

            // Actualizar solo los campos enviados (si vienen null, mantener los existentes)
            if (cli.getNombre() != null) {
                existente.setNombre(cli.getNombre());
            }
            if (cli.getEmail() != null) {
                existente.setEmail(cli.getEmail());
            }
            if (cli.getTelefono() != null) {
                existente.setTelefono(cli.getTelefono());
            }
            if (cli.getPassword() != null) {
                existente.setPassword(cli.getPassword());
            }
            if (cli.getTipo_cliente() != null) {
                existente.setTipo_cliente(cli.getTipo_cliente());
            }
            if (cli.getFecha_alta() != null) {
                existente.setFecha_alta(cli.getFecha_alta());
            }

            Cliente actualizado = clienteService.upLocalDateCliente(existente);
            sendSuccess(resp, actualizado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            // LOG de error JSON PUT
            logger.severe("[" + CODIGO_LOG + "011] ERROR JSON doPut Clientes: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            // LOG de error genérico PUT
            logger.severe("[" + CODIGO_LOG + "012] ERROR doPut Clientes: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // DELETE (eliminar)
    // ============================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // LOG de entrada
        logger.info("[" + CODIGO_LOG + "004] doDelete - inicio. id=" + req.getParameter("id"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Debe proporcionar ?id= para eliminar");
                return;
            }

            Integer id = Integer.parseInt(idParam);

            // Verificar que el cliente existe antes de eliminar
            Cliente cli = clienteService.findClienteById(id);
            if (cli == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Cliente no encontrado");
                return;
            }

            clienteService.deleteCliente(id);

            // Devolver null en data para indicar éxito sin datos
            sendSuccess(resp, null);

        } catch (NumberFormatException ex) {
            // LOG de error de formato de ID
            logger.severe("[" + CODIGO_LOG + "013] ERROR doDelete Clientes - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            // LOG de error genérico DELETE
            logger.severe("[" + CODIGO_LOG + "014] ERROR doDelete Clientes: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        // LOG de traza de lectura de body
        logger.fine("[" + CODIGO_LOG + "005] readBody - leyendo cuerpo de la petición");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object dataObj) throws IOException {
        // LOG de envío de éxito
        logger.fine("[" + CODIGO_LOG + "006] sendSuccess - enviando respuesta de éxito");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, dataObj)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        // LOG de envío de error
        logger.fine("[" + CODIGO_LOG + "007] sendError - enviando error: " + msg);
        resp.getWriter().write(JsonUtil.toJson(new Response(false, new ErrorMsg(msg))));
    }

    private static class Response {
        final boolean success;
        final Object data;

        Response(boolean success, Object data) {
            this.success = success;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public Object getData() {
            return data;
        }
    }

    private static class ErrorMsg {
        final String error;

        ErrorMsg(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
