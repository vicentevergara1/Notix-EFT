# Notix-EFT — corrección de enfoque para defensa: Xano

## Comentario del profesor y estado

El profesor indicó que mostrar la app conectada a `10.0.2.2:8081` no satisface la demostración esperada de API REST con Xano. `10.0.2.2` es una dirección local del emulador, no una URL pública.

**Estado actual:** Android sigue utilizando Spring Boot local para autenticación/trámites; Xano se usa solo para feriados. Esta versión **NO está migrada** a la arquitectura Xano principal.

## Objetivo deseado

- Xano debe ser claramente visible como API REST pública y origen de los datos que se muestran durante la defensa.
- Antes de cambiar Retrofit, reparar en Xano `Deleted Table` / `Invalid name`, configurar autenticación segura, autorización por rol, y probar todos los endpoints.
- No publicar contraseñas, tokens ni datos personales.
- Mantener evidencia y código de microservicios Spring Boot: la rúbrica EP4, páginas 2–5, exige microservicios propios con base activa e integración CRUD. **Xano no sustituye automáticamente ese requisito**.
- Definir cómo participan Spring Boot y Xano en un flujo integrado, evitando duplicación de datos y evitando afirmar que una API no probada funciona.
- No sobreescribir un proyecto que ya compila hasta demostrar una ruta equivalente con Xano.

## API base compartida por usuario

`https://x8ki-letl-twmt.n7.xano.io/api:8Q9ARAS-/`

**Rutas objetivo (sujetas al contrato real de Xano):**

| HTTP | Ruta | Función |
|---|---|---|
| POST | auth/signup | Alta Persona |
| POST | auth/login | Obtener token |
| GET | auth/me | Perfil autenticado |
| GET | tramites | Consulta según rol |
| POST | tramites | Crear |
| PUT/PATCH | tramites/{id} | Cambiar estado |
| DELETE | tramites/{id} | Eliminar autorizado |
| GET | feriados | Consulta externa |

No asumir que estas rutas existen o responden en el Xano actual. Deben configurarse, probarse y comparar JSON real antes de adaptar las interfaces Kotlin.

## Qué enseñar al profesor

En Android Studio, abrir `ApiConfig.kt`, `RetrofitCliente.kt`, `AuthApi.kt`, `TramitesApi.kt`, `NotixRepository.kt` y `NotixViewModel.kt`. En Xano mostrar API Group, Function Stack, Database y Run. Explicar el flujo desde el botón hasta la tabla.

## Riesgo de rúbrica

La pauta EP4 exige específicamente microservicios propios Spring Boot e integración desde la app; si Xano sustituye completamente Spring Boot, los indicadores IE 3.1.2 e IE 3.1.3 pueden quedar sin cumplir. Antes de elegir arquitectura final para EFT, confirmar su rúbrica. Es posible usar Xano como fachada API pública y Spring Boot desplegado como procesamiento, pero necesita diseño y pruebas adicionales.
