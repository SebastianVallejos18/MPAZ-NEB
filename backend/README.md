# Backend MPAZ NEB — Microservicios Spring Boot

Tres microservicios independientes. Cada uno tiene su propia base de datos MySQL.

| Microservicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `ms-usuarios` | 8081 | `mpaz_usuarios` | Usuarios ficticios y login |
| `ms-actividades` | 8082 | `mpaz_actividades` | Actividades y sus preguntas |
| `ms-asignaciones` | 8083 | `mpaz_asignaciones` | Asignaciones (código temporal) y respuestas |

## Requisitos
- JDK 17 o superior
- MySQL corriendo en `localhost:3306` (XAMPP, Laragon o MySQL Server)
- IntelliJ IDEA (o VS Code con extensiones de Java/Spring)

## Cómo ejecutar
1. Encender MySQL (en XAMPP: botón *Start* de MySQL). Las bases se crean solas (`createDatabaseIfNotExist=true`).
2. Si tu MySQL usa usuario/clave distintos a `root` sin clave, cámbialos en `src/main/resources/application.properties` de cada microservicio.
3. En IntelliJ: **File → Open → `backend/pom.xml` → Open as Project**. Aparecen los 3 módulos en un solo proyecto.
4. Ejecutar `UsuariosApplication`, `ActividadesApplication` y `AsignacionesApplication` (flecha verde junto a cada `main`).
   Tip: *Run → Edit Configurations → + → Compound*, agregar las tres y así se encienden con un solo clic.
5. Al primer arranque se cargan los datos de prueba (usuarios y 4 actividades con 12 preguntas).

> El `pom.xml` de `backend/` solo agrupa los módulos. Cada microservicio sigue siendo una aplicación
> independiente: se ejecuta por separado, en su propio puerto y con su propia base de datos.

## Prueba rápida (navegador o Postman)
- http://localhost:8081/api/usuarios
- http://localhost:8082/api/actividades
- http://localhost:8083/api/asignaciones

## Estructura de cada microservicio
```
model/        Entidades JPA (tablas) y enums
repository/   Interfaces JpaRepository (Spring Data genera el SQL)
service/      Lógica y reglas de negocio
controller/   Endpoints REST (@RestController)
dto/          Cuerpos de las peticiones (records con validaciones)
exception/    Errores propios + @RestControllerAdvice que responde JSON {"mensaje": ...}
config/       DatosIniciales: carga datos ficticios si las tablas están vacías
```
