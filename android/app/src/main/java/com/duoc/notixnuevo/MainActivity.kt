package com.duoc.notixnuevo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.duoc.notixnuevo.ui.PantallaNotix
import com.duoc.notixnuevo.viewmodel.NotixViewModel

class MainActivity : ComponentActivity() {
    private val vm: NotixViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaNotix(vm)
            }
        }
    }
}
