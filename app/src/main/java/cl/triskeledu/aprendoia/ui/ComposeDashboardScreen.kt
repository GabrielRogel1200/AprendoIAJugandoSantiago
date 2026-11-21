package cl.triskeledu.aprendoia.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF102B4C)
private val Turquoise = Color(0xFF087F84)
private val Background = Color(0xFFF2F8FB)

private data class Destination(val icon: String, val label: String, val route: String)

private val destinations = listOf(
    Destination("🗺️", "Mapa interactivo de Santiago", "map"),
    Destination("📷", "Escanear un código QR", "scan"),
    Destination("🌐", "Lugares del servidor REST", "remote"),
    Destination("☀️", "Clima de Santiago", "weather"),
    Destination("📚", "Mi colección", "collection"),
    Destination("🏆", "Puntaje y progreso", "progress"),
    Destination("🕒", "Historial", "history"),
    Destination("🔔", "Notificaciones", "notifications"),
    Destination("👤", "Perfil y configuración", "profile")
)

/** Pantalla 1 migrada de XML a Compose sin modificar QR, mapa ni persistencia Room. */
@Composable
fun ComposeDashboardScreen(
    userName: String,
    score: Int,
    placesCount: Int,
    watchedCount: Int,
    loading: Boolean,
    onNavigate: (String) -> Unit
) {
    MaterialTheme(colorScheme = androidx.compose.material3.lightColorScheme(
        primary = Turquoise,
        onPrimary = Color.White,
        background = Background,
        surface = Color.White,
        onSurface = Navy
    )) {
        Surface(color = Background) {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                    ) {
                        Column(Modifier.padding(22.dp).animateContentSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("APRENDO IA JUGANDO", color = Color(0xFFB2F2ED), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("¡Hola, $userName!", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                            Text("Descubre Santiago, aprende y suma puntos", color = Color(0xFFE6F3FF))
                            if (loading) {
                                CircularProgressIndicator(color = Color.White)
                            } else {
                                Text("🏆 $score puntos  ·  📍 $placesCount lugares", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("🎬 $watchedCount videos revisados", color = Color(0xFFE6F3FF))
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(6.dp))
                    Text("Explora tu aventura", color = Navy, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                }
                items(destinations, key = { it.route }) { destination ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(destination.route) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Navy),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp)
                        ) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(destination.icon, fontSize = 23.sp)
                                Text(destination.label, modifier = Modifier.padding(start = 14.dp), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}
