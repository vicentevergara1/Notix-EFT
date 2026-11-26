# Xano en el nuevo Notix-EFT

Xano se usa **únicamente como API externa de feriados**, a través de la URL que compartiste:
`https://x8ki-letl-twmt.n7.xano.io/api:8Q9ARAS-/`

Se necesita un endpoint público de solo lectura `GET /feriados`, que responda directamente una lista JSON:

```json
[
  {"id":1,"nombre":"Navidad","fecha":"2026-12-25","pais":"CL"}
]
```

Los datos son ilustrativos. El retorno debe corresponder a tus tablas reales.

## Si aparece Deleted Table

- Entra a APIs → Notix → GET /feriados.
- En `Function Stack`, elimina solo la función que hace referencia a la tabla antigua (no borres la tabla ni los registros).
- Agrega una consulta a la **tabla actual feriados** y configura su Response para retornar directamente la lista.
- Prueba Run hasta obtener HTTP 200.

## Seguridad

Las capturas antiguas mostraban endpoints CRUD públicos para `usuarios` y un `seed_notix` público. No hace falta utilizarlos desde la nueva app. **Desactívalos o protégelos** para impedir acceso o modificación de datos no autorizados.

## Cómo explicarlo

«Spring Boot maneja usuarios y trámites. Xano es una API externa independiente que ofrece feriados. Notix consulta Xano por Retrofit y los muestra en el catálogo. Si Xano no responde, solicita los feriados a Nager.Date. La tabla de Xano no sustituye las bases H2 de nuestros microservicios propios».
