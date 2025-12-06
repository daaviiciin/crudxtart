# Solución: Error de Conexión a Base de Datos

## 🔴 Error
```
Access denied for user 'root'@'localhost' (using password: YES)
```

Este error indica que el backend Java no puede conectarse a MySQL porque:
- La contraseña es incorrecta
- El usuario no tiene permisos
- MySQL no está ejecutándose
- La base de datos no existe

## ✅ Solución Paso a Paso

### 1. Verificar que MySQL está ejecutándose

**Windows:**
```bash
# Verificar servicio MySQL
sc query MySQL80
# O buscar en "Servicios" de Windows
```

**Si no está ejecutándose:**
```bash
# Iniciar MySQL
net start MySQL80
# O desde "Servicios" de Windows
```

### 2. Verificar la contraseña de MySQL

**Opción A: Probar la contraseña actual**

Abre MySQL Command Line Client o MySQL Workbench e intenta conectarte:
```sql
mysql -u root -p
# Ingresa la contraseña: 1234
```

**Si la contraseña es incorrecta:**

**Opción B: Cambiar la contraseña de root**

1. Detén MySQL si está ejecutándose
2. Inicia MySQL en modo seguro (sin verificación de contraseña):
   ```bash
   mysqld --skip-grant-tables
   ```
3. En otra terminal, conéctate sin contraseña:
   ```bash
   mysql -u root
   ```
4. Cambia la contraseña:
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';
   FLUSH PRIVILEGES;
   EXIT;
   ```
5. Reinicia MySQL normalmente

**Opción C: Usar la contraseña correcta**

Si conoces la contraseña correcta de root, actualiza el archivo `persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.password" value="TU_CONTRASEÑA_AQUI"/>
```

### 3. Verificar que la base de datos existe

Conéctate a MySQL y verifica:
```sql
mysql -u root -p
```

```sql
SHOW DATABASES;
```

Si no existe `crm_xtart`, créala:
```sql
CREATE DATABASE IF NOT EXISTS crm_xtart
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;
```

### 4. Verificar permisos del usuario

Asegúrate de que el usuario `root` tiene permisos:
```sql
SHOW GRANTS FOR 'root'@'localhost';
```

Si no tiene permisos, otórgalos:
```sql
GRANT ALL PRIVILEGES ON crm_xtart.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### 5. Crear un usuario específico para la aplicación (Recomendado)

En lugar de usar `root`, es mejor crear un usuario específico:

```sql
-- Crear usuario
CREATE USER 'crm_user'@'localhost' IDENTIFIED BY 'crm_password123';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON crm_xtart.* TO 'crm_user'@'localhost';
FLUSH PRIVILEGES;
```

Luego actualiza `persistence.xml`:
```xml
<property name="jakarta.persistence.jdbc.user" value="crm_user"/>
<property name="jakarta.persistence.jdbc.password" value="crm_password123"/>
```

### 6. Verificar la configuración en persistence.xml

El archivo está en: `crudxtart/src/main/resources/META-INF/persistence.xml`

Configuración actual:
```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/crm_xtart"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="1234"/>
<property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
```

**Ajusta según tu configuración:**
- Si MySQL está en otro puerto, cambia `3306`
- Si la base de datos tiene otro nombre, cámbialo
- Si usas otro usuario/contraseña, actualízalos

### 7. Probar la conexión manualmente

Desde la línea de comandos:
```bash
mysql -u root -p1234 -h localhost -P 3306 crm_xtart
```

Si funciona, el problema está en la configuración de Java.
Si no funciona, el problema está en MySQL.

## 🔧 Configuración Recomendada

### Crear usuario y base de datos desde cero:

```sql
-- 1. Crear base de datos
CREATE DATABASE IF NOT EXISTS crm_xtart
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

-- 2. Crear usuario
CREATE USER 'crm_user'@'localhost' IDENTIFIED BY 'crm_password123';

-- 3. Otorgar permisos
GRANT ALL PRIVILEGES ON crm_xtart.* TO 'crm_user'@'localhost';
FLUSH PRIVILEGES;

-- 4. Verificar
SHOW GRANTS FOR 'crm_user'@'localhost';
```

### Actualizar persistence.xml:

```xml
<property name="jakarta.persistence.jdbc.user" value="crm_user"/>
<property name="jakarta.persistence.jdbc.password" value="crm_password123"/>
```

## 📝 Ejecutar el script de base de datos

Una vez configurada la conexión, ejecuta el script SQL:

```bash
mysql -u root -p1234 crm_xtart < script_bd_corregido.sql
```

O desde MySQL Workbench:
1. Abre el archivo `script_bd_corregido.sql`
2. Ejecuta todo el script

## ⚠️ Solución Rápida (Solo para desarrollo)

Si estás en desarrollo y quieres una solución rápida:

1. **Reinicia MySQL:**
   ```bash
   net stop MySQL80
   net start MySQL80
   ```

2. **Prueba con contraseña vacía** (si es posible en tu instalación):
   ```xml
   <property name="jakarta.persistence.jdbc.password" value=""/>
   ```

3. **O restablece la contraseña de root a "1234"** (solo desarrollo):
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';
   FLUSH PRIVILEGES;
   ```

## 🆘 Verificación Final

Después de aplicar los cambios:

1. **Reinicia el servidor Tomcat/Java**
2. **Verifica los logs** del servidor
3. **Intenta hacer login** desde el cliente Python

Si el error persiste, verifica:
- ✅ MySQL está ejecutándose
- ✅ La contraseña es correcta
- ✅ La base de datos `crm_xtart` existe
- ✅ El usuario tiene permisos
- ✅ El puerto 3306 está disponible
- ✅ El driver MySQL está en el classpath del proyecto

