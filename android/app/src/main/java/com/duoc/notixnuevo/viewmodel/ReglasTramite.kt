package com.duoc.notixnuevo.viewmodel

fun validarCredenciales(correo: String, password: String): String? {
    if (!correo.contains("@") || correo.substringAfter("@").isBlank()) {
        return "Ingresa un correo valido."
    }
    if (password.isBlank()) return "Ingresa tu contrasena."
    return null
}

fun validarRegistro(nombre: String, correo: String, password: String): String? {
    if (nombre.isBlank()) return "Ingresa tu nombre."
    val error = validarCredenciales(correo, password)
    if (error != null) return error
    if (password.length < 8) return "La contrasena debe tener al menos 8 caracteres."
    return null
}

fun validarTramite(tipo: String, descripcion: String): String? {
    if (tipo.isBlank()) return "Selecciona un tipo de tramite."
    if (descripcion.isBlank()) return "Escribe una descripcion."
    return null
}

fun esNotario(rol: String) = rol.equals("Notario", ignoreCase = true)
fun esPersona(rol: String) = rol.equals("Persona", ignoreCase = true)
