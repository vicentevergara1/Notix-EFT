# Configurar Xano para Notix-EFT

## Objetivo

Notix-EFT ya no utiliza un modo de datos ficticios. La aplicación necesita autenticación real y autorización en Xano antes de poder usarse. No hay cuentas ni contraseñas de demostración codificadas dentro del APK.

**Base URL del grupo:** `https://x8ki-letl-twmt.n7.xano.io/api:8Q9ARAS-/`.

## Prompt para Xano Agent

Copia estas instrucciones en Xano Agent. No le pidas que elimine y recree tablas existentes.

> Proyecto académico Notix. Quiero habilitar autenticación segura y autorización por rol en las tablas actuales usuarios, tramites y feriados, sin borrar datos ni crear tablas duplicadas.
>
> 1. Revisa y repara TODAS las referencias "Deleted Table" / "Invalid name" de los endpoints existentes. Usa las tablas actualmente existentes.
> 2. En la tabla usuarios, verifica campos id, nombre, correo (único), tipo_usuario y un campo de contraseña compatible con autenticación nativa de Xano. Habilita la autenticación nativa de Xano en la tabla si es compatible con los registros actuales, sin destruir datos ni contraseñas. Si requiere migración, detente e indica qué falta; nunca guardes contraseñas en texto plano.
> 3. Crear POST /auth/signup: recibe nombre, correo y password; valida datos y crea solo cuentas con tipo_usuario="Persona" desde registro público. Retorna exactamente {"authToken":"token"}. Nunca permite crear un Notario por signup público.
> 4. Crear POST /auth/login: recibe correo y password; usa login seguro de Xano y devuelve exactamente {"authToken":"token"} si las credenciales son válidas.
> 5. Crear GET /auth/me: exige Bearer token y devuelve el usuario autenticado con id, nombre, correo, tipo_usuario; no devuelve contraseña.
> 6. El usuario con tipo_usuario="Notario" será creado o designado SOLO desde el panel administrativo de Xano, nunca por una petición pública.
> 7. Exigir autenticación en GET /tramites, POST /tramites, PUT /tramites/{tramites_id}, DELETE /tramites/{tramites_id}. Obtener id y tipo_usuario de la sesión autenticada, NO de inputs del usuario. Persona puede consultar solo sus trámites, crear para sí misma y eliminar solo sus trámites no aprobados; Notario puede consultar todos y aprobar/devolver solo trámites pendientes. Devolución requiere observacion no vacía. El backend debe rechazar cualquier acceso no autorizado con HTTP 401 o 403.
> 8. GET /tramites retorna directamente un array JSON con campos id, usuario_id, tipo_tramite, descripcion, estado, observacion. POST /tramites recibe {"tipo_tramite":"Poder simple","descripcion":"..."} y establece usuario_id=auth.id y estado="Pendiente". PUT /tramites/{tramites_id} recibe {"estado":"Aprobado","observacion":null} o {"estado":"Devuelto","observacion":"Motivo"} y aplica autorización de Notario. DELETE elimina solo si usuario_id coincide con auth.id y estado no es Aprobado.
> 9. GET /feriados devuelve un array JSON con id, nombre, fecha, pais. Se puede dejar público porque son datos no sensibles; no permitir modificar feriados desde el APK.
> 10. No expondrás listados de todos los usuarios públicamente. Desactiva o protege POST /seed_notix cuando terminen las pruebas. Si conservas endpoints CRUD públicos antiguos, protégelos o desactívalos para evitar saltarse los controles.
> 11. Ejecuta pruebas de signup, login, auth/me, GET filtrado, POST, PUT y DELETE con un usuario Persona y otro Notario, incluyendo intentos prohibidos. No declares resultados aprobados si no los verificaste. Devuelve un listado de métodos, rutas, respuestas HTTP y errores que queden pendientes.

## Contrato Kotlin y API REST

| Método | Endpoint | Autenticación | Acceso |
| --- | --- | --- | --- |
| POST | /auth/signup | No | crea Persona |
| POST | /auth/login | No | inicia sesión |
| GET | /auth/me | Sí | perfil propio |
| GET | /tramites | Sí | Persona ve propios, Notario todos |
| POST | /tramites | Sí | Persona crea propio |
| PUT | /tramites/{tramites_id} | Sí | solo Notario |
| DELETE | /tramites/{tramites_id} | Sí | solo propietario en estados permitidos |
| GET | /feriados | No | lectura |

Para todos los endpoints autenticados, Retrofit envía `Authorization: Bearer <authToken>`.

## Verificación que debes hacer

1. En Xano, ejecutar POST /auth/signup con una cuenta ficticia (sin publicar la contraseña en GitHub).
2. Ejecutar POST /auth/login y comprobar que devuelve `authToken`.
3. Ejecutar GET /auth/me con ese token y comprobar que retorna el rol Persona.
4. Crear/designar una cuenta Notario mediante Xano administrativo.
5. Probar peticiones autorizadas y denegadas.
6. Ejecutar Notix en Android Studio y demostrar que los datos realmente aparecen en Database.

**Nunca afirmar que la integración es funcional hasta completar las pruebas de Xano y Android.**
