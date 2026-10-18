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
        if (dao.observePoints().first().isNotEmpty()) return
        val videoId=dao.insertVideo(VideoEntity(title="Introducción a la Inteligencia Artificial (demo)",topic="Fundamentos de IA",author="Triskeledu - ejemplo",videoUri="https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",introduction="Video demostrativo de reproducción. Reemplazar por cápsula de IA autorizada.",approved=true))
        val points=listOf(
            PointEntity(title="Plaza de Armas",description="Centro histórico de Santiago",latitude=-33.4372,longitude=-70.6506,qrPayload="TRISKEL-PLAZA-ARMAS",videoId=videoId),
            PointEntity(title="Cerro Santa Lucía",description="Parque y mirador histórico",latitude=-33.4407,longitude=-70.6447,qrPayload="TRISKEL-SANTA-LUCIA",videoId=videoId),
            PointEntity(title="Parque Forestal",description="Área verde cultural de Santiago",latitude=-33.4362,longitude=-70.6373,qrPayload="TRISKEL-PARQUE-FORESTAL",videoId=videoId),
            PointEntity(title="Palacio de La Moneda",description="Edificio histórico de Santiago",latitude=-33.4429,longitude=-70.6539,qrPayload="TRISKEL-LA-MONEDA",videoId=videoId)
        )
        points.forEach { dao.insertPoint(it) }
    }
    companion object {
        @Volatile private var instance:AppDatabase?=null
        fun get(context:Context):AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"aprendo_ia.db").build().also { instance=it }
        }
    }
}
