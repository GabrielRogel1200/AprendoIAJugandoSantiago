package cl.triskeledu.aprendoia.data.repository
import androidx.room.withTransaction
import cl.triskeledu.aprendoia.data.database.AppDatabase
import cl.triskeledu.aprendoia.data.entities.*
import cl.triskeledu.aprendoia.domain.*

sealed interface ScanResult { data class Awarded(val point:PointEntity,val points:Int):ScanResult; data object AlreadyFound:ScanResult; data object InvalidQr:ScanResult; data object Unauthorized:ScanResult }
class GameRepository(private val db:AppDatabase) {
    private val dao=db.gameDao()
    fun users()=dao.observeUsers()
    fun points()=dao.observePoints()
    fun discoveries(userId:Long)=dao.observeDiscoveries(userId)
    fun history(userId:Long)=dao.observeActivities(userId)
    fun score(userId:Long)=dao.observePointsTotal(userId)
    suspend fun login(alias:String,password:String):UserEntity? {
        val user=dao.userByAlias(alias) ?: return null
        return user.takeIf { PasswordHasher.verify(password,it.passwordSalt,it.passwordHash) }
    }
    suspend fun register(name:String,alias:String,email:String,phone:String,password:String,address:String?,avatar:String):Result<Long> {
        val errors=AuthValidator.validate(name,alias,email,phone,password)
        if(errors.isNotEmpty()) return Result.failure(IllegalArgumentException(errors.values.joinToString("; ")))
        return runCatching {
            val salt=PasswordHasher.newSalt()
            dao.insertUser(UserEntity(name=name.trim(),alias=alias,email=email.trim(),phone=phone.trim(),address=address,avatar=avatar,passwordSalt=salt,passwordHash=PasswordHasher.hash(password,salt)))
        }
    }
    suspend fun scanQr(userId:Long,payload:String):ScanResult = db.withTransaction {
        val user=dao.userById(userId) ?: return@withTransaction ScanResult.Unauthorized
        if(user.role!="CLIENT") return@withTransaction ScanResult.Unauthorized
        val point=dao.pointByQr(payload) ?: return@withTransaction ScanResult.InvalidQr
        if(dao.discovery(userId,point.id)!=null) return@withTransaction ScanResult.AlreadyFound
        val points=(10000-dao.totalPoints(userId)).coerceIn(0,10).coerceAtMost(10)
        dao.insertDiscovery(DiscoveryEntity(userId=userId,pointId=point.id,videoId=point.videoId))
        dao.insertActivity(ActivityEntity(userId=userId,kind="QR",description="QR: ${point.title}",points=points))
        ScanResult.Awarded(point,points)
    }
    // La validación visual del rostro y del QR debe ejecutarse ANTES de invocar esta función.
    suspend fun awardVerifiedSelfie(userId:Long,pointId:Long,photoUri:String):Boolean = db.withTransaction {
        val user=dao.userById(userId) ?: return@withTransaction false
        if(user.role!="CLIENT" || dao.discovery(userId,pointId)==null || dao.selfie(userId,pointId)!=null) return@withTransaction false
        val points=(10000-dao.totalPoints(userId)).coerceIn(0,100)
        if(dao.insertSelfie(SelfieEntity(userId=userId,pointId=pointId,photoUri=photoUri))<0) return@withTransaction false
        dao.insertActivity(ActivityEntity(userId=userId,kind="SELFIE",description="Selfie turística",points=points))
        true
    }
    suspend fun markVideoWatched(userId:Long,pointId:Long) = db.withTransaction {
        val discovery=dao.discovery(userId,pointId) ?: return@withTransaction
        if(discovery.watchedAt==null) {
            dao.updateDiscovery(discovery.copy(watchedAt=System.currentTimeMillis()))
            dao.insertActivity(ActivityEntity(userId=userId,kind="VIDEO",description="Video revisado"))
        }
    }
}
