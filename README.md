# Estado verificable de Notix EFT

## Requisitos de la pauta EP4 como referencia

La pauta recibida es **Evaluación Parcial 4 de DSY1105**, no la del examen EFT. No asumir que son idénticas.

| Indicador | Ponderación | Estado actual |
| --- | ---: | --- |
| IE 3.1.1 App, pantallas y flujos sin fallos | 15% | Código preparado, ejecución pendiente |
| IE 3.1.2 Microservicios Spring Boot con DB y endpoints | 15% | Código de 2 servicios subido, ejecutar pruebas pendiente |
| IE 3.1.3 CRUD Android ↔ microservicios | 15% | Retrofit configurado; prueba de extremo a extremo pendiente |
| IE 3.1.4 API externa Retrofit en interfaz | 15% | Xano con respaldo Nager.Date; prueba en vivo pendiente |
| IE 3.2.1 Pruebas unitarias >=80% de lógica | 15% | Pruebas añadidas; cobertura medida pendiente |
| IE 3.3.1 APK release firmado y jks | 10% | Pendiente: requiere Android Studio y llave privada |
| IE 3.3.2 GitHub y Trello colaboración real | 15% | GitHub actualizado; evidencias Trello/comisiones pendientes |

**Advertencia de la pauta EP4:** no modificar el repositorio de esa evaluación después de su fecha límite. Este es el repositorio nuevo `Notix-EFT`, no el anterior.

## Descripción

Notix simula trámites notariales con **cuentas Persona y Notario**, autenticación segura mediante contraseña BCrypt y token JWT, operaciones CRUD y datos persistentes en dos bases H2. No procesa solicitudes notariales reales ni firmas legales.

### Arquitectura

```
Pantalla Compose → NotixViewModel → NotixRepository → Retrofit
                                                    ├─ ms-usuarios :8081 → H2 usuarios
                                                    ├─ ms-tramites :8082 → H2 tramites
                                                    └─ Xano /feriados → Xano Database
                                                        (fallback: Nager.Date)
```

La base Xano NO es la fuente de verdad de usuarios ni trámites: esas entidades corresponden a los microservicios **propios** Spring Boot. Xano se usa como API externa para feriados. Así se distingue claramente el criterio IE 3.1.2/3.1.3 del IE 3.1.4.

## Dónde está cada archivo

| Archivo | Responsabilidad |
| --- | --- |
| `android/.../MainActivity.kt` | Punto de entrada |
| `android/.../ui/LoginScreen.kt` | Registro/login |
| `android/.../ui/PantallaNotix.kt` | Formularios, bandeja y lista de feriados |
| `android/.../viewmodel/NotixViewModel.kt` | Estado y acciones |
| `android/.../viewmodel/ReglasTramite.kt` | Validación |
| `android/.../data/NotixRepository.kt` | Elige qué API consultar |
| `android/.../data/AuthApi.kt` | Rutas HTTP de autenticación |
| `android/.../data/TramitesApi.kt` | GET/POST/PUT/DELETE de trámites |
| `android/.../data/FeriadosApi.kt` | Xano y Nager.Date |
| `android/.../data/RetrofitCliente.kt` | Crea clientes Retrofit |
| `android/.../data/ApiConfig.kt` | URLs locales y externas |
| `backend/ms-usuarios/.../AuthController.java` | Signup/login/perfil y JWT |
| `backend/ms-usuarios/.../UsuarioEntity.java` | Tabla de usuarios en H2 |
| `backend/ms-tramites/.../TramiteController.java` | CRUD y permisos |
| `backend/ms-tramites/.../TramiteEntity.java` | Tabla de trámites en H2 |

La explicación oral está en [GUIA_DEFENSA.md](GUIA_DEFENSA.md) y los pasos exactos en [INICIAR_PROYECTO.md](INICIAR_PROYECTO.md).

## Endpoints

| Método | URL (ruta) | Función |
| --- | --- | --- |
| POST | `http://localhost:8081/api/auth/signup` | Crear Persona |
| POST | `http://localhost:8081/api/auth/login` | Iniciar sesión |
| GET | `http://localhost:8081/api/auth/me` | Ver cuenta autenticada |
| GET | `http://localhost:8082/api/tramites` | Persona ve los suyos; Notario todos |
| POST | `http://localhost:8082/api/tramites` | Crear trámite |
| PUT | `http://localhost:8082/api/tramites/{id}` | Aprobar/devolver |
| DELETE | `http://localhost:8082/api/tramites/{id}` | Borrar trámite propio no aprobado |
| GET | `https://x8ki-letl-twmt.n7.xano.io/api:8Q9ARAS-/feriados` | Consultar feriados de Xano |
| GET | `https://date.nager.at/api/v3/NextPublicHolidays/CL` | Respaldo externo |

En Android emulador el PC se representa con `10.0.2.2` en vez de `localhost`. Trámites requiere `Authorization: Bearer <token>`.

## Estados y pruebas

- Repositorio y código publicados en GitHub.
- Pruebas escritas en ambos servicios Java y en Android.
- Las pruebas no son un resultado aprobado hasta que GitHub Actions o Android Studio las ejecute exitosamente.
- APK firmado, cobertura >=80%, UI en emulador y funcionamiento con Xano: **pendientes de comprobación**.
