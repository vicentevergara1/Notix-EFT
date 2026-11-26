package com.duoc.notixnuevo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.notixnuevo.data.Tramite
import com.duoc.notixnuevo.viewmodel.NotixViewModel
import com.duoc.notixnuevo.viewmodel.esNotario
import com.duoc.notixnuevo.viewmodel.esPersona

@Composable
fun PantallaNotix(vm: NotixViewModel) {
    val e = vm.estado
    val usuario = e.usuario
    if (usuario == null) {
        LoginScreen(vm)
        return
    }

    var tipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var devolver by remember { mutableStateOf<Tramite?>(null) }
    var eliminar by remember { mutableStateOf<Tramite?>(null) }

    devolver?.let { tramite ->
        AlertDialog(
            onDismissRequest = { devolver = null },
            title = { Text("Devolver tramite #${tramite.id}") },
            text = {
                OutlinedTextField(observacion, { observacion = it },
                    label = { Text("Motivo de devolucion") })
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.resolverTramite(tramite.id, "Devuelto", observacion)
                    devolver = null
                    observacion = ""
                }) { Text("Devolver") }
            },
            dismissButton = { TextButton(onClick = { devolver = null }) {
                Text("Cancelar")
            } }
        )
    }
    eliminar?.let { tramite ->
        AlertDialog(
            onDismissRequest = { eliminar = null },
            title = { Text("Eliminar tramite #${tramite.id}") },
            text = { Text("Esta accion no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminarTramite(tramite)
                    eliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { eliminar = null }) {
                Text("Cancelar")
            } }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notix", style = MaterialTheme.typography.headlineMedium)
        Text("Hola, ${usuario.nombre} · ${usuario.tipo_usuario}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = vm::cargarDatos, enabled = !e.cargando) { Text("Actualizar (GET)") }
            OutlinedButton(onClick = vm::salir, enabled = !e.cargando) { Text("Salir") }
        }
        if (e.cargando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (e.mensaje.isNotBlank()) Text(e.mensaje)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (esPersona(usuario.tipo_usuario)) {
                item {
                    Text("Crear solicitud", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(tipo, { tipo = it },
                        label = { Text("Tipo de tramite") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(descripcion, { descripcion = it },
                        label = { Text("Descripcion") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        vm.crearTramite(tipo, descripcion)
                    }, enabled = !e.cargando) { Text("Enviar tramite (POST)") }
                }
            }
            item {
                Text(
                    if (esNotario(usuario.tipo_usuario)) "Bandeja del notario"
                    else "Mis tramites",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(e.tramites, key = { it.id }) { tramite ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("#${tramite.id} · ${tramite.tipo_tramite}",
                            style = MaterialTheme.typography.titleMedium)
                        Text(tramite.descripcion)
                        Text("Estado: ${tramite.estado}")
                        if (!tramite.observacion.isNullOrBlank()) {
                            Text("Observacion: ${tramite.observacion}")
                        }
                        if (esNotario(usuario.tipo_usuario) &&
                            tramite.estado == "Pendiente") {
                            Row {
                                Button(onClick = {
                                    vm.resolverTramite(tramite.id, "Aprobado", "")
                                }, enabled = !e.cargando) { Text("Aprobar (PUT)") }
                                Spacer(Modifier.width(6.dp))
                                OutlinedButton(onClick = { devolver = tramite },
                                    enabled = !e.cargando) { Text("Devolver (PUT)") }
                            }
                        }
                        if (esPersona(usuario.tipo_usuario) &&
                            tramite.usuario_id == usuario.id &&
                            tramite.estado != "Aprobado") {
                            TextButton(onClick = { eliminar = tramite }, enabled = !e.cargando) {
                                Text("Eliminar (DELETE)")
                            }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(6.dp))
                Text("Feriados", style = MaterialTheme.typography.titleMedium)
            }
            items(e.feriados, key = { it.id }) { feriado ->
                Text("${feriado.nombre} · ${feriado.fecha} · Fuente: ${feriado.origen ?: "API externa"}")
            }
        }
    }
}
