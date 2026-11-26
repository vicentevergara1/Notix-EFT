# GUÍA DE DEFENSA — NOTIX EFT

Esta guía sigue el recorrido real de los archivos del repositorio. No confundas código escrito con código **probado**: revisa la pestaña Actions para saber qué compiló.

## 1. Abre los archivos en este orden delante del profesor

| Orden | Archivo | Qué explicas en una frase |
| --- | --- | --- |
| 1 | `android/app/src/main/java/com/duoc/notixnuevo/MainActivity.kt` | Inicia la app y muestra la interfaz Compose |
| 2 | `.../ui/LoginScreen.kt` | Recibe correo y contraseña, y llama al ViewModel |
| 3 | `.../ui/PantallaNotix.kt` | Muestra mis trámites, bandeja del notario y feriados |
| 4 | `.../viewmodel/NotixViewModel.kt` | Controla estado, navegación lógica y acciones de los botones |
| 5 | `.../viewmodel/ReglasTramite.kt` | Valida los formularios antes del envío |
| 6 | `.../data/NotixRepository.kt` | Organiza las llamadas al servicio correcto |
| 7 | `.../data/AuthApi.kt` | Define POST login/registro y GET perfil |
| 8 | `.../data/TramitesApi.kt` | Define GET, POST, PUT y DELETE de trámites |
| 9 | `.../data/FeriadosApi.kt` | Consulta externa Xano, respaldo de Nager.Date |
| 10 | `.../data/RetrofitCliente.kt` | Crea el cliente Retrofit y convierte JSON |
| 11 | `.../data/ApiConfig.kt` | Configura URLs y puertos |
| 12 | `backend/ms-usuarios/src/main/java/com/duoc/notix/usuarios/AuthController.java` | Recibe login/registro, valida y emite token |
| 13 | `backend/ms-tramites/src/main/java/com/duoc/notix/tramites/TramiteController.java` | Recibe peticiones CRUD y comprueba permisos |
| 14 | `backend/.../UsuarioEntity.java` y `TramiteEntity.java` | Representan las tablas de H2 |

**Ojo: dos Repository distintos.** El Kotlin `NotixRepository` decide qué API llamar; el Java `TramiteRepository` extiende `JpaRepository` para leer y escribir en la base H2. NO son la misma clase ni cumplen el mismo papel.

## 2. Explicar cómo se comunican

```text
[LoginScreen.kt] --------> NotixViewModel.login()
                                  |
                                  v
                            NotixRepository.login()
                                  |
                                  v
                           AuthApi.login()             [POST JSON]
                                  |
                                  v
                      ms-usuarios :8081 /api/auth/login
                                  |
                                  v
                          AuthController.java
                                  |
                                  v
                    UsuarioRepository → tabla usuarios H2
                                  |
                                  v
                  JWT de respuesta (authToken) → Android
                                  |
                                  v
                       GET /api/auth/me → usuario y rol
```

Cuando crea una solicitud:

```text
PantallaNotix.kt → NotixViewModel.crearTramite()
                 → ReglasTramite.validarTramite()
                 → NotixRepository.crear()
                 → TramitesApi.crear()
                 → POST http://10.0.2.2:8082/api/tramites
                 → TramiteController.java
                 → TramiteRepository.java (Spring Data JPA)
                 → H2 tabla tramites
                 → respuesta JSON → interfaz
```

**Respuesta oral de 20 segundos:** «La pantalla llama al ViewModel, que valida y envía la acción al Repository. El Repository usa Retrofit y ejecuta el método HTTP. Spring Boot recibe la petición en el Controller, aplica reglas de negocio y utiliza JpaRepository para guardar el trámite en H2. La respuesta vuelve como JSON y se muestra en la pantalla».

## 3. Endpoints precisos

| URL (host desde emulador) | Método | ¿Qué hace? | Archivo backend |
| --- | --- | --- | --- |
| `http://10.0.2.2:8081/api/auth/signup` | POST | Crea Persona (nunca Notario) | `AuthController` |
| `http://10.0.2.2:8081/api/auth/login` | POST | Autentica y responde JWT | `AuthController` |
| `http://10.0.2.2:8081/api/auth/me` | GET | Devuelve datos del usuario | `AuthController` |
| `http://10.0.2.2:8082/api/tramites` | GET | Persona: propios; Notario: todos | `TramiteController` |
| `http://10.0.2.2:8082/api/tramites` | POST | Crea trámite de Persona | `TramiteController` |
| `http://10.0.2.2:8082/api/tramites/{id}` | PUT | Notario aprueba/devuelve | `TramiteController` |
| `http://10.0.2.2:8082/api/tramites/{id}` | DELETE | Persona borra trámite propio permitido | `TramiteController` |
| `https://x8ki-letl-twmt.n7.xano.io/api:8Q9ARAS-/feriados` | GET | Feriados externos guardados en Xano | `FeriadosApi.kt` |
| `https://date.nager.at/api/v3/NextPublicHolidays/CL` | GET | Respaldo si falla Xano | `FeriadosApi.kt` |

**Body JSON de POST trámite** (payload):

```json
{"tipo_tramite":"Poder simple","descripcion":"Autorizacion administrativa"}
```

**No enviamos `usuario_id`** desde Android: el backend lo toma del JWT. Así la Persona no puede registrar una solicitud a nombre de otra cuenta.

**Body JSON de PUT estado:**

```json
{"estado":"Devuelto","observacion":"Falta documento legible"}
```

## 4. Preguntas de profesor: respuestas cortas

**¿Qué es una API REST?** Una interfaz que usa URLs y métodos HTTP para enviar o recibir datos entre sistemas.

**¿Qué es un endpoint?** Una combinación de método HTTP y ruta; por ejemplo, POST /api/tramites.

**¿Qué significa CRUD?** Create (POST), Read (GET), Update (PUT) y Delete (DELETE).

**¿Qué es Retrofit?** La biblioteca Android que convierte los métodos de `AuthApi.kt` y `TramitesApi.kt` en peticiones HTTP y procesa JSON.

**¿Por qué hay dos microservicios?** Uno maneja las cuentas y autenticación, otro los trámites. Tienen responsabilidades y bases H2 separadas.

**¿Cómo se autentica un usuario?** Spring Boot busca el correo y verifica la contraseña BCrypt. Si corresponde, emite un JWT con el ID y rol. Android lo envía en `Authorization: Bearer <token>`.

**¿Cuáles son las tres partes de JWT?** Cabecera (algoritmo), payload o carga útil (información como ID y rol), firma (permite detectar alteraciones). Un JWT firmado NO cifra su payload.

**¿Qué pasa si una Persona intenta aprobar?** Aunque modifique la app, el `TramiteController` lee el rol del token firmado y responde 403. Mostrar u ocultar botones es visual, no protección suficiente.

**¿Dónde se guardan los usuarios y trámites?** En dos bases H2 persistentes, en archivos locales de los microservicios. `UsuarioEntity` y `TramiteEntity` representan las tablas.

**¿Dónde están los feriados?** Xano tiene la tabla de feriados y expone GET /feriados. Si esa API falla, Android consulta Nager.Date. Es la API externa y es independiente de los microservicios propios.

**¿Qué es `Repository`?** En Android organiza las llamadas remotas. En Spring Boot `JpaRepository` realiza operaciones de base de datos.

**¿Qué es un ViewModel?** Una clase que mantiene el estado de pantalla y coordina acciones sin meter toda la lógica en los componentes Compose.

**¿Qué es un payload?** Es el cuerpo JSON que enviamos en un POST/PUT; por ejemplo tipo_tramite y descripcion.

**¿Qué pruebas hicieron?** Hay pruebas JUnit del repositorio Kotlin, del ViewModel y de los endpoints Spring Boot. Antes de afirmar que pasaron, mostrar el resultado de GitHub Actions o de las ejecuciones locales. La cobertura >=80% debe medirse.

**¿Qué es Swagger?** Documentación interactiva de los endpoints. Xano posee Swagger Docs; en Spring Boot no hemos añadido springdoc/OpenAPI todavía. No decir que sí está implementado.

## 5. Demostración de la defensa (paso a paso)

1. En dos terminales, comprobar que `ms-usuarios` y `ms-tramites` estén activos.
2. Iniciar Android; registrarse o iniciar sesión como Persona.
3. Crear un trámite. Mostrar POST en `TramitesApi.kt` y `@PostMapping` en `TramiteController.java`.
4. Salir. Entrar como Notario. Consultar la bandeja.
5. Aprobar o devolver un trámite. Mostrar PUT en Kotlin y Java.
6. Volver a Persona y comprobar el cambio desde GET.
7. Mostrar feriados y explicar que vienen de Xano/Nager.Date, NO de H2.
8. Abrir la pestaña Actions de GitHub y los archivos JUnit.
9. Mostrar la evidencia de APK firmado solo cuando haya sido generado y comprobado.

Para evitar errores durante la defensa, **prueba primero el flujo completo con las cuentas de ensayo**. No prometas resultados no probados.
