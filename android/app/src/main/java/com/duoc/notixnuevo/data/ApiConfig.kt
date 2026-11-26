package com.duoc.notixnuevo.data

import android.os.Build

object ApiConfig {
    // El emulador ve el PC como 10.0.2.2. Para USB: usar adb reverse y 127.0.0.1.
    private val equipoLocal: String
        get() {
            val emulador = Build.FINGERPRINT.contains("generic", ignoreCase = true) ||
                Build.MODEL.contains("Emulator", ignoreCase = true) ||
                Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
                Build.HARDWARE.contains("goldfish", ignoreCase = true)
            return if (emulador) "10.0.2.2" else "127.0.0.1"
        }

    val USUARIOS_URL: String get() = "http://$equipoLocal:8081/"
    val TRAMITES_URL: String get() = "http://$equipoLocal:8082/"

    // La consulta externa está separada de los microservicios Spring Boot.
    const val XANO_URL = "https://x8ki-letl-twmt.n7.xano.io/api:8Q9ARAS-/"
    const val NAGER_URL = "https://date.nager.at/"
}
