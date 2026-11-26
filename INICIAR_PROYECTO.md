# Iniciar Notix-EFT en Windows (guía de comandos)

La app Android se ejecuta en Android Studio, mientras que los microservicios se ejecutan por separado. **Las credenciales y el secreto JWT no deben subirse al repositorio**.

## 1. Preparar

Instala **JDK 21** y Android Studio con SDK 36. Descarga o clona el repositorio:

```powershell
git clone https://github.com/vicentevergara1/Notix-EFT.git
cd Notix-EFT
java -version
```

Los dos microservicios tienen Maven Wrapper (`mvnw.cmd`); no necesitas instalar Maven por separado. La primera ejecución requiere internet para descargar dependencias.

## 2. Crear el secreto que compartirán los 2 microservicios

En **PowerShell de Windows**, ejecuta:

```powershell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
```

Copia el texto producido y úsalo en ambas ventanas como `NOTIX_JWT_SECRET`. Es un secreto privado; no lo compartas ni publiques.

## 3. Terminal 1 (PowerShell): usuarios

Desde la carpeta raíz `Notix-EFT`:

```powershell
cd backend\ms-usuarios
$env:NOTIX_JWT_SECRET = "PEGA_AQUI_TU_SECRETO_GENERADO"
$env:NOTIX_ADMIN_EMAIL = "notario@notix.test"
$env:NOTIX_ADMIN_PASSWORD = "CREA_UNA_CLAVE_PRIVADA_DE_8_O_MAS"
.\mvnw.cmd spring-boot:run
```

Espera a que aparezca el arranque de Spring Boot en el puerto **8081**. No cierres esta ventana. Si esta cuenta Notario ya existía en H2, cambiar la variable de contraseña no modifica su contraseña anterior: para pruebas usa la previamente configurada o crea una base nueva con cuidado.

## 4. Terminal 2 (PowerShell): trámites

En otra ventana de PowerShell:

```powershell
cd RUTA_AL_REPOSITORIO\Notix-EFT\backend\ms-tramites
$env:NOTIX_JWT_SECRET = "EXACTAMENTE_EL_MISMO_SECRETO_DE_TERMINAL_1"
.\mvnw.cmd spring-boot:run
```

Espera al arranque en el puerto **8082**. Ambos servicios deben compartir el mismo secreto para comprobar la firma JWT.

## 5. Probar las APIs en Windows (PowerShell)

Abre una tercera terminal y ejecuta:

```powershell
$datos = @{
  nombre = "Ana Demo"
  correo = "ana@notix.test"
  password = "ClaveDePruebas123"
} | ConvertTo-Json

$registro = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/signup" -Method POST -ContentType "application/json" -Body $datos
$token = $registro.authToken
$headers = @{ Authorization = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8081/api/auth/me" -Headers $headers

$solicitud = @{
  tipo_tramite = "Poder simple"
  descripcion = "Autorizacion administrativa de prueba"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/tramites" -Method POST -ContentType "application/json" -Headers $headers -Body $solicitud
Invoke-RestMethod -Uri "http://localhost:8082/api/tramites" -Headers $headers
```

Si aparece HTTP **409**, el usuario puede haber sido creado anteriormente; en ese caso utiliza `POST /api/auth/login` con correo y contraseña. No publiques el JWT en GitHub ni lo compartas en capturas.

## 6. Android Studio

1. Abre la carpeta **`Notix-EFT/android`**, no `backend`.
2. Espera Gradle Sync (usa JDK 21 y SDK Android 36).
3. Ejecuta `app` en un emulador.
4. La app usa `http://10.0.2.2:8081/` y `http://10.0.2.2:8082/` desde el emulador. No son URLs de internet, sino el PC que ejecuta el servidor.
5. Para un teléfono físico conectado por USB, abre PowerShell y usa `adb reverse tcp:8081 tcp:8081` y `adb reverse tcp:8082 tcp:8082`; la app usa `127.0.0.1` si no detecta emulador.

Pruebas y compilación (PowerShell, dentro de `Notix-EFT/android`):

```powershell
.\gradlew.bat :app:testDebugUnitTest --stacktrace
.\gradlew.bat :app:assembleDebug --stacktrace
```

En cada servicio backend, ejecutar desde su propia carpeta:

```powershell
.\mvnw.cmd test
```

**No afirmes BUILD SUCCESSFUL antes de verlo en la consola.**

## 7. Feriados y Xano

En el proyecto Xano, entra a **APIs → Notix → GET /feriados**, comprueba que su Function Stack consulta la tabla actual `feriados` y que Run devuelve una lista JSON. Las capturas anteriores mostraban errores `Deleted Table`. Si Xano falla, Android intenta Nager.Date.

La tabla `usuarios` y `tramites` de Xano NO es la base de datos de los microservicios Spring Boot. Para evitar confusiones y exposición de datos, desactiva o protege sus endpoints públicos antiguos de escritura/lectura sensible.

## 8. APK firmado

El APK release **aún no está generado**. En Android Studio entra a **Build → Generate Signed App Bundle / APK → APK**, crea/selecciona un keystore `.jks` y genera release. Conserva el `.jks` y las contraseñas en privado. Comprueba que el APK release se instale y funcione.

La pauta EP4 pide evidencia de APK firmado, captura y archivos. Para el EFT, sigue el método de entrega indicado por el profesor; no publiques contraseñas ni llaves privadas en un repositorio abierto.

## 9. Problemas habituales

| Síntoma | Revisar |
| --- | --- |
| Error de conexión / timeout | Verifica que los servicios 8081 y 8082 estén activos |
| HTTP 401 | Falta token, expiró (1 hora) o el secreto no coincide |
| HTTP 403 | El token corresponde a un rol sin permisos |
| HTTP 404 | Ruta incorrecta o recurso inexistente |
| HTTP 409 | Usuario repetido o trámite que ya no está Pendiente |
| Xano Invalid name / Deleted Table | Arreglar referencia en Function Stack |
| Android no compila | Compartir primeras líneas del error y `Caused by` |
