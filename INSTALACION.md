# Guía de Instalación y Puesta en Marcha - CRUDXTART

## Requisitos Previos

### Software Necesario
- **Java JDK 21** o superior
- **Apache Maven 3.6+** (o usar el Maven Wrapper incluido: `mvnw` / `mvnw.cmd`)
- **MySQL 8.0+** (o MariaDB compatible)
- **Apache Tomcat 10.x** (compatible con Jakarta EE 9+)

### Verificar Instalación
```bash
java -version        # Debe mostrar Java 21 o superior
mvn -version         # Debe mostrar Maven 3.6+
mysql --version      # Debe mostrar MySQL 8.0+
```

---

## 1. Configuración de la Base de Datos

### 1.1. Crear la Base de Datos
```bash
mysql -u root -p
```

Ejecutar el script SQL:
```sql
source script_bd_Xtart.sql
```

O manualmente:
```sql
CREATE DATABASE IF NOT EXISTS crm_xtart
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;
```

### 1.2. Configurar Credenciales de Conexión

Editar `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/crm_xtart"/>
<property name="jakarta.persistence.jdbc.user" value="TU_USUARIO"/>
<property name="jakarta.persistence.jdbc.password" value="TU_PASSWORD"/>
```

**Valores por defecto:**
- Usuario: `root`
- Password: `1234`
- Base de datos: `crm_xtart`
- Puerto: `3306`

---

## 2. Compilación del Proyecto

### Opción A: Usando Maven Wrapper (Recomendado)
```bash
# Windows
mvnw.cmd clean package

# Linux/Mac
./mvnw clean package
```

### Opción B: Usando Maven Instalado
```bash
mvn clean package
```

**Resultado:** Se genera el archivo `target/crudxtart-1.0-SNAPSHOT.war`

---

## 3. Despliegue en Tomcat

### 3.1. Copiar el WAR
Copiar `target/crudxtart-1.0-SNAPSHOT.war` a la carpeta `webapps` de Tomcat:

```bash
# Windows
copy target\crudxtart-1.0-SNAPSHOT.war C:\apache-tomcat-10\webapps\

# Linux/Mac
cp target/crudxtart-1.0-SNAPSHOT.war /opt/tomcat/webapps/
```

### 3.2. Iniciar Tomcat
```bash
# Windows
C:\apache-tomcat-10\bin\startup.bat

# Linux/Mac
/opt/tomcat/bin/startup.sh
```

### 3.3. Verificar Despliegue
Abrir en el navegador:
```
http://localhost:8080/crudxtart-1.0-SNAPSHOT/
```

O si renombraste el WAR a `crudxtart.war`:
```
http://localhost:8080/crudxtart/
```

---

## 4. Configuración Adicional

### 4.1. Puerto del Servidor
Si necesitas cambiar el puerto de Tomcat, editar `conf/server.xml`:
```xml
    <Connector port="8080" protocol="HTTP/1.1" ... />
```

### 4.2. Hibernate DDL Auto
En `persistence.xml`, la propiedad `hibernate.hbm2ddl.auto` está configurada como `update`:
- **update**: Actualiza el esquema automáticamente (desarrollo)
- **validate**: Solo valida sin modificar (producción recomendado)
- **create**: Recrea las tablas (¡CUIDADO: borra datos!)

### 4.3. Logs SQL
Para desactivar los logs SQL en producción, cambiar en `persistence.xml`:
```xml
<property name="hibernate.show_sql" value="false"/>
```

---

## 5. Verificación de la Instalación

### 5.1. Endpoints de Prueba
Probar los siguientes endpoints:

```bash
# Listar clientes
curl http://localhost:8080/crudxtart-1.0-SNAPSHOT/clientes

# Listar empleados
curl http://localhost:8080/crudxtart-1.0-SNAPSHOT/empleados

# Login
curl -X POST http://localhost:8080/crudxtart-1.0-SNAPSHOT/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password"}'
```

### 5.2. Logs de Tomcat
Revisar los logs en `logs/catalina.out` para verificar:
- Conexión a la base de datos exitosa
- Despliegue del WAR sin errores
- Inicialización de CDI/Weld correcta

---

## 6. Solución de Problemas Comunes

### Error: "Cannot connect to MySQL"
- Verificar que MySQL esté ejecutándose
- Comprobar credenciales en `persistence.xml`
- Verificar que la base de datos `crm_xtart` exista

### Error: "ClassNotFoundException"
- Verificar que todas las dependencias estén en el WAR
- Ejecutar `mvn clean package` nuevamente
- Comprobar que Tomcat tenga Java 21

### Error: "CDI not initialized"
- Verificar que `web.xml` tenga el listener de Weld
- Comprobar que `beans.xml` esté en `META-INF/`

### Error: "Port 8080 already in use"
- Cambiar el puerto en `conf/server.xml`
- O detener el proceso que usa el puerto 8080

---

## 7. Desarrollo con Hot Reload

Para desarrollo activo, usar el plugin de Maven Tomcat:

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8080</port>
        <path>/crudxtart</path>
    </configuration>
</plugin>
```

Ejecutar:
```bash
mvn tomcat7:run
```

---

## 8. Estructura del Proyecto

```
crudxtart/
├── pom.xml                    # Configuración Maven
├── script_bd_Xtart.sql        # Script de base de datos
├── src/
│   └── main/
│       ├── java/              # Código fuente Java
│       ├── resources/
│       │   └── META-INF/
│       │       └── persistence.xml  # Config JPA
│       └── webapp/
│           └── WEB-INF/
│               └── web.xml    # Configuración web
└── target/                    # Archivos compilados
    └── crudxtart-1.0-SNAPSHOT.war
```

---

## Notas Importantes

- **Java 21 es obligatorio** - El proyecto usa características de Java 21
- **Tomcat 10.x** - Compatible con Jakarta EE 9+ (no usar Tomcat 9)
- **MySQL 8.0+** - Requerido para compatibilidad con el driver
- **Charset UTF-8** - La base de datos debe usar `utf8mb4` para caracteres especiales
- **Weld CDI** - Se incluye en el WAR, no requiere configuración adicional en Tomcat

---

## Contacto y Soporte

Para problemas de instalación, revisar:
- Logs de Tomcat: `logs/catalina.out`
- Logs de la aplicación en consola
