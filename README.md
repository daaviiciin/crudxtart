# CRUDXTART - Backend REST API

Backend Java/Jakarta EE para el sistema CRM XTART. Proporciona una API REST completa para la gestión de clientes, empleados, productos, presupuestos, facturas, pagos e informes analíticos.

## Descripción del Proyecto

CRUDXTART es una aplicación web Java que implementa una arquitectura en capas siguiendo el patrón MVC adaptado:

- **Modelo**: Entidades JPA (Hibernate) que representan las tablas de la base de datos
- **Vista**: Endpoints REST que devuelven JSON
- **Controlador**: Servlets que manejan las peticiones HTTP y coordinan con los servicios

El proyecto utiliza **Jakarta EE 9+** con **CDI (Weld)** para inyección de dependencias, **JPA/Hibernate** para persistencia y **Jackson** para serialización JSON.

## Tecnologías Utilizadas

- **Java 21** - Lenguaje de programación
- **Jakarta EE 9+** - Especificaciones empresariales
- **Hibernate 7.0.4** - ORM para JPA
- **MySQL 8.0+** - Base de datos relacional
- **Apache Tomcat 10.x** - Servidor de aplicaciones
- **Maven** - Gestión de dependencias y construcción
- **CDI/Weld** - Inyección de dependencias
- **Jackson** - Serialización/deserialización JSON
- **JUnit 5** - Framework de testing

## Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas:

```
┌─────────────────────────────────────┐
│      Controller (Servlets)          │  ← Maneja peticiones HTTP
├─────────────────────────────────────┤
│      Service (Lógica de Negocio)    │  ← Reglas de negocio y validaciones
├─────────────────────────────────────┤
│      Repository (Acceso a Datos)    │  ← Operaciones JPA/Hibernate
├─────────────────────────────────────┤
│      Models (Entidades JPA)         │  ← Mapeo objeto-relacional
├─────────────────────────────────────┤
│      Base de Datos MySQL            │  ← Persistencia
└─────────────────────────────────────┘
```

## Estructura del Proyecto

```
crudxtart/
├── pom.xml                          # Configuración Maven
├── script_bd_Xtart.sql              # Script de creación de BD
├── INSTALACION.md                   # Guía de instalación
├── README.md                        # Este archivo
├── DIAGRAMAS.md                     # Diagramas del sistema
└── src/
    └── main/
        ├── java/com/example/crudxtart/
        │   ├── controller/          # Servlets REST (endpoints)
        │   ├── service/             # Lógica de negocio
        │   ├── repository/         # Acceso a datos (JPA)
        │   ├── models/             # Entidades JPA
        │   ├── config/             # Configuración (JPA Factory)
        │   ├── utils/              # Utilidades (JsonUtil)
        │   └── presentation/       # Servlets de prueba
        ├── resources/
        │   └── META-INF/
        │       ├── persistence.xml # Configuración JPA
        │       └── beans.xml       # Configuración CDI
        └── webapp/
            └── WEB-INF/
                └── web.xml         # Configuración web
```

## Endpoints REST Disponibles

### Autenticación
- `POST /login` - Autenticación de usuarios

### Entidades CRUD
- `GET /clientes` - Listar todos los clientes
- `GET /clientes?id={id}` - Obtener cliente por ID
- `POST /clientes` - Crear nuevo cliente
- `PUT /clientes?id={id}` - Actualizar cliente
- `DELETE /clientes?id={id}` - Eliminar cliente

Mismos endpoints para:
- `/empleados`
- `/productos`
- `/presupuestos`
- `/facturas`
- `/pagos`
- `/roles_empleado`

### Endpoints Especiales
- `GET /presupuestos/{id}/facturas` - Generar facturas desde presupuesto
- `GET /informes/ventas-empleado` - Informe de ventas por empleado
- `GET /informes/presupuestos-estado` - Informe de estados de presupuestos
- `GET /informes/facturacion-mensual` - Informe de facturación mensual
- `GET /informes/ventas-producto` - Informe de ventas por producto
- `GET /informes/ratio-conversion` - Ratio de conversión de presupuestos

## Características Principales

### 1. Inyección de Dependencias (CDI)
Todos los componentes utilizan `@Inject` para la inyección de dependencias, facilitando el testing y la mantenibilidad.

### 2. Logging Estructurado
Cada servlet utiliza un sistema de logging con códigos únicos para facilitar el debugging:
```java
private static final String CODIGO_LOG = "CTL-CLI-";
logger.info("[" + CODIGO_LOG + "001] doGet - inicio");
```

### 3. Manejo de Errores
Respuestas JSON estandarizadas:
```json
{
  "success": true,
  "data": {...}
}
```
o
```json
{
  "success": false,
  "data": {
    "error": "Mensaje de error"
  }
}
```

### 4. Concurrencia en Informes
El `InformesServlet` utiliza un `ExecutorService` con pool de 5 hilos para ejecutar informes pesados en segundo plano, mejorando el rendimiento del servidor.

### 5. Validaciones de Negocio
Cada servicio implementa validaciones específicas:
- Validación de campos obligatorios
- Validación de relaciones entre entidades
- Validación de estados y transiciones
- Validación de rangos y formatos

## Instalación y Configuración

Ver el archivo [INSTALACION.md](INSTALACION.md) para instrucciones detalladas de instalación.

### Resumen Rápido

1. **Requisitos**: Java 21, Maven, MySQL 8.0+, Tomcat 10.x
2. **Base de datos**: Ejecutar `script_bd_Xtart.sql`
3. **Configuración**: Editar `persistence.xml` con credenciales de BD
4. **Compilación**: `mvn clean package`
5. **Despliegue**: Copiar WAR a `webapps/` de Tomcat

## Uso de la API

### Ejemplo: Crear un Cliente

```bash
curl -X POST http://localhost:8080/crudxtart-1.0-SNAPSHOT/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "telefono": "123456789",
    "tipo_cliente": "PARTICULAR",
    "password": "password123"
  }'
```

### Ejemplo: Obtener Facturas

```bash
curl http://localhost:8080/crudxtart-1.0-SNAPSHOT/facturas
```

### Ejemplo: Generar Informe

```bash
curl "http://localhost:8080/crudxtart-1.0-SNAPSHOT/informes/ventas-empleado?desde=2024-01-01&hasta=2024-12-31"
```

## Testing

El proyecto incluye clases de test en el paquete `test/` y servlets de prueba en `presentation/` para verificar el funcionamiento de cada entidad.

## Base de Datos

El sistema utiliza MySQL con las siguientes entidades principales:

- **clientes** - Información de clientes (particulares y empresas)
- **empleados** - Información de empleados
- **roles_empleado** - Roles del sistema (ADMIN, COMERCIAL, etc.)
- **productos** - Catálogo de productos
- **presupuestos** - Presupuestos comerciales
- **presupuesto_productos** - Líneas de presupuesto
- **facturas** - Facturas emitidas
- **factura_productos** - Líneas de factura
- **pagos** - Pagos registrados

Ver `script_bd_Xtart.sql` para el esquema completo.

## Desarrollo

### Compilar el Proyecto
```bash
mvn clean package
```

### Ejecutar Tests
```bash
mvn test
```

### Desarrollo con Hot Reload
Ver sección 7 de [INSTALACION.md](INSTALACION.md) para configuración con plugin Tomcat.

## Logging

Los logs se generan en la consola de Tomcat y en `logs/catalina.out`. Cada operación está identificada con un código único para facilitar el seguimiento.

## Seguridad

- Las contraseñas se almacenan en texto plano (mejorable con hash)
- La autenticación se realiza mediante sesiones HTTP
- Los endpoints no requieren tokens JWT (implementación básica)

## Mejoras Futuras

- [ ] Implementar hash de contraseñas (BCrypt)
- [ ] Añadir autenticación JWT
- [ ] Implementar paginación en endpoints GET
- [ ] Añadir filtros avanzados en endpoints
- [ ] Implementar caché para consultas frecuentes
- [ ] Añadir documentación OpenAPI/Swagger

## Licencia

Proyecto académico para DAM (Desarrollo de Aplicaciones Multiplataforma).

## Contacto

Para problemas o consultas, revisar:
- [INSTALACION.md](INSTALACION.md) - Guía de instalación
- [DIAGRAMAS.md](DIAGRAMAS.md) - Diagramas del sistema
- Logs de Tomcat para errores en tiempo de ejecución

