# 🏦 Credit Request Microservice (MVP)

Backend desarrollado como Prueba de Concepto (PoC) para un sistema de evaluación de créditos bancarios. Simula la lógica de negocio, seguridad y persistencia requerida en un entorno financiero real (Banca/Seguros).

## 🚀 Tecnologías y Arquitectura

Este proyecto sigue una arquitectura en capas (Layered Architecture) y utiliza las últimas prácticas de la industria:

* **Java 17:** Uso de nuevas características del lenguaje.
* **Spring Boot 3:** Framework principal.
* **Spring Security + JWT:** Implementación de seguridad stateless con filtros personalizados para autenticación y autorización basada en Roles (RBAC).
* **Spring Data JPA:** Capa de persistencia optimizada.
* **PostgreSQL:** Base de datos relacional.
* **Bean Validation (Jakarta):** Validaciones de entrada (Fail-Fast).
* **Maven Wrapper:** Para garantizar la portabilidad del build.

## ⚙️ Funcionalidades Clave

1.  **Seguridad Robusta:**
    * Sistema de Login con emisión de **Tokens JWT**.
    * Protección de rutas por roles: `ROLE_CLIENT` vs `ROLE_ANALYST`.
    * Filtros personalizados: `JwtAuthenticationFilter` y `JwtValidationFilter`.

2.  **Lógica de Negocio Financiera:**
    * Uso estricto de **`BigDecimal`** para evitar errores de precisión en montos monetarios (no `Double`).
    * Reglas de negocio: Rechazo automático de solicitudes superiores a **S/ 50,000** (Políticas de riesgo).
    * Validaciones de integridad: No se permiten montos negativos ni plazos inválidos.

3.  **Manejo de Errores:**
    * Implementación de `@RestControllerAdvice` para centralizar excepciones.
    * Respuestas HTTP semánticas (404, 400, 401) con mensajes claros en formato JSON (evitando stacktraces expuestos).

## 🛠️ Cómo ejecutar el proyecto

### Prerrequisitos
* Java 17 o superior.
* PostgreSQL instalado y corriendo.

### Configuración
1.  Crear una base de datos en PostgreSQL llamada `banco_db`.
2.  Configurar las credenciales en `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/banco_db
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_password
    ```

### Ejecución
Usa el Maven Wrapper incluido para no necesitar instalar dependencias manualmente:

```bash
./mvnw spring-boot:run
