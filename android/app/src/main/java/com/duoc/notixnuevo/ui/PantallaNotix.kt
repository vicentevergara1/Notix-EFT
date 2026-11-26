package com.duoc.notixnuevo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.notixnuevo.viewmodel.ModoDatos
import com.duoc.notixnuevo.viewmodel.NotixViewModel

private enum class Seccion { USUARIOS, TRAMITES, FERIADOS }

@Composable
fun PantallaNotix(vm: NotixViewModel) {
    val e = vm.estado
    var seccion by rememberSaveable { mutableStateOf(Seccion.USUARIOS) }
    var usuarioId by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var borrarId by remember { mutableStateOf<Long?>(null) }

    borrarId?.let { id ->
        AlertDialog(
            onDismissRequest = { borrarId = null },
            title = { Text("Eliminar tramite") },
            text = { Text("Se borrara del origen " + e.modo + ".") },
            confirmButton = { TextButton(onClick = {
                borrarId = null; vm.eliminarTramite(id)
            }) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { borrarId = null }) {
                Text("Cancelar")
            } }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notix EFT", style = MaterialTheme.typography.headlineMedium)
        Text("Solicitudes notariales - ejemplo academico")
        Spacer(Modifier.height(8.dp))
        Text("Origen actual: " + e.modo, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { vm.cambiarModo(ModoDatos.DEMO) }) {
                Text("Demo local")
            }
            OutlinedButton(onClick = { vm.cambiarModo(ModoDatos.XANO) }) {
                Text("Xano real")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedButton(modifier = Modifier.weight(1f),
                onClick = { seccion = Seccion.USUARIOS }) { Text("Usuarios") }
            OutlinedButton(modifier = Modifier.weight(1f),
                onClick = { seccion = Seccion.TRAMITES }) { Text("Tramites") }
            OutlinedButton(modifier = Modifier.weight(1f),
                onClick = { seccion = Seccion.FERIADOS }) { Text("Feriados") }
        }
        if (e.cargando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (e.mensaje.isNotBlank()) Text(e.mensaje, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            when (seccion) {
                Seccion.USUARIOS -> {
                    item {
                        Button(onClick = vm::cargarUsuarios, enabled = !e.cargando) {
                            Text("Consultar usuarios (GET)")
                        }
                    }
                    items(e.usuarios, key = { it.id }) { u ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(u.nombre + " (#" + u.id + ")")
                                Text(u.correo + " / " + u.tipo_usuario)
                            }
                        }
                    }
                }
                Seccion.TRAMITES -> {
                    item {
                        Button(onClick = vm::cargarTramites, enabled = !e.cargando) {
                            Text("Consultar tramites (GET)")
                        }
                        OutlinedTextField(value = usuarioId, onValueChange = { usuarioId = it },
                            label = { Text("ID usuario") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = tipo, onValueChange = { tipo = it },
                            label = { Text("Tipo de tramite") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it },
                            label = { Text("Descripcion") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { vm.crearTramite(usuarioId, tipo, descripcion) },
                            enabled = !e.cargando) { Text("Crear tramite (POST)") }
                    }
                    items(e.tramites, key = { it.id }) { t ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("#" + t.id + " " + t.tipo_tramite,
                                    style = MaterialTheme.typography.titleMedium)
                                Text(t.descripcion)
                                Text("Usuario #" + t.usuario_id + " | Estado: " + t.estado)
                                Row {
                                    Button(onClick = { vm.cambiarEstado(t, "Aprobado") },
                                        enabled = !e.cargando) { Text("Aprobar") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { vm.cambiarEstado(t, "Devuelto") },
                                        enabled = !e.cargando) { Text("Devolver") }
                                }
                                TextButton(onClick = { borrarId = t.id },
                                    enabled = !e.cargando) { Text("Eliminar (DELETE)") }
                            }
                        }
                    }
                }
                Seccion.FERIADOS -> {
                    item {
                        Button(onClick = vm::cargarFeriados, enabled = !e.cargando) {
                            Text("Consultar feriados (GET)")
                        }
                    }
                    items(e.feriados, key = { it.id }) { f ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(f.nombre)
                                Text(f.fecha + " | " + f.pais)
                            }
                        }
                    }
                }
            }
        }
    }
}
