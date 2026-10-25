# MPAZ NEB

Aplicación móvil Android de nivelación en **Lectura y Matemática** para estudiantes de 1.º a 4.º básico de la Escuela Escritora Marcela Paz (San Bernardo).

Proyecto académico (MVP) de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**, Duoc UC. Corresponde a la **Evaluación Parcial 2 (encargo)**.

> **Alcance académico:** la aplicación trabaja únicamente con usuarios, estudiantes, actividades, respuestas y resultados **ficticios**. No diagnostica dificultades de aprendizaje, no asigna notas oficiales y no reemplaza el criterio profesional de docentes, UTP o PIE.

## Integrante

| Nombre                         | Rol |
|--------------------------------|---|
| `Sebastian Andres Vallejos Rios` | Desarrollo individual del proyecto |

- Docente: `Jose Miguel Candia Hernandez`
- Sección: 003D

## Enlaces

- Repositorio: [[ENLACE AL REPOSITORIO - CLICK]](https://github.com/SebastianVallejos18/MPAZ-NEB)
- Planificación en Trello: [[ENLACE AL TABLERO - CLICK]](https://trello.com/invite/b/6ade80a5a3fb6b3ae8006db6/ATTIa81e5618767762904e0fe8d395fe33e24B2800A5/desarrollo-de-aplicaciones-moviles)

## Problema que resuelve

Los materiales de refuerzo de lectura y matemática de la escuela están repartidos en documentos impresos, lo que hace lento buscarlos y adaptarlos al nivel de cada estudiante y no hay un registro central de qué actividad realizó cada alumno, cuántos intentos necesitó o qué respondió.

MPAZ NEB centraliza las actividades en dos módulos (Lectura y Matemática), permite organizarlas por curso y dificultad, asignarlas de forma supervisada mediante un código temporal, y registrar respuestas, intentos y resultados básicos para orientar el acompañamiento.

## Funcionalidades implementadas

### Administración pedagógica (docente o UTP)
- Ingreso con alias y contraseña.
- Catálogo de actividades con filtros por módulo, curso, dificultad y estado.
- Crear y editar actividades (nombre, módulo, curso, contenido, objetivo pedagógico, dificultad, instrucciones, texto de lectura, intentos permitidos y estado).
- Crear, editar y eliminar (con confirmación) las preguntas de cada actividad, con tres alternativas, respuesta correcta y retroalimentación.
- Activar la lectura en voz alta para actividades de 3.º básico.

### Asistente de aula
- Ingreso con alias y contraseña.
- Consulta de actividades **disponibles**, filtradas por módulo, curso y dificultad.
- Asignación de una actividad a un estudiante ficticio, con generación de un **código temporal** de 6 caracteres.
- Resultados de sus asignaciones: estado, intentos, aciertos y respuestas de cada intento.
- Registro de una observación pedagógica general (máximo 300 caracteres).

### Estudiante
- Ingreso solo con el código temporal entregado por la asistente.
- Instrucciones, texto de lectura y preguntas con indicador de avance.
- Confirmación antes de enviar cada respuesta.
- Retroalimentación inmediata, breve y motivadora, sin calificaciones.
- Reintentos cuando la actividad lo permite; si cierra la app a mitad de camino, retoma donde quedó.
- Lectura en voz alta del texto y de las preguntas (según el curso).

## Usuarios de prueba (ficticios)

| Alias | Contraseña | Perfil | Cómo ingresa |
|---|---|---|---|
| `admin` | `admin123` | Administración pedagógica | Pantalla de inicio de sesión |
| `asistente1` | `asist123` | Asistente de aula | Pantalla de inicio de sesión |
| `estudiante1` | — | Estudiante | "Soy estudiante" + código que genera la asistente |
| `estudiante2` | — | Estudiante | "Soy estudiante" + código que genera la asistente |

La primera vez que se abre la app se cargan 4 actividades de ejemplo (2 de Lectura y 2 de Matemática, con 12 preguntas en total).

**Flujo sugerido para probar todo:** ingresar como `asistente1`, asignar una actividad a `estudiante1`, anotar el código, cerrar sesión, entrar como estudiante con ese código, responder, y volver a entrar como `asistente1` para ver los resultados.

## Cómo ejecutar el proyecto

**Requisitos**
- Android Studio compatible con Android Gradle Plugin 9.4.1
- Un teléfono o tableta Android 8.0 (API 26) o superior, o un emulador.
- Conexión a internet solo para la primera sincronización de Gradle; la aplicación funciona sin conexión.

**Pasos**
1. Clonar el repositorio: `git clone [ENLACE AL REPOSITORIO]`
2. Abrir la carpeta en Android Studio y esperar a que termine **Sync Gradle**.
3. Conectar un dispositivo con **Depuración USB** activada (Ajustes → Opciones de desarrollador), o crear un emulador.
4. Presionar **Run** (▶) con el módulo `app` seleccionado.
5. Ingresar con alguno de los usuarios de prueba.

**Notas**
- Para la lectura en voz alta, el dispositivo necesita un motor de texto a voz con español instalado (Ajustes → Administración general → Texto a voz).
- La vibración solo se percibe en un dispositivo físico.
- Si se modifica alguna entidad de Room durante el desarrollo, hay que desinstalar la app del dispositivo antes de volver a ejecutarla.

## Arquitectura

El proyecto sigue **MVVM** con una capa **Repository**:

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Model | `model`, `data/local` | Entidades, sealed classes, DAOs, `AppDatabase` y convertidores de Room |
| Repository | `data/repository` | Único punto de acceso a los datos para los ViewModels |
| ViewModel | `viewmodel` | Estado de cada pantalla (`StateFlow`), validaciones y coordinación con los repositorios |
| View | `ui` | Pantallas y componentes Composable; solo muestran el estado y avisan los eventos del usuario |

Las reglas de validación son **funciones puras** (`util/Validaciones.kt`), separadas de la interfaz, por lo que cada pantalla solo muestra el mensaje que le entrega el ViewModel.

### Estructura de carpetas

```
cl.duoc.mpazneb
├── data
│   ├── local          // DAOs, AppDatabase, convertidores
│   ├── repository     // Repositorios
│   └── DatosIniciales.kt
├── model              // Entidades y sealed classes
├── ui
│   ├── components     // Componentes reutilizables
│   ├── navigation     // Rutas y NavHost
│   ├── screens        // Pantallas
│   └── theme          // Colores y tipografía
├── util               // Validaciones, etiquetas, LectorVoz, Vibrador
├── viewmodel          // ViewModels y estados de pantalla
├── MainActivity.kt
└── MpazApplication.kt
```

## Persistencia local (Room)

Base de datos `mpaz_neb.db` con cinco tablas:

| Tabla | Contenido |
|---|---|
| `usuarios` | Alias, hash de la clave, perfil, estado de la cuenta y último acceso |
| `actividades` | Datos pedagógicos de cada actividad, estado y opción de lectura en voz |
| `preguntas` | Enunciado, alternativas, respuesta correcta, procedimiento y retroalimentación |
| `asignaciones` | Actividad, estudiante, asistente, código temporal, estado, intentos, aciertos y observación |
| `respuestas` | Respuesta elegida por pregunta y por intento |

Los perfiles, módulos, dificultades y estados se modelan con **sealed classes** y se guardan como texto mediante `TypeConverter`. Las relaciones se definen con claves foráneas. Las contraseñas se guardan con hash SHA-256; en un sistema productivo se usaría además una sal por usuario y un algoritmo específico para contraseñas.

## Formularios y validaciones

Cada campo muestra su error con **color, ícono de advertencia y mensaje**, y el botón de guardar queda deshabilitado mientras se procesa. Si un campo ya tenía error, se revalida mientras el usuario lo corrige.

- **Login:** alias (3 a 20 caracteres, letras, números o guion bajo) y contraseña (mínimo 6).
- **Actividad:** nombre, contenido, objetivo, instrucciones, texto de lectura (obligatorio solo en Lectura) e intentos permitidos (1 a 5).
- **Pregunta:** enunciado, tres opciones **distintas entre sí**, retroalimentación y procedimiento (obligatorio solo en Matemática).
- **Código del estudiante:** 6 caracteres, siempre en mayúscula.

## Reglas de negocio

- Una actividad sin preguntas no puede quedar en estado **Disponible**.
- No se puede eliminar la única pregunta de una actividad disponible.
- La asistente solo ve actividades **disponibles**.
- El código temporal es único y no usa caracteres que se confunden fácilmente (sin `0`/`O` ni `1`/`I`/`L`).
- El código deja de funcionar cuando la asignación se finaliza.
- Las respuestas y el avance se guardan en una **transacción**, de modo que no queden datos a medias.
- El administrador y la asistente no pueden modificar resultados; la asistente solo registra observaciones generales.

## Animaciones

| Animación | Dónde | Aporte |
|---|---|---|
| Transición entre pantallas (deslizamiento y fundido, 300 ms) | Toda la navegación | Indica si el usuario avanzó o retrocedió |
| `AnimatedVisibility` | Errores del login, campos condicionales de formularios, retroalimentación | Los elementos aparecen y desaparecen con suavidad |
| `animateFloatAsState` | Barra de progreso del estudiante | Muestra el avance con fluidez |
| `animateColorAsState` | Opción de respuesta elegida | Confirma la selección |
| Indicador de carga en botones | Login, formularios y envío de respuestas | Informa que se está procesando |

## Recursos nativos del dispositivo

### Texto a voz (parlante)
- Implementado con `TextToSpeech` de Android, encapsulado en `util/LectorVoz.kt`.
- **Regla por curso:** siempre activo en 1.º y 2.º básico; en 3.º básico solo si el administrador lo activa en la actividad; no disponible en 4.º básico.
- Lee las instrucciones, el texto, cada pregunta con sus alternativas y la retroalimentación.
- Velocidad reducida (0,85) pensada para niños que están aprendiendo a leer.
- **Permisos:** no requiere permisos de ejecución. En el manifiesto se declara `<queries>` para el servicio `TTS_SERVICE`, necesario desde Android 11 por la visibilidad de paquetes.
- Si el dispositivo no tiene voz en español, los botones no se muestran y la app sigue funcionando. El motor se libera al cerrar la sesión.

### Vibración
- Implementada con `Vibrator` / `VibrationEffect`, encapsulada en `util/Vibrador.kt`.
- Un toque firme al acertar y dos toques suaves al responder algo por reforzar; el refuerzo es deliberadamente más suave para mantener una retroalimentación respetuosa.
- **Permiso:** `android.permission.VIBRATE` (permiso normal, se concede al instalar).
- Duración, pausa e intensidad se ajustan en el bloque de constantes al inicio de `Vibrador.kt`, con un tope de seguridad de 500 ms. Se verifica que el dispositivo tenga vibrador y cualquier error se captura sin afectar la app.

## Accesibilidad

Tipografía grande, buen contraste con Material Design 3, botones de al menos 48 a 64 dp, instrucciones breves, indicador de avance, confirmación antes de enviar una respuesta y mensajes motivadores. No se usa cámara, micrófono, GPS, datos biométricos ni contactos.

## Tecnologías

- Kotlin 2.2.10 · Jetpack Compose · Material Design 3
- Navigation Compose 2.9.8
- Room 2.8.5 con KSP 2.3.11
- ViewModel, StateFlow y corrutinas
- Android Gradle Plugin 9.4.1 · compileSdk 37 · minSdk 26

## Trabajo colaborativo

- Control de versiones en GitHub, con commits técnicos por funcionalidad.
- Planificación y seguimiento de tareas en Trello (enlace más arriba).

## Limitaciones del MVP

- Una secuencia reducida de actividades por módulo y datos exclusivamente ficticios.
- Sin integración con sistemas institucionales ni uso de inteligencia artificial para generar contenido.
- No evalúa lectura en voz alta ni reemplaza la evaluación oficial.
- La selección de curso, nivel y dificultad la hace siempre un usuario autorizado.