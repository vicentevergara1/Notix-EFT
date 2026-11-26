package com.duoc.notixnuevo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.duoc.notixnuevo.viewmodel.NotixViewModel

@Composable
fun LoginScreen(vm: NotixViewModel) {
    var registro by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val e = vm.estado

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Notix", style = MaterialTheme.typography.headlineLarge)
        Text(if (registro) "Crea una cuenta Persona" else "Ingresa a tu cuenta")
        Spacer(Modifier.height(18.dp))
        if (registro) {
            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(password, { password = it }, label = { Text("Contrasena") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(12.dp))
        Button(
            enabled = !e.cargando,
            onClick = {
                if (registro) vm.registro(nombre, correo, password)
                else vm.login(correo, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (registro) "Registrarme" else "Iniciar sesion") }
        TextButton(onClick = { registro = !registro; password = "" }, enabled = !e.cargando) {
            Text(if (registro) "Ya tengo cuenta" else "Crear cuenta de Persona")
        }
        if (e.cargando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (e.mensaje.isNotBlank()) Text(e.mensaje)
    }
}
