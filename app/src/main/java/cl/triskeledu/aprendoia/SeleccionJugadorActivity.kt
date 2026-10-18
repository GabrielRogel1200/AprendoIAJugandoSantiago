package cl.triskeledu.aprendoia

import android.os.Bundle
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import cl.triskeledu.aprendoia.data.database.AppDatabase
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cl.triskeledu.aprendoia.data.entities.UserEntity
import cl.triskeledu.aprendoia.ui.SeleccionJugadorViewModel
import kotlinx.coroutines.launch

class SeleccionJugadorActivity : AppCompatActivity() {

    private val viewModel: SeleccionJugadorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_seleccion_jugador)

        val contenedor = findViewById<LinearLayout>(
            R.id.contenedorJugadores
        )

        val btnRegistrar = findViewById<Button>(
            R.id.btnRegistrarJugador
        )

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, AppScreenActivity::class.java).putExtra("screen", "register"))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usuarios.collect { usuarios ->
                    mostrarUsuarios(contenedor, usuarios)
                }
            }
        }
    }

    private fun mostrarUsuarios(
        contenedor: LinearLayout,
        usuarios: List<UserEntity>
    ) {
        contenedor.removeAllViews()

        for (usuario in usuarios) {

            val boton = Button(this).apply {
                text = "${usuario.alias} - ${usuario.role}"
                isAllCaps = false

                setOnClickListener {
                    startActivity(Intent(this@SeleccionJugadorActivity, AppScreenActivity::class.java)
                        .putExtra("screen", "login").putExtra("userId", usuario.id))
                }
            }

            contenedor.addView(boton)
        }
    }
}
