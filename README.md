# Notix-EFT · app académica con autenticación y API REST

Nueva versión del caso NotariaX orientada a comprender y defender su funcionamiento.

## Estado de implementación

- **Android (código creado):** pantalla de registro e inicio de sesión, separación Persona/Notario, listado de trámites, creación, aprobación/devolución, eliminación por Persona y consulta de feriados.
- **Xano (pendiente de configurar y verificar):** autenticación real y permisos obligatorios en backend. Los endpoints actuales tenían errores "Deleted Table" en capturas previas.
- **Pruebas:** reglas de formulario/roles escritas en JUnit; compilación Gradle y prueba en emulador pendientes.
- **Rúbrica EFT:** aún no facilitada. La pauta EP4 anterior requería microservicios Spring Boot, 80% de cobertura y APK firmado, lo cual **no está implementado aquí**.

## Flujo que debes mostrar

Persona inicia sesión → POST /auth/login → GET /auth/me → GET /tramites (solo propios) → POST /tramites → Notario inicia sesión → GET /tramites (todos) → PUT /tramites/{id} → Persona ve el cambio.

La separación de datos y permisos tiene que aplicarse en Xano. El filtrado de botones en Android no es seguridad suficiente.

## Estructura de código

| Archivo | Responsabilidad |
| --- | --- |
| MainActivity.kt | inicia Android y monta Compose |
| ui/LoginScreen.kt | formulario de registro/login |
| ui/PantallaNotix.kt | pantallas de trámites y feriados según rol |
| viewmodel/NotixViewModel.kt | estado de sesión, formularios y acciones |
| viewmodel/ReglasTramite.kt | validaciones sencillas |
| data/NotixRepository.kt | comunica ViewModel con Retrofit |
| data/NotixApi.kt | contratos HTTP de Xano |
| data/RetrofitCliente.kt | base URL, Gson y Retrofit |
| data/ApiConfig.kt | dirección del API Group de Xano |
| data/Modelos.kt | objetos JSON y solicitudes |

**Recorrido:** botón → ViewModel → Repository → Retrofit (NotixApi) → Xano → tabla Database.

## Abrir en Android Studio (Windows)

1. Abrir carpeta `android/` de este repositorio.
2. Usar Android SDK 36, JDK 21 y sincronizar Gradle.
3. Ejecutar la app y comprobar login. Sin la configuración de Xano documentada en `XANO_AUTENTICACION.md`, el login no podrá completarse.
4. En la terminal de Android Studio/PowerShell, dentro de `android/`:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

## Seguridad y limitaciones

- No almacena la contraseña; guarda el token solo en memoria del ViewModel y lo descarta al cerrar sesión. Al reiniciar se requiere login.
- La aplicación no incluye un modo demo ni usuarios/contraseñas falsas que simulen autenticación.
- Se utiliza HTTPS con Xano y la cabecera Bearer para rutas protegidas.
- Endpoints actuales de Xano deben ajustarse al contrato exacto de `XANO_AUTENTICACION.md`.
- **No reutilizar/compartir datos personales reales en el ambiente académico.**
- El proyecto no reemplaza requisitos aún desconocidos de la rúbrica EFT.

