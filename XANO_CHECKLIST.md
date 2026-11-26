# Revisar Xano antes de mostrar la app en la defensa

En tus capturas Xano daba el error `Query All Records From Deleted Table` y `ERROR_FATAL Invalid name`.

## Reparar endpoints

1. En Xano, ir a APIs → Notix → GET /usuarios.
2. En Function Stack sustituir la función que indica `Deleted Table` por una nueva función Query All Records de la **tabla actual** `usuarios`.
3. En Response devolver la lista de registros.
4. Ejecutar **Run** y comprobar HTTP 200 con una lista JSON.
5. Repetir el procedimiento en GET /tramites y GET /feriados. Revisar también los Function Stacks de POST, PUT y DELETE si usan referencias antiguas.
6. Confirmar en cada endpoint los nombres reales de los `Inputs`, el método y la forma de Response; corregir `data/NotixApi.kt` si el contrato difiere.

## Prueba controlada

- GET /usuarios → lista de usuarios reales ficticios.
- GET /tramites → lista de trámites.
- POST /tramites → crea un **registro de prueba nuevo**.
- PUT /tramites/{tramites_id} → modifica ese registro (campos esperados: usuario_id, tipo_tramite, descripcion, estado).
- DELETE /tramites/{tramites_id} → elimina solamente ese registro de prueba.
- GET /feriados → lee la tabla feriados.

No se considera validado hasta que lo compruebes con Run en Xano y con el modo XANO real en Android. No uses cuentas personales ni datos reales; desactiva POST /seed_notix cuando termines la carga.

## Qué decir al profesor

“Database almacena registros. Cada endpoint del API Group tiene un método HTTP y un Function Stack que consulta o actualiza esas tablas. Desde Android, Retrofit llama a esos endpoints y procesa las respuestas JSON.”
