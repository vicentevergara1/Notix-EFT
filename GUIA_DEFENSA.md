# Guía corta de defensa — Notix EFT

## Preguntas típicas

**¿Dónde inicia la aplicación?**
En `MainActivity.kt`, que llama a `PantallaNotix(vm)`.

**¿Cómo funciona el login?**
`LoginScreen.kt` llama `vm.login()`. El ViewModel usa `NotixRepository.login()`, que llama a Retrofit `POST /auth/login`. Xano valida el usuario y entrega `authToken`. Después llamamos `GET /auth/me` para obtener su rol.

**¿Qué es el token?**
Un comprobante de sesión generado por Xano que se manda como `Authorization: Bearer TOKEN` al consultar y cambiar trámites. No es la contraseña.

**¿Por qué un usuario Persona no puede aprobar?**
La interfaz oculta ese botón y el ViewModel lo rechaza. Más importante: Xano también debe comprobar el rol de la cuenta autenticada y devolver HTTP 403 a quien no sea Notario.

**¿Cómo se crea un trámite?**
`PantallaNotix.kt` → `NotixViewModel.crearTramite()` → `validarTramite()` → `NotixRepository.crear()` → `NotixApi.crear()` → `POST /tramites` → Xano guarda el registro usando el ID del usuario autenticado.

**¿Cómo se aprueba?**
El Notario presiona Aprobar en pantalla → ViewModel → Repository → `PUT /tramites/{id}`. Xano verifica rol Notario y estado pendiente; guarda estado Aprobado.

**¿Cómo se consultan feriados?**
`GET /feriados` mediante Retrofit; Xano lee Database/feriados. No confundir con la consulta directa a Nager.Date del Notix anterior.

**¿Qué ocurre si Xano falla?**
No se simula éxito. El ViewModel captura el error y lo muestra; los registros no se modifican localmente.

**¿Qué es CRUD?**
Create (POST), Read (GET), Update (PUT), Delete (DELETE).

## Evidencias que faltan

- Ejecutar y comprobar Xano (auth, roles, CRUD).
- Compilar y probar Android Studio.
- Verificar criterio por criterio cuando llegue la rúbrica EFT.

No afirmar que todo está funcionando hasta contar con resultados comprobados.
