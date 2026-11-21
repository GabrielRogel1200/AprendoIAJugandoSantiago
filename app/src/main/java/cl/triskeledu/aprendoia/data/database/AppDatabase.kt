package cl.triskeledu.aprendoia.data.database
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.first
import cl.triskeledu.aprendoia.data.dao.GameDao
import cl.triskeledu.aprendoia.data.entities.*
import cl.triskeledu.aprendoia.domain.PasswordHasher

@Database(entities=[UserEntity::class,PointEntity::class,VideoEntity::class,DiscoveryEntity::class,SelfieEntity::class,ActivityEntity::class,NotificationEntity::class],version=1,exportSchema=false)
abstract class AppDatabase:RoomDatabase() {
    abstract fun gameDao():GameDao
    suspend fun seedInitialAccounts() {
        val dao=gameDao()
        for ((name,alias,role) in listOf(Triple("Administrador","Admin","ADMIN"),Triple("Contenido","GenCont","CREATOR"))) {
            if(dao.userByAlias(alias)==null) {
                val salt=PasswordHasher.newSalt()
                dao.insertUser(UserEntity(name=name,alias=alias,role=role,passwordSalt=salt,passwordHash=PasswordHasher.hash("123",salt)))
            }
        }
    }
    suspend fun seedDemoPoints() {
        val dao=gameDao()
        val existing = dao.observePoints().first()
        if(existing.isNotEmpty()) {
            // Actualiza únicamente las descripciones de demostración antiguas; conserva contenido editado.
            val descriptions = demoDescriptions()
            existing.forEach { point ->
                val replacement=descriptions[point.title]
                if(replacement!=null && point.description in listOf("Centro histórico de Santiago","Parque y mirador histórico","Área verde cultural de Santiago","Edificio histórico de Santiago"))
                    dao.updatePoint(point.copy(description=replacement))
            }
            return
        }
        val videoId=dao.insertVideo(VideoEntity(title="Introducción a la Inteligencia Artificial (demo)",topic="Fundamentos de IA",author="Triskeledu - ejemplo",videoUri="https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",introduction="Video demostrativo de reproducción. Reemplazar por cápsula de IA autorizada.",approved=true))
        val points=listOf(
            PointEntity(title="Plaza de Armas",description=demoDescriptions().getValue("Plaza de Armas"),latitude=-33.4372,longitude=-70.6506,qrPayload="TRISKEL-PLAZA-ARMAS",videoId=videoId),
            PointEntity(title="Cerro Santa Lucía",description=demoDescriptions().getValue("Cerro Santa Lucía"),latitude=-33.4407,longitude=-70.6447,qrPayload="TRISKEL-SANTA-LUCIA",videoId=videoId),
            PointEntity(title="Parque Forestal",description=demoDescriptions().getValue("Parque Forestal"),latitude=-33.4362,longitude=-70.6373,qrPayload="TRISKEL-PARQUE-FORESTAL",videoId=videoId),
            PointEntity(title="Palacio de La Moneda",description=demoDescriptions().getValue("Palacio de La Moneda"),latitude=-33.4429,longitude=-70.6539,qrPayload="TRISKEL-LA-MONEDA",videoId=videoId)
        )
        points.forEach { dao.insertPoint(it) }
    }
    private fun demoDescriptions() = mapOf(
        "Plaza de Armas" to "La Plaza de Armas es el núcleo histórico del centro de Santiago. Desde la época colonial ha sido un lugar de encuentro ciudadano y alrededor de ella se ubican edificios emblemáticos, como la Catedral Metropolitana y el edificio del Correo Central. Observa su trazado urbano y descubre cómo ha cambiado la vida en la ciudad.",
        "Cerro Santa Lucía" to "El cerro Santa Lucía es uno de los espacios patrimoniales más conocidos de Santiago. Sus senderos, escalinatas, jardines y miradores invitan a conocer la historia de la ciudad desde un punto elevado. Desde aquí puedes observar distintos sectores del centro y comprender cómo se relacionan la geografía y la urbanización.",
        "Parque Forestal" to "El Parque Forestal es un corredor verde situado junto al río Mapocho. Es un espacio de paseo, descanso y actividades culturales cercano al Museo Nacional de Bellas Artes. Sus árboles y recorridos peatonales permiten conversar sobre la importancia de las áreas verdes en una ciudad y cómo contribuyen a su vida cotidiana.",
        "Palacio de La Moneda" to "El Palacio de La Moneda es la sede del Poder Ejecutivo de Chile y uno de los edificios más reconocibles del centro de Santiago. Su arquitectura y su historia lo convierten en un punto relevante para comprender las instituciones del país. En sus alrededores se encuentran plazas y espacios públicos que pueden observarse durante una visita responsable."
    )
    companion object {
        @Volatile private var instance:AppDatabase?=null
        fun get(context:Context):AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"aprendo_ia.db").build().also { instance=it }
        }
    }
}
