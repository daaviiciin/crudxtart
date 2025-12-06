CREATE DATABASE IF NOT EXISTS crm_xtart
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE crm_xtart;

-- =========================================================
--  1. Tabla: roles_empleado
-- =========================================================
CREATE TABLE roles_empleado (
  id_rol INT AUTO_INCREMENT PRIMARY KEY,
  nombre_rol VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================
--  2. Tabla: empleados
-- =========================================================
CREATE TABLE empleados (
  id_empleado INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  telefono VARCHAR(20),
  password VARCHAR(255) NOT NULL,
  id_rol INT NOT NULL,
  fecha_ingreso DATE,
  estado VARCHAR(50) DEFAULT 'activo',
  CONSTRAINT fk_empleado_rol FOREIGN KEY (id_rol)
    REFERENCES roles_empleado(id_rol)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_empleados_email ON empleados(email);

-- =========================================================
--  3. Tabla: clientes
-- =========================================================
CREATE TABLE clientes (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  email VARCHAR(150) UNIQUE,
  telefono VARCHAR(20),
  tipo_cliente VARCHAR(50), -- PARTICULAR o EMPRESA
  password VARCHAR(255) NOT NULL,
  fecha_alta DATE,
  id_empleado_responsable INT,
  CONSTRAINT fk_cliente_empleado FOREIGN KEY (id_empleado_responsable)
    REFERENCES empleados(id_empleado)
    ON UPDATE CASCADE
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_tipo ON clientes(tipo_cliente);

-- =========================================================
--  4. Tabla: productos
-- =========================================================
CREATE TABLE productos (
  id_producto INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  descripcion TEXT,
  categoria VARCHAR(50) NOT NULL, -- CICLO FORMATIVO o FORMACION COMPLEMENTARIA
  precio DECIMAL(10,2) NOT NULL,
  activo BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_productos_categoria ON productos(categoria);

-- =========================================================
--  5. Tabla: presupuestos
-- =========================================================
CREATE TABLE presupuestos (
  id_presupuesto INT AUTO_INCREMENT PRIMARY KEY,
  id_empleado INT NOT NULL,
  id_cliente_pagador INT NOT NULL,
  id_cliente_beneficiario INT NOT NULL,
  presupuesto DECIMAL(10,2) NOT NULL,
  estado VARCHAR(50) DEFAULT 'PENDIENTE', -- PENDIENTE, APROBADO, RECHAZADO
  fecha_apertura DATE,
  fecha_cierre DATE,
  CONSTRAINT fk_presupuesto_empleado FOREIGN KEY (id_empleado)
    REFERENCES empleados(id_empleado)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_presupuesto_pagador FOREIGN KEY (id_cliente_pagador)
    REFERENCES clientes(id_cliente)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_presupuesto_beneficiario FOREIGN KEY (id_cliente_beneficiario)
    REFERENCES clientes(id_cliente)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_presupuestos_estado ON presupuestos(estado);
CREATE INDEX idx_presupuestos_fecha_apertura ON presupuestos(fecha_apertura);

-- =========================================================
--  5B. Tabla: presupuesto_productos
-- =========================================================
CREATE TABLE presupuesto_productos (
  id_presupuesto_producto INT AUTO_INCREMENT PRIMARY KEY,
  id_presupuesto INT NOT NULL,
  id_producto INT NOT NULL,
  id_cliente_beneficiario INT NOT NULL,
  cantidad INT DEFAULT 1,
  precio_unitario DECIMAL(10,2),
  subtotal DECIMAL(10,2),
  CONSTRAINT fk_presupuesto_producto_presupuesto FOREIGN KEY (id_presupuesto)
    REFERENCES presupuestos(id_presupuesto)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_presupuesto_producto_producto FOREIGN KEY (id_producto)
    REFERENCES productos(id_producto)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_presupuesto_producto_beneficiario FOREIGN KEY (id_cliente_beneficiario)
    REFERENCES clientes(id_cliente)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================
--  6. Tabla: facturas
-- =========================================================
CREATE TABLE facturas (
  id_factura INT AUTO_INCREMENT PRIMARY KEY,
  num_factura VARCHAR(50) NOT NULL UNIQUE,
  id_cliente_pagador INT NOT NULL,
  id_empleado INT,
  fecha_emision DATE NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(50) DEFAULT 'PENDIENTE', -- PENDIENTE, EMITIDA, PAGADA, VENCIDA
  notas TEXT,
  CONSTRAINT fk_factura_pagador FOREIGN KEY (id_cliente_pagador)
    REFERENCES clientes(id_cliente)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_factura_empleado FOREIGN KEY (id_empleado)
    REFERENCES empleados(id_empleado)
    ON UPDATE CASCADE
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_facturas_estado ON facturas(estado);
CREATE INDEX idx_facturas_fecha ON facturas(fecha_emision);

-- =========================================================
--  7. Tabla: factura_productos
-- =========================================================
CREATE TABLE factura_productos (
  id_factura_producto INT AUTO_INCREMENT PRIMARY KEY,
  id_factura INT NOT NULL,
  id_producto INT NOT NULL,
  id_cliente_beneficiario INT NOT NULL,
  cantidad INT DEFAULT 1,
  precio_unitario DECIMAL(10,2),
  subtotal DECIMAL(10,2),
  CONSTRAINT fk_factura_producto_factura FOREIGN KEY (id_factura)
    REFERENCES facturas(id_factura)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_factura_producto_producto FOREIGN KEY (id_producto)
    REFERENCES productos(id_producto)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_factura_producto_beneficiario FOREIGN KEY (id_cliente_beneficiario)
    REFERENCES clientes(id_cliente)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_factura_productos_factura ON factura_productos(id_factura);
CREATE INDEX idx_factura_productos_beneficiario ON factura_productos(id_cliente_beneficiario);

-- =========================================================
--  8. Tabla: pagos
-- =========================================================
CREATE TABLE pagos (
  id_pago INT AUTO_INCREMENT PRIMARY KEY,
  id_factura INT NOT NULL,
  fecha_pago DATE,
  importe DECIMAL(10,2),
  metodo_pago VARCHAR(50), -- TRANSFERENCIA, TARJETA, EFECTIVO
  estado VARCHAR(50) DEFAULT 'PENDIENTE', -- PENDIENTE, PAGADA, CANCELADA
  CONSTRAINT fk_pago_factura FOREIGN KEY (id_factura)
    REFERENCES facturas(id_factura)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_pagos_estado ON pagos(estado);
CREATE INDEX idx_pagos_fecha ON pagos(fecha_pago);

-- =========================================================
-- FIN DEL SCRIPT CORREGIDO
-- =========================================================

USE crm_xtart;

-- =========================================================
-- ROLES DE EMPLEADOS
-- =========================================================
INSERT INTO roles_empleado (nombre_rol)
VALUES 
  ('admin'),
  ('comercial'),
  ('gestor'),
  ('direccion');

-- =========================================================
-- EMPLEADOS
-- =========================================================
INSERT INTO empleados (nombre, email, telefono, password, id_rol, fecha_ingreso, estado)
VALUES
  ('Laura Martínez', 'laura.martinez@xtart.es', '600123456', 'com123', 2, '2021-09-01', 'activo'),
  ('Carlos Gómez', 'carlos.gomez@xtart.es', '600234567', 'com123', 2, '2022-01-10', 'activo'),
  ('María López', 'maria.lopez@xtart.es', '600345678', 'gestor123', 3, '2020-06-15', 'activo'),
  ('Javier Ruiz', 'admin@admin.es', '600456789', '123', 1, '2019-03-20', 'activo'),
  ('Ana Torres', 'ana.torres@xtart.es', '600567890', 'dir123', 4, '2020-11-10', 'activo');

-- =========================================================
-- CLIENTES (pagadores y beneficiarios)
-- =========================================================
INSERT INTO clientes (nombre, email, telefono, tipo_cliente, password, fecha_alta, id_empleado_responsable)
VALUES
  ('Juan Pérez', 'juan.perez@gmail.com', '611111111', 'PARTICULAR', 'cliente123', '2023-09-01', 1),
  ('María Sánchez', 'maria.sanchez@gmail.com', '622222222', 'PARTICULAR', 'cliente123', '2023-10-10', 2),
  ('Pedro Romero', 'pedro.romero@gmail.com', '633333333', 'PARTICULAR', 'cliente123', '2023-08-20', 1),
  ('Lucía Fernández', 'lucia.fernandez@gmail.com', '644444444', 'PARTICULAR', 'cliente123', '2023-07-05', 2),
  ('Academia TechPro', 'contacto@techpro.com', '955000111', 'EMPRESA', 'cliente123', '2023-04-01', 1),
  ('Colegio Innovar', 'info@innovar.edu', '955000222', 'EMPRESA', 'cliente123', '2023-05-01', 2),
  ('José Ramírez', 'jose.ramirez@gmail.com', '655555555', 'PARTICULAR', 'cliente123', '2024-01-12', 2),
  ('Claudia Núñez', 'claudia.nunez@gmail.com', '666666666', 'PARTICULAR', 'cliente123', '2024-02-05', 1),
  ('Esteban Mora', 'esteban.mora@gmail.com', '677777777', 'PARTICULAR', 'cliente123', '2024-03-10', 2),
  ('Empresa FormarPlus', 'info@formarplus.com', '688888888', 'EMPRESA', 'cliente123', '2023-09-20', 1);

-- =========================================================
-- PRODUCTOS (cursos y formaciones)
-- =========================================================
INSERT INTO productos (nombre, descripcion, categoria, precio, activo)
VALUES
  ('Ciclo Formativo DAM', 'Desarrollo de Aplicaciones Multiplataforma', 'CICLO FORMATIVO', 3200.00, TRUE),
  ('Ciclo Formativo DAW', 'Desarrollo de Aplicaciones Web', 'CICLO FORMATIVO', 3100.00, TRUE),
  ('Curso Python Avanzado', 'Formación complementaria en Python', 'FORMACION COMPLEMENTARIA', 350.00, TRUE),
  ('Curso Ciberseguridad', 'Técnicas y buenas prácticas de seguridad informática', 'FORMACION COMPLEMENTARIA', 400.00, TRUE),
  ('Curso Diseño UX/UI', 'Diseño centrado en el usuario para aplicaciones', 'FORMACION COMPLEMENTARIA', 300.00, TRUE),
  ('Ciclo Formativo Marketing Digital', 'Formación avanzada en marketing digital', 'CICLO FORMATIVO', 2800.00, TRUE),
  ('Curso Inglés Técnico', 'Mejora del inglés profesional', 'FORMACION COMPLEMENTARIA', 250.00, TRUE);

-- =========================================================
-- PRESUPUESTOS
-- =========================================================
INSERT INTO presupuestos (id_empleado, id_cliente_pagador, id_cliente_beneficiario, presupuesto, estado, fecha_apertura, fecha_cierre)
VALUES
  (1, 1, 1, 3200.00, 'APROBADO', '2023-09-10', '2023-09-20'),
  (2, 2, 2, 3100.00, 'APROBADO', '2023-10-15', '2023-10-20'),
  (1, 5, 3, 3200.00, 'APROBADO', '2023-08-01', '2023-08-15'),
  (2, 6, 4, 2800.00, 'APROBADO', '2023-06-01', '2023-06-20'),
  (1, 1, 1, 350.00, 'APROBADO', '2023-11-01', '2023-11-05'),
  (2, 2, 2, 400.00, 'APROBADO', '2023-12-01', '2023-12-05'),
  (1, 5, 7, 3200.00, 'PENDIENTE', '2024-01-15', NULL),
  (2, 10, 8, 2800.00, 'PENDIENTE', '2024-02-01', NULL),
  (1, 10, 9, 300.00, 'APROBADO', '2024-03-01', '2024-03-05'),
  (2, 3, 3, 3200.00, 'RECHAZADO', '2024-01-20', NULL),
  (1, 4, 4, 3100.00, 'PENDIENTE', '2024-02-10', NULL);

-- =========================================================
-- PRESUPUESTO_PRODUCTOS
-- =========================================================
INSERT INTO presupuesto_productos (id_presupuesto, id_producto, id_cliente_beneficiario, cantidad, precio_unitario, subtotal)
VALUES
  (1, 1, 1, 1, 3200.00, 3200.00),
  (2, 2, 2, 1, 3100.00, 3100.00),
  (3, 1, 3, 1, 3200.00, 3200.00),
  (4, 6, 4, 1, 2800.00, 2800.00),
  (5, 3, 1, 1, 350.00, 350.00),
  (6, 4, 2, 1, 400.00, 400.00),
  (7, 1, 7, 1, 3200.00, 3200.00),
  (8, 6, 8, 1, 2800.00, 2800.00),
  (9, 5, 9, 1, 300.00, 300.00),
  (10, 1, 3, 1, 3200.00, 3200.00),
  (11, 2, 4, 1, 3100.00, 3100.00);

-- =========================================================
-- FACTURAS
-- =========================================================
INSERT INTO facturas (num_factura, id_cliente_pagador, id_empleado, fecha_emision, total, estado, notas)
VALUES
  ('F-2023-001', 1, 1, '2023-09-21', 3550.00, 'PAGADA', 'Incluye DAM + curso Python'),
  ('F-2023-002', 5, 1, '2023-08-16', 3200.00, 'EMITIDA', 'Formación de Pedro Romero'),
  ('F-2023-003', 6, 2, '2023-06-22', 2800.00, 'PAGADA', 'Formación de Lucía Fernández'),
  ('F-2023-004', 2, 2, '2023-12-06', 3500.00, 'PAGADA', 'DAW + Ciberseguridad'),
  ('F-2024-001', 10, 1, '2024-03-10', 3100.00, 'EMITIDA', 'Marketing Digital + UX/UI'),
  ('F-2024-002', 1, 1, '2024-01-25', 3200.00, 'PENDIENTE', 'Factura pendiente de emisión'),
  ('F-2024-003', 2, 2, '2024-02-15', 400.00, 'VENCIDA', 'Factura vencida sin pago');

-- =========================================================
-- FACTURA_PRODUCTOS
-- =========================================================
INSERT INTO factura_productos (id_factura, id_producto, id_cliente_beneficiario, cantidad, precio_unitario, subtotal)
VALUES
  (1, 1, 1, 1, 3200.00, 3200.00),
  (1, 3, 1, 1, 350.00, 350.00),
  (2, 1, 3, 1, 3200.00, 3200.00),
  (3, 6, 4, 1, 2800.00, 2800.00),
  (4, 2, 2, 1, 3100.00, 3100.00),
  (4, 4, 2, 1, 400.00, 400.00),
  (5, 6, 8, 1, 2800.00, 2800.00),
  (5, 5, 9, 1, 300.00, 300.00),
  (6, 1, 1, 1, 3200.00, 3200.00),
  (7, 4, 2, 1, 400.00, 400.00);

-- =========================================================
-- PAGOS
-- =========================================================
INSERT INTO pagos (id_factura, fecha_pago, importe, metodo_pago, estado)
VALUES
  (1, '2023-09-22', 3550.00, 'TRANSFERENCIA', 'PAGADA'),
  (2, '2023-08-20', 3200.00, 'TARJETA', 'PAGADA'),
  (3, '2023-06-25', 2800.00, 'TRANSFERENCIA', 'PAGADA'),
  (4, '2023-12-10', 2000.00, 'EFECTIVO', 'PAGADA'),
  (4, '2023-12-15', 1500.00, 'TRANSFERENCIA', 'PAGADA'),
  (5, '2024-03-20', 1500.00, 'TARJETA', 'PENDIENTE'),
  (5, '2024-03-25', 1600.00, 'TRANSFERENCIA', 'PAGADA'),
  (6, '2024-01-30', 3200.00, 'TARJETA', 'PAGADA'),
  (7, '2024-02-20', 200.00, 'EFECTIVO', 'CANCELADA');

-- =========================================================
-- FIN DE LOS INSERTS DE PRUEBA
-- =========================================================

-- =========================================================
-- AMPLIACIÓN DATOS DE PRUEBA PARA INFORMES
-- =========================================================

-- =========================================================
-- CLIENTES ADICIONALES (para más variedad)
-- =========================================================
INSERT INTO clientes (nombre, email, telefono, tipo_cliente, password, fecha_alta, id_empleado_responsable)
VALUES
  ('Roberto Silva', 'roberto.silva@email.com', '690000001', 'PARTICULAR', 'cliente123', '2022-05-15', 1),
  ('Carmen Vega', 'carmen.vega@email.com', '690000002', 'PARTICULAR', 'cliente123', '2022-08-20', 2),
  ('Diego Morales', 'diego.morales@email.com', '690000003', 'PARTICULAR', 'cliente123', '2023-01-10', 1),
  ('Sofía Herrera', 'sofia.herrera@email.com', '690000004', 'PARTICULAR', 'cliente123', '2023-03-25', 2),
  ('Miguel Castro', 'miguel.castro@email.com', '690000005', 'PARTICULAR', 'cliente123', '2023-06-12', 1),
  ('Instituto Tecnológico Madrid', 'info@itmadrid.edu', '911111111', 'EMPRESA', 'cliente123', '2022-11-01', 1),
  ('Centro Formación Barcelona', 'formacion@cfbarcelona.com', '932222222', 'EMPRESA', 'cliente123', '2023-02-14', 2),
  ('Academia Digital Valencia', 'contacto@advalencia.es', '963333333', 'EMPRESA', 'cliente123', '2023-07-08', 1),
  ('Elena Domínguez', 'elena.dominguez@email.com', '690000006', 'PARTICULAR', 'cliente123', '2024-01-20', 2),
  ('Fernando Jiménez', 'fernando.jimenez@email.com', '690000007', 'PARTICULAR', 'cliente123', '2024-04-05', 1);

-- =========================================================
-- PRODUCTOS ADICIONALES
-- =========================================================
INSERT INTO productos (nombre, descripcion, categoria, precio, activo)
VALUES
  ('Ciclo Formativo ASIR', 'Administración de Sistemas Informáticos en Red', 'CICLO FORMATIVO', 3000.00, TRUE),
  ('Curso Java Enterprise', 'Desarrollo empresarial con Java', 'FORMACION COMPLEMENTARIA', 450.00, TRUE),
  ('Curso React y Node.js', 'Stack completo MERN', 'FORMACION COMPLEMENTARIA', 500.00, TRUE),
  ('Ciclo Formativo Comercio Internacional', 'Gestión de operaciones comerciales', 'CICLO FORMATIVO', 2900.00, TRUE),
  ('Curso Big Data', 'Análisis de datos masivos', 'FORMACION COMPLEMENTARIA', 600.00, TRUE);

-- =========================================================
-- PRESUPUESTOS ADICIONALES (distribuidos en el tiempo)
-- =========================================================
INSERT INTO presupuestos (id_empleado, id_cliente_pagador, id_cliente_beneficiario, presupuesto, estado, fecha_apertura, fecha_cierre)
VALUES
  -- 2022: Presupuestos antiguos (distribuidos entre empleados)
  (1, 11, 11, 3000.00, 'APROBADO', '2022-06-01', '2022-06-15'),
  (2, 12, 12, 3100.00, 'APROBADO', '2022-09-10', '2022-09-25'),
  (3, 16, 13, 3200.00, 'APROBADO', '2022-12-05', '2022-12-20'),
  
  -- 2023: Primer semestre (distribuidos entre todos los empleados)
  (2, 13, 13, 350.00, 'APROBADO', '2023-01-15', '2023-01-20'),
  (3, 14, 14, 400.00, 'APROBADO', '2023-02-10', '2023-02-15'),
  (4, 15, 15, 500.00, 'APROBADO', '2023-03-20', '2023-03-25'),
  (5, 17, 16, 3000.00, 'APROBADO', '2023-04-12', '2023-04-28'),
  (1, 18, 17, 2900.00, 'APROBADO', '2023-05-08', '2023-05-22'),
  
  -- 2023: Segundo semestre (rotación de empleados)
  (2, 11, 11, 450.00, 'APROBADO', '2023-07-01', '2023-07-10'),
  (3, 12, 12, 600.00, 'APROBADO', '2023-08-15', '2023-08-30'),
  (4, 13, 13, 3200.00, 'APROBADO', '2023-09-05', '2023-09-20'),
  (5, 14, 14, 3100.00, 'APROBADO', '2023-10-10', '2023-10-25'),
  (1, 15, 15, 500.00, 'APROBADO', '2023-11-12', '2023-11-18'),
  (2, 16, 11, 3000.00, 'APROBADO', '2023-11-20', '2023-12-05'),
  
  -- 2024: Primer semestre (todos los empleados)
  (3, 17, 12, 2900.00, 'APROBADO', '2024-01-08', '2024-01-22'),
  (4, 18, 13, 450.00, 'APROBADO', '2024-01-15', '2024-01-20'),
  (5, 19, 14, 600.00, 'APROBADO', '2024-02-10', '2024-02-15'),
  (1, 20, 15, 3200.00, 'APROBADO', '2024-03-05', '2024-03-20'),
  (2, 11, 16, 3100.00, 'APROBADO', '2024-04-01', '2024-04-15'),
  (3, 12, 17, 500.00, 'APROBADO', '2024-04-20', '2024-04-25'),
  
  -- 2024: Segundo semestre
  (4, 13, 18, 3000.00, 'APROBADO', '2024-05-10', '2024-05-25'),
  (5, 14, 19, 2900.00, 'APROBADO', '2024-06-05', '2024-06-20'),
  (1, 15, 20, 450.00, 'APROBADO', '2024-07-01', '2024-07-10'),
  (2, 16, 11, 600.00, 'APROBADO', '2024-08-15', '2024-08-30'),
  (3, 17, 12, 3200.00, 'APROBADO', '2024-09-10', '2024-09-25'),
  (4, 18, 13, 3100.00, 'APROBADO', '2024-10-05', '2024-10-20'),
  (5, 19, 14, 500.00, 'APROBADO', '2024-11-01', '2024-11-10'),
  (1, 20, 15, 3000.00, 'APROBADO', '2024-11-15', '2024-11-30'),
  
  -- 2025: Primer semestre
  (2, 11, 16, 2900.00, 'APROBADO', '2025-01-10', '2025-01-25'),
  (3, 12, 17, 450.00, 'APROBADO', '2025-02-05', '2025-02-20'),
  (4, 13, 18, 600.00, 'APROBADO', '2025-03-12', '2025-03-28'),
  (5, 14, 19, 3200.00, 'APROBADO', '2025-04-08', '2025-04-22'),
  (1, 15, 20, 3100.00, 'APROBADO', '2025-05-15', '2025-05-30'),
  (2, 16, 11, 500.00, 'APROBADO', '2025-06-10', '2025-06-25'),
  
  -- 2025: Segundo semestre (hasta actualidad 05/12/2025)
  (3, 17, 12, 3000.00, 'APROBADO', '2025-07-05', '2025-07-20'),
  (4, 18, 13, 2900.00, 'APROBADO', '2025-08-12', '2025-08-28'),
  (5, 19, 14, 450.00, 'APROBADO', '2025-09-08', '2025-09-22'),
  (1, 20, 15, 600.00, 'APROBADO', '2025-10-15', '2025-10-30'),
  (2, 11, 16, 3200.00, 'APROBADO', '2025-11-05', '2025-11-20'),
  (3, 12, 17, 3100.00, 'APROBADO', '2025-11-25', '2025-12-05'),
  
  -- Presupuestos pendientes y rechazados (variedad de estados y empleados hasta actualidad)
  (4, 13, 18, 2900.00, 'PENDIENTE', '2025-12-01', NULL),
  (5, 14, 19, 450.00, 'PENDIENTE', '2025-12-03', NULL),
  (1, 15, 20, 600.00, 'RECHAZADO', '2025-11-28', NULL),
  (2, 16, 11, 3200.00, 'PENDIENTE', '2025-12-05', NULL),
  (3, 17, 12, 3100.00, 'RECHAZADO', '2025-11-15', NULL);

-- =========================================================
-- PRESUPUESTO_PRODUCTOS ADICIONALES
-- =========================================================
INSERT INTO presupuesto_productos (id_presupuesto, id_producto, id_cliente_beneficiario, cantidad, precio_unitario, subtotal)
VALUES
  -- Presupuestos 2022
  (12, 8, 11, 1, 3000.00, 3000.00),
  (13, 2, 12, 1, 3100.00, 3100.00),
  (14, 1, 13, 1, 3200.00, 3200.00),
  
  -- Presupuestos 2023 primer semestre
  (15, 3, 13, 1, 350.00, 350.00),
  (16, 4, 14, 1, 400.00, 400.00),
  (17, 9, 15, 1, 500.00, 500.00),
  (18, 8, 16, 1, 3000.00, 3000.00),
  (19, 11, 17, 1, 2900.00, 2900.00),
  
  -- Presupuestos 2023 segundo semestre
  (20, 9, 11, 1, 450.00, 450.00),
  (21, 12, 12, 1, 600.00, 600.00),
  (22, 1, 13, 1, 3200.00, 3200.00),
  (23, 2, 14, 1, 3100.00, 3100.00),
  (24, 9, 15, 1, 500.00, 500.00),
  (25, 8, 11, 1, 3000.00, 3000.00),
  
  -- Presupuestos 2024
  (26, 11, 12, 1, 2900.00, 2900.00),
  (27, 9, 13, 1, 450.00, 450.00),
  (28, 12, 14, 1, 600.00, 600.00),
  (29, 1, 15, 1, 3200.00, 3200.00),
  (30, 2, 16, 1, 3100.00, 3100.00),
  (31, 9, 17, 1, 500.00, 500.00),
  (32, 8, 18, 1, 3000.00, 3000.00),
  (33, 11, 19, 1, 2900.00, 2900.00),
  (34, 9, 20, 1, 450.00, 450.00),
  (35, 12, 11, 1, 600.00, 600.00),
  (36, 1, 12, 1, 3200.00, 3200.00),
  (37, 2, 13, 1, 3100.00, 3100.00),
  (38, 9, 14, 1, 500.00, 500.00),
  (39, 8, 15, 1, 3000.00, 3000.00),
  (40, 11, 16, 1, 2900.00, 2900.00),
  (41, 9, 17, 1, 450.00, 450.00),
  (42, 12, 18, 1, 600.00, 600.00),
  (43, 1, 19, 1, 3200.00, 3200.00),
  (44, 2, 20, 1, 3100.00, 3100.00),
  (45, 9, 11, 1, 500.00, 500.00),
  (46, 8, 12, 1, 3000.00, 3000.00),
  (47, 11, 13, 1, 2900.00, 2900.00),
  (48, 9, 14, 1, 450.00, 450.00),
  (49, 12, 15, 1, 600.00, 600.00),
  (50, 1, 16, 1, 3200.00, 3200.00);

-- =========================================================
-- FACTURAS ADICIONALES (asociadas a presupuestos y distribuidas en el tiempo)
-- =========================================================
INSERT INTO facturas (num_factura, id_cliente_pagador, id_empleado, fecha_emision, total, estado, notas)
VALUES
  -- Facturas 2022 (distribuidas entre empleados)
  ('F-2022-001', 11, 1, '2022-06-20', 3000.00, 'PAGADA', 'Presupuesto aprobado junio 2022'),
  ('F-2022-002', 12, 2, '2022-09-30', 3100.00, 'PAGADA', 'Presupuesto aprobado septiembre 2022'),
  ('F-2022-003', 16, 3, '2022-12-25', 3200.00, 'PAGADA', 'Presupuesto aprobado diciembre 2022'),
  
  -- Facturas 2023 primer semestre (todos los empleados)
  ('F-2023-005', 13, 2, '2023-01-25', 350.00, 'PAGADA', 'Curso Python'),
  ('F-2023-006', 14, 3, '2023-02-20', 400.00, 'PAGADA', 'Curso Ciberseguridad'),
  ('F-2023-007', 15, 4, '2023-03-30', 500.00, 'PAGADA', 'Curso React'),
  ('F-2023-008', 17, 5, '2023-05-05', 3000.00, 'PAGADA', 'Ciclo ASIR'),
  ('F-2023-009', 18, 1, '2023-05-28', 2900.00, 'PAGADA', 'Ciclo Comercio'),
  
  -- Facturas 2023 segundo semestre (rotación de empleados)
  ('F-2023-010', 11, 2, '2023-07-15', 450.00, 'PAGADA', 'Curso Java'),
  ('F-2023-011', 12, 3, '2023-09-05', 600.00, 'PAGADA', 'Curso Big Data'),
  ('F-2023-012', 13, 4, '2023-09-25', 3200.00, 'PAGADA', 'Ciclo DAM'),
  ('F-2023-013', 14, 5, '2023-10-30', 3100.00, 'PAGADA', 'Ciclo DAW'),
  ('F-2023-014', 15, 1, '2023-11-25', 500.00, 'PAGADA', 'Curso React'),
  ('F-2023-015', 16, 2, '2023-12-10', 3000.00, 'PAGADA', 'Ciclo ASIR'),
  
  -- Facturas 2024 primer semestre (todos los empleados)
  ('F-2024-004', 17, 3, '2024-01-28', 2900.00, 'PAGADA', 'Ciclo Comercio'),
  ('F-2024-005', 18, 4, '2024-01-25', 450.00, 'PAGADA', 'Curso Java'),
  ('F-2024-006', 19, 5, '2024-02-20', 600.00, 'PAGADA', 'Curso Big Data'),
  ('F-2024-007', 20, 1, '2024-03-25', 3200.00, 'PAGADA', 'Ciclo DAM'),
  ('F-2024-008', 11, 2, '2024-04-20', 3100.00, 'PAGADA', 'Ciclo DAW'),
  ('F-2024-009', 12, 3, '2024-04-30', 500.00, 'PAGADA', 'Curso React'),
  
  -- Facturas 2024 segundo semestre
  ('F-2024-010', 13, 4, '2024-05-30', 3000.00, 'PAGADA', 'Ciclo ASIR'),
  ('F-2024-011', 14, 5, '2024-06-25', 2900.00, 'PAGADA', 'Ciclo Comercio'),
  ('F-2024-012', 15, 1, '2024-07-15', 450.00, 'PAGADA', 'Curso Java'),
  ('F-2024-013', 16, 2, '2024-09-05', 600.00, 'PAGADA', 'Curso Big Data'),
  ('F-2024-014', 17, 3, '2024-09-30', 3200.00, 'PAGADA', 'Ciclo DAM'),
  ('F-2024-015', 18, 4, '2024-10-25', 3100.00, 'PAGADA', 'Ciclo DAW'),
  ('F-2024-016', 19, 5, '2024-11-15', 500.00, 'PAGADA', 'Curso React'),
  ('F-2024-017', 20, 1, '2024-12-05', 3000.00, 'PAGADA', 'Ciclo ASIR'),
  
  -- Facturas 2025 primer semestre
  ('F-2025-001', 11, 2, '2025-01-30', 2900.00, 'PAGADA', 'Ciclo Comercio'),
  ('F-2025-002', 12, 3, '2025-02-25', 450.00, 'PAGADA', 'Curso Java'),
  ('F-2025-003', 13, 4, '2025-04-05', 600.00, 'PAGADA', 'Curso Big Data'),
  ('F-2025-004', 14, 5, '2025-04-28', 3200.00, 'PAGADA', 'Ciclo DAM'),
  ('F-2025-005', 15, 1, '2025-06-05', 3100.00, 'PAGADA', 'Ciclo DAW'),
  ('F-2025-006', 16, 2, '2025-06-30', 500.00, 'PAGADA', 'Curso React'),
  
  -- Facturas 2025 segundo semestre
  ('F-2025-007', 17, 3, '2025-07-25', 3000.00, 'PAGADA', 'Ciclo ASIR'),
  ('F-2025-008', 18, 4, '2025-09-05', 2900.00, 'PAGADA', 'Ciclo Comercio'),
  ('F-2025-009', 19, 5, '2025-09-28', 450.00, 'PAGADA', 'Curso Java'),
  ('F-2025-010', 20, 1, '2025-11-05', 600.00, 'PAGADA', 'Curso Big Data'),
  ('F-2025-011', 11, 2, '2025-11-25', 3200.00, 'PAGADA', 'Ciclo DAM'),
  ('F-2025-012', 12, 3, '2025-12-05', 3100.00, 'EMITIDA', 'Ciclo DAW - pendiente pago'),
  
  -- Facturas con múltiples productos (distribuidas entre empleados)
  ('F-2023-016', 11, 1, '2023-08-10', 3650.00, 'PAGADA', 'DAM + Python'),
  ('F-2023-017', 12, 2, '2023-10-15', 3700.00, 'PAGADA', 'DAW + Ciberseguridad'),
  ('F-2024-018', 13, 3, '2024-07-20', 3500.00, 'PAGADA', 'ASIR + Java'),
  ('F-2024-019', 14, 4, '2024-08-25', 3800.00, 'PAGADA', 'DAM + React + Big Data'),
  ('F-2025-013', 15, 5, '2025-10-15', 3400.00, 'PAGADA', 'DAW + Java'),
  ('F-2025-014', 16, 1, '2025-11-20', 3600.00, 'EMITIDA', 'ASIR + React'),
  
  -- Facturas pendientes y vencidas (distribuidas entre empleados hasta actualidad)
  ('F-2025-015', 17, 2, '2025-10-10', 2900.00, 'PENDIENTE', 'Factura pendiente de emisión'),
  ('F-2025-016', 18, 3, '2025-09-15', 450.00, 'VENCIDA', 'Factura vencida sin pago'),
  ('F-2025-017', 19, 4, '2025-11-05', 600.00, 'VENCIDA', 'Factura vencida'),
  ('F-2025-018', 20, 5, '2025-12-01', 3200.00, 'PENDIENTE', 'Factura reciente pendiente'),
  ('F-2025-019', 11, 1, '2025-12-05', 3100.00, 'PENDIENTE', 'Factura del día actual');

-- =========================================================
-- FACTURA_PRODUCTOS ADICIONALES
-- =========================================================
INSERT INTO factura_productos (id_factura, id_producto, id_cliente_beneficiario, cantidad, precio_unitario, subtotal)
VALUES
  -- Facturas simples 2022
  (8, 8, 11, 1, 3000.00, 3000.00),
  (9, 2, 12, 1, 3100.00, 3100.00),
  (10, 1, 13, 1, 3200.00, 3200.00),
  
  -- Facturas 2023
  (11, 3, 13, 1, 350.00, 350.00),
  (12, 4, 14, 1, 400.00, 400.00),
  (13, 9, 15, 1, 500.00, 500.00),
  (14, 8, 16, 1, 3000.00, 3000.00),
  (15, 11, 17, 1, 2900.00, 2900.00),
  (16, 9, 11, 1, 450.00, 450.00),
  (17, 12, 12, 1, 600.00, 600.00),
  (18, 1, 13, 1, 3200.00, 3200.00),
  (19, 2, 14, 1, 3100.00, 3100.00),
  (20, 9, 15, 1, 500.00, 500.00),
  (21, 8, 11, 1, 3000.00, 3000.00),
  
  -- Facturas 2024
  (22, 11, 12, 1, 2900.00, 2900.00),
  (23, 9, 13, 1, 450.00, 450.00),
  (24, 12, 14, 1, 600.00, 600.00),
  (25, 1, 15, 1, 3200.00, 3200.00),
  (26, 2, 16, 1, 3100.00, 3100.00),
  (27, 9, 17, 1, 500.00, 500.00),
  (28, 8, 18, 1, 3000.00, 3000.00),
  (29, 11, 19, 1, 2900.00, 2900.00),
  (30, 9, 20, 1, 450.00, 450.00),
  (31, 12, 11, 1, 600.00, 600.00),
  (32, 1, 12, 1, 3200.00, 3200.00),
  (33, 2, 13, 1, 3100.00, 3100.00),
  (34, 9, 14, 1, 500.00, 500.00),
  (35, 8, 15, 1, 3000.00, 3000.00),
  
  -- Facturas 2025
  (36, 11, 16, 1, 2900.00, 2900.00),
  (37, 9, 17, 1, 450.00, 450.00),
  (38, 12, 18, 1, 600.00, 600.00),
  (39, 1, 19, 1, 3200.00, 3200.00),
  (40, 2, 20, 1, 3100.00, 3100.00),
  (41, 9, 11, 1, 500.00, 500.00),
  (42, 8, 12, 1, 3000.00, 3000.00),
  (43, 11, 13, 1, 2900.00, 2900.00),
  (44, 9, 14, 1, 450.00, 450.00),
  (45, 12, 15, 1, 600.00, 600.00),
  (46, 1, 16, 1, 3200.00, 3200.00),
  (47, 2, 17, 1, 3100.00, 3100.00),
  
  -- Facturas con múltiples productos
  (48, 1, 11, 1, 3200.00, 3200.00),
  (48, 3, 11, 1, 350.00, 350.00),
  (49, 2, 12, 1, 3100.00, 3100.00),
  (49, 4, 12, 1, 400.00, 400.00),
  (50, 8, 13, 1, 3000.00, 3000.00),
  (50, 9, 13, 1, 450.00, 450.00),
  (51, 1, 14, 1, 3200.00, 3200.00),
  (51, 9, 14, 1, 500.00, 500.00),
  (51, 12, 14, 1, 600.00, 600.00),
  (52, 2, 15, 1, 3100.00, 3100.00),
  (52, 9, 15, 1, 450.00, 450.00),
  (53, 8, 16, 1, 3000.00, 3000.00),
  (53, 9, 16, 1, 500.00, 500.00),
  
  -- Facturas pendientes y vencidas (hasta actualidad)
  (54, 11, 17, 1, 2900.00, 2900.00),
  (55, 9, 18, 1, 450.00, 450.00),
  (56, 12, 19, 1, 600.00, 600.00),
  (57, 1, 20, 1, 3200.00, 3200.00),
  (58, 2, 11, 1, 3100.00, 3100.00);

-- =========================================================
-- PAGOS ADICIONALES (distribuidos en el tiempo, algunos parciales)
-- =========================================================
INSERT INTO pagos (id_factura, fecha_pago, importe, metodo_pago, estado)
VALUES
  -- Pagos 2022
  (8, '2022-07-01', 3000.00, 'TRANSFERENCIA', 'PAGADA'),
  (9, '2022-10-05', 3100.00, 'TARJETA', 'PAGADA'),
  (10, '2023-01-10', 3200.00, 'TRANSFERENCIA', 'PAGADA'),
  
  -- Pagos 2023 primer semestre
  (11, '2023-01-30', 350.00, 'EFECTIVO', 'PAGADA'),
  (12, '2023-02-25', 400.00, 'TARJETA', 'PAGADA'),
  (13, '2023-04-05', 500.00, 'TRANSFERENCIA', 'PAGADA'),
  (14, '2023-05-10', 3000.00, 'TRANSFERENCIA', 'PAGADA'),
  (15, '2023-06-05', 2900.00, 'TARJETA', 'PAGADA'),
  
  -- Pagos 2023 segundo semestre
  (16, '2023-07-20', 450.00, 'EFECTIVO', 'PAGADA'),
  (17, '2023-09-10', 600.00, 'TRANSFERENCIA', 'PAGADA'),
  (18, '2023-10-01', 3200.00, 'TARJETA', 'PAGADA'),
  (19, '2023-11-05', 3100.00, 'TRANSFERENCIA', 'PAGADA'),
  (20, '2023-11-30', 500.00, 'EFECTIVO', 'PAGADA'),
  (21, '2023-12-15', 3000.00, 'TARJETA', 'PAGADA'),
  
  -- Pagos 2024 primer semestre
  (22, '2024-02-05', 2900.00, 'TRANSFERENCIA', 'PAGADA'),
  (23, '2024-02-01', 450.00, 'EFECTIVO', 'PAGADA'),
  (24, '2024-02-25', 600.00, 'TARJETA', 'PAGADA'),
  (25, '2024-04-01', 3200.00, 'TRANSFERENCIA', 'PAGADA'),
  (26, '2024-05-05', 3100.00, 'TARJETA', 'PAGADA'),
  (27, '2024-05-05', 500.00, 'EFECTIVO', 'PAGADA'),
  
  -- Pagos 2024 segundo semestre
  (28, '2024-06-05', 3000.00, 'TRANSFERENCIA', 'PAGADA'),
  (29, '2024-07-01', 2900.00, 'TARJETA', 'PAGADA'),
  (30, '2024-07-20', 450.00, 'EFECTIVO', 'PAGADA'),
  (31, '2024-09-10', 600.00, 'TRANSFERENCIA', 'PAGADA'),
  (32, '2024-10-05', 3200.00, 'TARJETA', 'PAGADA'),
  (33, '2024-11-01', 3100.00, 'TRANSFERENCIA', 'PAGADA'),
  (34, '2024-11-20', 500.00, 'EFECTIVO', 'PAGADA'),
  (35, '2024-12-10', 3000.00, 'TARJETA', 'PAGADA'),
  
  -- Pagos 2025 primer semestre
  (36, '2025-02-05', 2900.00, 'TRANSFERENCIA', 'PAGADA'),
  (37, '2025-03-01', 450.00, 'EFECTIVO', 'PAGADA'),
  (38, '2025-04-10', 600.00, 'TARJETA', 'PAGADA'),
  (39, '2025-05-05', 3200.00, 'TRANSFERENCIA', 'PAGADA'),
  (40, '2025-07-01', 3100.00, 'TARJETA', 'PAGADA'),
  (41, '2025-07-05', 500.00, 'EFECTIVO', 'PAGADA'),
  
  -- Pagos 2025 segundo semestre (hasta actualidad 05/12/2025)
  (42, '2025-08-05', 3000.00, 'TRANSFERENCIA', 'PAGADA'),
  (43, '2025-09-10', 2900.00, 'TARJETA', 'PAGADA'),
  (44, '2025-10-05', 450.00, 'EFECTIVO', 'PAGADA'),
  (45, '2025-11-10', 600.00, 'TRANSFERENCIA', 'PAGADA'),
  (46, '2025-11-30', 3200.00, 'TARJETA', 'PAGADA'),
  (47, '2025-12-05', 3100.00, 'TRANSFERENCIA', 'PAGADA'),
  
  -- Pagos parciales (facturas con múltiples pagos)
  (48, '2023-08-15', 2000.00, 'TRANSFERENCIA', 'PAGADA'),
  (48, '2023-08-25', 1650.00, 'TARJETA', 'PAGADA'),
  (49, '2023-10-20', 2000.00, 'EFECTIVO', 'PAGADA'),
  (49, '2023-10-30', 1700.00, 'TRANSFERENCIA', 'PAGADA'),
  (50, '2024-07-25', 2000.00, 'TARJETA', 'PAGADA'),
  (50, '2024-08-05', 1500.00, 'TRANSFERENCIA', 'PAGADA'),
  (51, '2024-09-05', 2000.00, 'EFECTIVO', 'PAGADA'),
  (51, '2024-09-15', 1800.00, 'TARJETA', 'PAGADA'),
  (52, '2025-10-20', 2000.00, 'TRANSFERENCIA', 'PAGADA'),
  (52, '2025-11-05', 1550.00, 'TARJETA', 'PAGADA'),
  (53, '2025-11-25', 2000.00, 'EFECTIVO', 'PAGADA'),
  (53, '2025-12-01', 1500.00, 'TRANSFERENCIA', 'PAGADA'),
  
  -- Pagos pendientes y cancelados (hasta actualidad)
  (54, '2025-10-15', 1500.00, 'TARJETA', 'PENDIENTE'),
  (55, '2025-09-20', 200.00, 'EFECTIVO', 'CANCELADA'),
  (56, '2025-11-10', 300.00, 'TRANSFERENCIA', 'CANCELADA'),
  (57, '2025-12-10', 3200.00, 'TRANSFERENCIA', 'PENDIENTE'), 
  (58, '2025-12-15', 3100.00, 'TARJETA', 'PENDIENTE'); 

-- =========================================================
-- FIN DE AMPLIACIÓN DE DATOS DE PRUEBA
-- =========================================================
