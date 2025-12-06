# Diagramas del Sistema - CRUDXTART Backend

Este documento contiene diagramas UML, ER y de flujo que representan la arquitectura y funcionamiento del backend Java CRUDXTART.

## Extensiones Recomendadas

### **Para VS Code**

#### **Markdown Preview Mermaid Support**
Permite que VS Code renderice diagramas Mermaid dentro de archivos Markdown.

#### **Mermaid Markdown Syntax Highlighting**
Añade sintaxis coloreada y reconocimiento de bloques Mermaid.

**Cómo Instalarlas en VS Code:**
1. Abrir **VS Code**.
2. Pulsar **Ctrl + Shift + X** para abrir el panel de extensiones.
3. Buscar cada extensión por su nombre exacto.
4. Pulsar **Install**.

Tras instalarlas, VS Code será capaz de renderizar los diagramas Mermaid integrados en el proyecto.

**Visualizar Diagramas en VS Code:**
1. Abrir el archivo que contiene los diagramas.
2. Pulsar:
   **Ctrl + Shift + V** → *Abrir vista previa Markdown*.
3. Los diagramas Mermaid se renderizan automáticamente en la vista previa.

---

### **Para IntelliJ IDEA**

#### **Opción 1: Plugin Mermaid (Recomendado)**

**Instalación:**
1. Abrir **IntelliJ IDEA**.
2. Ir a `File` → `Settings` (o `IntelliJ IDEA` → `Preferences` en Mac).
3. Navegar a `Plugins` → `Marketplace`.
4. Buscar "**Mermaid**" o "**Mermaid Support**".
5. Instalar uno de los plugins disponibles:
   - **Mermaid** (por JetBrains o desarrolladores de la comunidad)
   - **Mermaid Support**
   - **Markdown Mermaid Support**

**Uso:**
1. Abrir el archivo `.md` con diagramas Mermaid.
2. Abrir la vista previa de Markdown:
   - **Windows/Linux**: `Ctrl + Alt + P`
   - **Mac**: `Cmd + Alt + P`
   - O clic derecho en el archivo → `Open Preview`
3. Los diagramas Mermaid deberían renderizarse en la vista previa.

#### **Opción 2: Plugin Markdown Navigator Enhanced**

**Instalación:**
1. `Settings` → `Plugins` → `Marketplace`.
2. Buscar "**Markdown Navigator Enhanced**" o "**Markdown**".
3. Instalar el plugin (suele incluir soporte Mermaid).

**Uso:**
- Abrir el archivo `.md` y activar la vista previa.
- Los diagramas Mermaid se renderizan automáticamente.

#### **Opción 3: Visualización Externa (Si no funciona en IntelliJ)**

Si los plugins no funcionan correctamente, puedes usar herramientas externas:

**Mermaid Live Editor (Navegador):**
1. Abrir https://mermaid.live en tu navegador.
2. Copiar el código del diagrama (entre ```mermaid y ```).
3. Pegarlo en el editor.
4. El diagrama se renderiza automáticamente.
5. Opcional: Exportar como PNG/SVG desde el menú `Actions`.

**Exportar a Imagen:**
- En Mermaid Live Editor: `Actions` → `Download PNG` o `Download SVG`.
- Útil para incluir imágenes en documentación o presentaciones.

#### **Verificación Rápida en IntelliJ:**

Si ya tienes un plugin instalado:
1. Abrir `DIAGRAMAS.md` en IntelliJ.
2. Presionar `Ctrl + Alt + P` (o `Cmd + Alt + P` en Mac) para abrir la vista previa.
3. O clic derecho en el archivo → `Open Preview`.

Si los diagramas no se renderizan:
- Verificar que el plugin esté activado en `Settings` → `Plugins` → `Installed`.
- Reiniciar IntelliJ IDEA.
- Probar con otro plugin de Mermaid.

---

## 1. Diagrama de Arquitectura General

```mermaid
graph TB
    subgraph "Cliente Python"
        A[Cliente de Escritorio] -->|HTTP Requests| B[RESTClient]
    end
    
    subgraph "Backend Java - Tomcat 10"
        B -->|JSON| C[Servlets REST]
        
        subgraph "Capa Controller"
            C --> C1[ClienteServlet]
            C --> C2[EmpleadoServlet]
            C --> C3[FacturaServlet]
            C --> C4[PresupuestosServlet]
            C --> C5[PagosServlet]
            C --> C6[ProductoServlet]
            C --> C7[InformesServlet]
            C --> C8[LoginServlet]
        end
        
        subgraph "Capa Service"
            C1 --> S1[ClienteService]
            C2 --> S2[EmpleadoService]
            C3 --> S3[FacturaService]
            C4 --> S4[PresupuestosService]
            C5 --> S5[PagosService]
            C6 --> S6[ProductoService]
            C7 --> S7[FacturaService]
            C7 --> S4
        end
        
        subgraph "Capa Repository"
            S1 --> R1[ClienteRepository]
            S2 --> R2[EmpleadoRepository]
            S3 --> R3[FacturaRepository]
            S4 --> R4[PresupuestosRepository]
            S5 --> R5[PagosRepository]
            S6 --> R6[ProductoRepository]
        end
        
        subgraph "Capa Model"
            R1 --> M1[Cliente Entity]
            R2 --> M2[Empleado Entity]
            R3 --> M3[Factura Entity]
            R4 --> M4[Presupuestos Entity]
            R5 --> M5[Pagos Entity]
            R6 --> M6[Producto Entity]
        end
    end
    
    subgraph "Base de Datos"
        M1 -->|JPA/Hibernate| DB[(MySQL)]
        M2 -->|JPA/Hibernate| DB
        M3 -->|JPA/Hibernate| DB
        M4 -->|JPA/Hibernate| DB
        M5 -->|JPA/Hibernate| DB
        M6 -->|JPA/Hibernate| DB
    end
    
    style A fill:#e1f5ff
    style C fill:#ffe1f5
    style S1 fill:#fff4e1
    style S2 fill:#fff4e1
    style S3 fill:#fff4e1
    style R1 fill:#e1ffe1
    style DB fill:#ffcccc
```

---

## 2. Diagrama de Clases UML - Arquitectura en Capas

```mermaid
classDiagram
    class Servlet {
        <<abstract>>
        +doGet(req, resp)
        +doPost(req, resp)
        +doPut(req, resp)
        +doDelete(req, resp)
    }
    
    class ClienteServlet {
        -ClienteService clienteServicedef get_all(self, entity_name: str)
        -EmpleadoService empleadoService
        +doGet()
        +doPost()
        +doPut()
        +doDelete()
    }
    
    class ClienteService {
        -ClienteRepository repository
        +findAllClientes() List
        +findClienteById(id) Cliente
        +createCliente(cliente) Cliente
        +updateCliente(cliente) Cliente
        +deleteCliente(id) void
        -validarCliente(cliente) void
    }
    
    class ClienteRepository {
        <<interface>>
        +findAllClientes() List
        +findClienteById(id) Cliente
        +createCliente(cliente) Cliente
        +updateCliente(cliente) Cliente
        +deletebyid(id) void
    }
    
    class ClienteRepositoryImpl {
        -EntityManager em
        +findAllClientes() List
        +findClienteById(id) Cliente
        +createCliente(cliente) Cliente
        +updateCliente(cliente) Cliente
    }
    
    class Cliente {
        -Integer id_cliente
        -String nombre
        -String email
        -String telefono
        -String tipo_cliente
        -String password
        -Date fecha_alta
        -Empleado empleado_responsable
    }
    
    class EntityManager {
        +createQuery(query) Query
        +find(entityClass, id) Object
        +persist(entity) void
        +merge(entity) Object
        +remove(entity) void
    }
    
    class InformesServlet {
        -ExecutorService executor
        -FacturaService facturaService
        -PresupuestosService presupuestosService
        +doGet() void
        -ejecutarConExecutor(tarea) T
        -handleVentasPorEmpleado() List
        -handleFacturacionMensual() Map
    }
    
    Servlet <|-- ClienteServlet
    Servlet <|-- InformesServlet
    ClienteServlet --> ClienteService : uses
    ClienteService --> ClienteRepository : uses
    ClienteRepository <|.. ClienteRepositoryImpl : implements
    ClienteRepositoryImpl --> EntityManager : uses
    ClienteRepositoryImpl --> Cliente : manages
    InformesServlet --> ClienteService : uses
```

---

## 3. Diagrama de Secuencia - Operación CRUD (Crear Cliente)

```mermaid
sequenceDiagram
    participant C as Cliente Python
    participant S as ClienteServlet
    participant SV as ClienteService
    participant R as ClienteRepository
    participant EM as EntityManager
    participant DB as MySQL
    
    C->>S: POST /clientes {nombre, email, ...}
    S->>S: Parse JSON body
    S->>S: Crear objeto Cliente
    S->>SV: createCliente(cliente)
    SV->>SV: validarCliente(cliente)
    
    alt Validación exitosa
        SV->>R: createCliente(cliente)
        R->>EM: getTransaction().begin()
        R->>EM: persist(cliente)
        EM->>DB: INSERT INTO clientes ...
        DB-->>EM: ID generado
        EM-->>R: Cliente con ID
        R->>EM: getTransaction().commit()
        EM-->>R: Confirmado
        R-->>SV: Cliente creado
        SV-->>S: Cliente con ID
        S->>S: Serializar a JSON
        S-->>C: {success: true, data: {...}}
    else Error de validación
        SV-->>S: IllegalArgumentException
        S-->>C: {success: false, error: "..."}
    end
```

---

## 4. Diagrama de Secuencia - Generación de Informe con Concurrencia

```mermaid
sequenceDiagram
    participant C as Cliente Python
    participant IS as InformesServlet
    participant ES as ExecutorService
    participant T as Thread Pool
    participant SV as FacturaService
    participant R as FacturaRepository
    participant DB as MySQL
    
    C->>IS: GET /informes/ventas-empleado?desde=X&hasta=Y
    IS->>IS: Parse parámetros
    IS->>ES: ejecutarConExecutor(() -> handleVentasPorEmpleado())
    ES->>T: submit(Callable)
    Note over T: Hilo secundario del pool
    
    T->>T: Crear EntityManager
    T->>R: findAllFacturas()
    R->>DB: SELECT FROM facturas ...
    DB-->>R: Lista de facturas
    R-->>T: List<Factura>
    
    T->>T: Filtrar por fechas
    T->>T: Agrupar por empleado
    T->>T: Sumar totales
    T-->>ES: Resultado procesado
    
    ES->>IS: future.get() - Espera bloqueante
    IS->>IS: Serializar a JSON
    IS-->>C: {success: true, data: [...]}
    
    Note over IS,ES: El servlet espera hasta que<br/>el hilo secundario termine
```

---

## 5. Diagrama Entidad-Relación (Base de Datos)

```mermaid
erDiagram
    ROLES_EMPLEADO ||--o{ EMPLEADOS : "tiene"
    EMPLEADOS ||--o{ CLIENTES : "gestiona"
    EMPLEADOS ||--o{ PRESUPUESTOS : "crea"
    EMPLEADOS ||--o{ FACTURAS : "gestiona"
    CLIENTES ||--o{ PRESUPUESTOS : "solicita"
    CLIENTES ||--o{ FACTURAS : "recibe"
    PRESUPUESTOS ||--o{ PRESUPUESTO_PRODUCTOS : "contiene"
    PRODUCTOS ||--o{ PRESUPUESTO_PRODUCTOS : "está en"
    FACTURAS ||--o{ FACTURA_PRODUCTOS : "contiene"
    PRODUCTOS ||--o{ FACTURA_PRODUCTOS : "está en"
    FACTURAS ||--o{ PAGOS : "tiene"
    
    ROLES_EMPLEADO {
        int id_rol PK
        string nombre_rol UK
    }
    
    EMPLEADOS {
        int id_empleado PK
        string nombre
        string email UK
        string telefono
        string password
        int id_rol FK
        date fecha_ingreso
        string estado
    }
    
    CLIENTES {
        int id_cliente PK
        string nombre
        string email UK
        string telefono
        string direccion
        string tipo_cliente
        string password
        date fecha_alta
        int id_empleado_responsable FK
    }
    
    PRODUCTOS {
        int id_producto PK
        string nombre
        string descripcion
        decimal precio
        string categoria
        boolean activo
    }
    
    PRESUPUESTOS {
        int id_Presupuesto PK
        int id_cliente_pagador FK
        int id_cliente_beneficiario FK
        int id_empleado FK
        date fecha_apertura
        date fecha_cierre
        decimal presupuesto
        string estado
    }
    
    PRESUPUESTO_PRODUCTOS {
        int id_presupuesto_producto PK
        int id_Presupuesto FK
        int id_producto FK
        int cantidad
        decimal precio_unitario
        decimal subtotal
    }
    
    FACTURAS {
        int id_factura PK
        string num_factura UK
        int id_cliente_pagador FK
        int id_empleado FK
        date fecha_emision
        decimal total
        string estado
        string notas
    }
    
    FACTURA_PRODUCTOS {
        int id_factura_producto PK
        int id_factura FK
        int id_producto FK
        int id_cliente_beneficiario FK
        int cantidad
        decimal precio_unitario
        decimal subtotal
    }
    
    PAGOS {
        int id_pago PK
        int id_factura FK
        int id_cliente_pagador FK
        date fecha_pago
        decimal importe
        string metodo_pago
        string estado
    }
```

---

## 6. Diagrama de Flujo - Procesamiento de Petición REST

```mermaid
flowchart TD
    Start([Cliente envía petición HTTP]) --> Parse[Servlet parsea petición]
    Parse --> Method{¿Método HTTP?}
    
    Method -->|GET| GetPath{¿Tiene ID?}
    Method -->|POST| PostValidate[Validar JSON body]
    Method -->|PUT| PutValidate[Validar JSON body + ID]
    Method -->|DELETE| DeleteValidate[Validar ID]
    
    GetPath -->|Sí| GetById[Service.findById]
    GetPath -->|No| GetAll[Service.findAll]
    
    PostValidate --> PostService[Service.create]
    PutValidate --> PutService[Service.update]
    DeleteValidate --> DeleteService[Service.delete]
    
    GetById --> Validate{¿Validación OK?}
    GetAll --> Validate
    PostService --> Validate
    PutService --> Validate
    DeleteService --> Validate
    
    Validate -->|No| Error[Retornar error JSON]
    Validate -->|Sí| Repository[Repository ejecuta operación]
    
    Repository --> JPA[JPA/Hibernate]
    JPA --> DB[(Base de Datos)]
    DB -->|Resultado| JPA
    JPA -->|Entidad| Repository
    Repository -->|Objeto| Service
    Service -->|Datos| Servlet
    
    Servlet --> Serialize[Serializar a JSON con Jackson]
    Serialize --> Response[Enviar respuesta HTTP]
    Error --> Response
    Response --> End([Cliente recibe respuesta])
    
    style Start fill:#e1f5ff
    style DB fill:#ffcccc
    style Response fill:#e1ffe1
    style Error fill:#ffcccc
```

---

## 7. Diagrama de Componentes - Arquitectura del Sistema

```mermaid
graph TB
    subgraph "Capa de Presentación"
        A[Cliente Python]
    end
    
    subgraph "Capa de Control"
        B[Servlets REST]
        B1[ClienteServlet]
        B2[FacturaServlet]
        B3[InformesServlet]
        B4[LoginServlet]
    end
    
    subgraph "Capa de Servicios"
        C[Services]
        C1[ClienteService]
        C2[FacturaService]
        C3[PresupuestosService]
    end
    
    subgraph "Capa de Repositorio"
        D[Repositories]
        D1[ClienteRepository]
        D2[FacturaRepository]
        D3[PresupuestosRepository]
    end
    
    subgraph "Capa de Persistencia"
        E[JPA/Hibernate]
        F[EntityManager]
    end
    
    subgraph "Infraestructura"
        G[MySQL Database]
        H[CDI/Weld]
        I[ExecutorService]
    end
    
    A -->|HTTP/JSON| B
    B --> B1
    B --> B2
    B --> B3
    B --> B4
    
    B1 --> C1
    B2 --> C2
    B3 --> C3
    
    C1 --> D1
    C2 --> D2
    C3 --> D3
    
    D1 --> E
    D2 --> E
    D3 --> E
    
    E --> F
    F --> G
    
    B --> H
    B3 --> I
    
    style A fill:#e1f5ff
    style B fill:#ffe1f5
    style C fill:#fff4e1
    style D fill:#e1ffe1
    style G fill:#ffcccc
```

---

## 8. Diagrama de Paquetes - Estructura Modular

```mermaid
graph TB
    subgraph "com.example.crudxtart"
        subgraph "controller"
            A1[ClienteServlet]
            A2[EmpleadoServlet]
            A3[FacturaServlet]
            A4[PresupuestosServlet]
            A5[PagosServlet]
            A6[ProductoServlet]
            A7[InformesServlet]
            A8[LoginServlet]
            A9[RolesEmpleadoServlet]
            A10[FacturaProductoServlet]
        end
        
        subgraph "service"
            B1[ClienteService]
            B2[EmpleadoService]
            B3[FacturaService]
            B4[PresupuestosService]
            B5[PagosService]
            B6[ProductoService]
            B7[Roles_empleadoService]
            B8[FacturaProductoService]
        end
        
        subgraph "repository"
            C1[ClienteRepository]
            C2[ClienteRepositoryImpl]
            C3[FacturaRepository]
            C4[FacturaRepositoryImpl]
            C5[PresupuestosRepository]
            C6[PresupuestosRepositoryImpl]
        end
        
        subgraph "models"
            D1[Cliente]
            D2[Empleado]
            D3[Factura]
            D4[Presupuestos]
            D5[Pagos]
            D6[Producto]
            D7[FacturaProducto]
            D8[PresupuestoProducto]
            D9[Roles_empleado]
        end
        
        subgraph "config"
            E1[JpaFactory]
        end
        
        subgraph "utils"
            F1[JsonUtil]
        end
    end
    
    A1 --> B1
    A3 --> B3
    A4 --> B4
    
    B1 --> C1
    B3 --> C3
    B4 --> C5
    
    C1 <|.. C2
    C3 <|.. C4
    C5 <|.. C6
    
    C2 --> D1
    C4 --> D3
    C6 --> D4
    
    A1 --> F1
    A3 --> F1
    
    style A1 fill:#ffe1f5
    style B1 fill:#fff4e1
    style C2 fill:#e1ffe1
    style D1 fill:#e1f5ff
```

---

## 9. Diagrama de Estados - Ciclo de Vida de una Factura

```mermaid
stateDiagram-v2
    [*] --> Pendiente: Crear factura
    Pendiente --> Emitida: Fecha de emisión <= hoy
    Emitida --> Pagada: Pago completo registrado
    Emitida --> Vencida: Fin de mes sin pagos
    
    Vencida --> Pagada: Pago registrado
    
    note right of Pendiente
        Estado inicial cuando
        fecha_emision > hoy
    end note
    
    note right of Emitida
        Estado cuando fecha
        de emisión llega
    end note
    
    note right of Vencida
        Estado automático si
        no hay pagos al final
        del mes
    end note
    
    note right of Pagada
        Estado final cuando
        se registran pagos
    end note
```

---

## 10. Diagrama de Flujo - Autenticación y Autorización

```mermaid
flowchart TD
    Start([Cliente envía POST /login]) --> Parse[LoginServlet parsea JSON]
    Parse --> Extract[Extraer email y password]
    Extract --> Find[EmpleadoService.findByEmail]
    Find --> Exists{¿Usuario existe?}
    
    Exists -->|No| Error1[Retornar 401]
    Exists -->|Sí| CheckPass{¿Password correcto?}
    
    CheckPass -->|No| Error1
    CheckPass -->|Sí| GetRole[Obtener rol del empleado]
    GetRole --> BuildResponse[Construir respuesta JSON]
    BuildResponse --> Session[Establecer sesión HTTP]
    Session --> Response[Retornar user_info + rol]
    
    Response --> End([Cliente recibe token/sesión])
    Error1 --> End
    
    style Start fill:#e1f5ff
    style Error1 fill:#ffcccc
    style Response fill:#e1ffe1
```

---

## 11. Diagrama de Concurrencia - Pool de Hilos en Informes

```mermaid
sequenceDiagram
    participant R1 as Request 1
    participant R2 as Request 2
    participant R3 as Request 3
    participant IS as InformesServlet
    participant ES as ExecutorService
    participant T1 as Thread 1
    participant T2 as Thread 2
    participant T3 as Thread 3
    participant DB as MySQL
    
    R1->>IS: GET /informes/ventas-empleado
    IS->>ES: submit(task1)
    ES->>T1: Asignar tarea
    T1->>DB: Consulta SQL
    
    R2->>IS: GET /informes/facturacion-mensual
    IS->>ES: submit(task2)
    ES->>T2: Asignar tarea
    T2->>DB: Consulta SQL
    
    R3->>IS: GET /informes/ventas-producto
    IS->>ES: submit(task3)
    ES->>T3: Asignar tarea
    T3->>DB: Consulta SQL
    
    Note over ES: Pool de 5 hilos<br/>permite hasta 5<br/>informes simultáneos
    
    T1->>DB: Resultado
    DB-->>T1: Datos
    T1-->>ES: Resultado
    ES-->>IS: future.get()
    IS-->>R1: JSON Response
    
    T2->>DB: Resultado
    DB-->>T2: Datos
    T2-->>ES: Resultado
    ES-->>IS: future.get()
    IS-->>R2: JSON Response
    
    T3->>DB: Resultado
    DB-->>T3: Datos
    T3-->>ES: Resultado
    ES-->>IS: future.get()
    IS-->>R3: JSON Response
```

---

## Notas sobre los Diagramas

### Diagrama de Arquitectura General
Muestra la estructura en capas del sistema y cómo fluyen las peticiones desde el cliente hasta la base de datos.

### Diagrama de Clases UML
Representa las clases principales, sus relaciones de herencia, composición y dependencias mediante inyección CDI.

### Diagramas de Secuencia
Ilustran la interacción temporal entre componentes durante operaciones CRUD y generación de informes con concurrencia.

### Diagrama ER
Muestra el esquema completo de la base de datos con todas las relaciones entre entidades.

### Diagramas de Flujo
Describen los procesos de negocio desde la recepción de peticiones hasta la generación de respuestas.

### Diagrama de Estados
Muestra las transiciones de estado de una factura según las reglas de negocio implementadas.

### Diagrama de Concurrencia
Explica cómo el ExecutorService maneja múltiples peticiones de informes en paralelo.

---

## Herramientas para Visualizar

Estos diagramas están escritos en **Mermaid**, que puede visualizarse en:
- GitHub (renderizado automático en archivos .md)
- GitLab
- VS Code (con extensión Mermaid)
- Documentación online (Mermaid Live Editor: https://mermaid.live)
- Herramientas de documentación como MkDocs, Docusaurus, etc.

Para exportar a otros formatos (PNG, SVG, PDF), puedes usar:
- Mermaid CLI: `npm install -g @mermaid-js/mermaid-cli`
- Herramientas online de conversión
- Extensiones de VS Code que permiten exportar

