package cl.triskeledu.aprendoia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cl.triskeledu.aprendoia.data.database.AppDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("AprendoIA", "MainActivity iniciada")

        // Mostrar pantalla de bienvenida
        setContentView(R.layout.activity_main)

        Log.d("AprendoIA", "Pantalla XML cargada")

        // Conectar botón Comenzar
        val btnComenzar =
            findViewById<Button>(R.id.btnComenzar)

        // Abrir selección de jugadores
        btnComenzar.setOnClickListener {

            val intent = Intent(
                this,
                SeleccionJugadorActivity::class.java
            )

            startActivity(intent)
        }

        // Inicializar base de datos Room
        lifecycleScope.launch {
            try {
                val database = AppDatabase.get(
                    this@MainActivity
                )

                database.seedInitialAccounts()
                database.seedDemoPoints()

                Log.d(
                    "AprendoIA",
                    "Base de datos inicializada"
                )

            } catch (e: Exception) {
                Log.e(
                    "AprendoIA",
                    "Error al inicializar Room",
                    e
                )
            }
        }
    }
}
