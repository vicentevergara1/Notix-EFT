# Guía de defensa — Notix EFT

## Dónde está cada cosa

- `MainActivity.kt`: inicia la aplicación Android.
- `ui/PantallaNotix.kt`: interfaz y botones.
- `viewmodel/NotixViewModel.kt`: acciones y estado.
- `viewmodel/ReglasTramite.kt`: validaciones antes de guardar.
- `data/NotixRepository.kt`: organiza las llamadas a Xano.
- `data/RepositorioDemo.kt`: datos temporales de práctica, **no son Xano**.
- `data/NotixApi.kt`: métodos HTTP GET, POST, PUT, DELETE.
- `data/RetrofitCliente.kt`: cliente HTTP y conversión JSON.
- `data/ApiConfig.kt`: URL base del API Group.
- `data/Modelos.kt`: datos que corresponden al JSON.

## ¿Qué pasa al tocar Crear trámite?

Pantalla → ViewModel → Validación → Repositorio → Retrofit → Endpoint POST /tramites → Xano Database → respuesta → pantalla.

## Explicaciones cortas

**GET:** consulta registros. **POST:** crea. **PUT:** modifica. **DELETE:** elimina. **Retrofit:** conecta Android con la API REST y recibe JSON. **Xano:** almacena los registros en tablas y expone endpoints HTTP.

## Estados de verificación

- DEMO local: lógica probada aisladamente en Kotlin/JVM; datos no persistentes.
- Xano real: requiere reparar referencias a tablas eliminadas y ejecutar las APIs.
- Android: compilación y prueba en Android Studio pendientes.
- La rúbrica específica del examen final aún no se ha proporcionado.
