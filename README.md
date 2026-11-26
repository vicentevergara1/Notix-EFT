# Notix-EFT — versión simple y explicable

App Android académica Kotlin/Jetpack Compose para el caso NotariaX. Repositorio nuevo, separado de la entrega antigua.

## Dos modos claramente identificados

- **DEMO local:** muestra usuarios/feriados de ejemplo y permite crear, aprobar, devolver y eliminar trámites sin internet; no guarda en Xano y los cambios se pierden al reiniciar.
- **XANO real:** consume la API REST y las tablas `usuarios`, `tramites` y `feriados` del grupo Notix mediante Retrofit; necesita que los endpoints estén reparados.

**Importante:** anteriormente Xano devolvía `Query All Records From Deleted Table` / `Invalid name`. No está comprobado que las rutas remotas estén funcionando. No presentar DEMO como si fuera Xano.

## Abrir Android Studio

1. Abrir la carpeta `android/` (contiene `settings.gradle.kts`).
2. Usar JDK 21 y Android SDK 36. Esperar Gradle Sync.
3. Ejecutar `app` en emulador. Cambiar entre Modo DEMO / XANO real.
4. En PowerShell dentro de `android/`:
   ` .\\gradlew.bat :app:testDebugUnitTest ` y ` .\\gradlew.bat :app:assembleDebug `.

## Qué archivo hace qué

- `MainActivity.kt`: inicia la app.
- `ui/PantallaNotix.kt`: pinta los formularios, botones y listas.
- `viewmodel/NotixViewModel.kt`: recibe eventos y controla estado.
- `viewmodel/ReglasTramite.kt`: valida campos antes de hacer POST.
- `data/NotixRepository.kt`: separa UI de comunicación remota.
- `data/NotixApi.kt`: declara GET/POST/PUT/DELETE.
- `data/RetrofitCliente.kt`: cliente Retrofit + JSON.
- `data/ApiConfig.kt`: URL base de Xano.
- `data/Modelos.kt`: datos recibidos y enviados.
- `data/RepositorioDemo.kt`: implementación local para practicar.

**Recorrido de Crear:** Pantalla → ViewModel → Validación → Repositorio → Retrofit → Xano → Database.

## Endpoints esperados

Base URL: `https://x8ki-letl-twmt.n7.xano.io/api:8Q9ARAS-/`

| Método | Ruta | Función |
|---|---|---|
| GET | /usuarios | consultar usuarios |
| GET | /tramites | consultar trámites |
| GET | /feriados | consultar feriados |
| POST | /tramites | crear |
| PUT | /tramites/{tramites_id} | actualizar estado |
| DELETE | /tramites/{tramites_id} | eliminar |

Comprueba el contrato real de Xano con Run & Debug antes de usar el modo XANO. El PUT envía usuario_id, tipo_tramite, descripcion y estado.

## Estado y alcance

- Pruebas Kotlin/JVM aisladas de reglas y CRUD local: superadas en preparación.
- **Pendiente:** Android Gradle Sync, APK, pruebas con emulador, CRUD Xano real.
- **Pendiente:** rúbrica del Examen Final Transversal. No se afirma que esta base cubra toda la pauta. La pauta EP4 anterior también requería microservicios Spring Boot, cobertura ≥80 % y APK firmado.
- No subir datos personales ni contraseñas a endpoints públicos. Desactivar `seed_notix` cuando termine la carga de datos de prueba.

Ver también `GUIA_DEFENSA.md`.
