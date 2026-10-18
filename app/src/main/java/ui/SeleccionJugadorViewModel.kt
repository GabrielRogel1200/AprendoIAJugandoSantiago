
package cl.triskeledu.aprendoia.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.triskeledu.aprendoia.data.database.AppDatabase

class SeleccionJugadorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao =
        AppDatabase.get(application).gameDao()

    val usuarios = dao.observeUsers()
}
