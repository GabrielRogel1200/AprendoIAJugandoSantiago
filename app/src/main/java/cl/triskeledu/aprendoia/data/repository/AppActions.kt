package cl.triskeledu.aprendoia.data.repository

import cl.triskeledu.aprendoia.data.database.AppDatabase
import cl.triskeledu.aprendoia.data.entities.*
import cl.triskeledu.aprendoia.domain.PasswordHasher
import androidx.room.withTransaction

class AppActions(private val db: AppDatabase) {
    private val dao=db.gameDao()
    suspend fun user(id:Long)=dao.userById(id)
    suspend fun point(id:Long)=dao.pointById(id)
    suspend fun video(id:Long)=dao.videoById(id)
    suspend fun updateUser(user:UserEntity)=dao.updateUser(user)
    suspend fun deleteUser(user:UserEntity):Boolean {
        if(user.role!="CLIENT") return false
        dao.deleteUser(user); return true
    }
    suspend fun savePoint(point:PointEntity) { if(point.id==0L) dao.insertPoint(point) else dao.updatePoint(point) }
    suspend fun deletePoint(point:PointEntity)=dao.deletePoint(point)
    suspend fun saveVideo(video:VideoEntity) { if(video.id==0L) dao.insertVideo(video) else dao.updateVideo(video) }
    suspend fun recordVideo(userId:Long,pointId:Long)=GameRepository(db).markVideoWatched(userId,pointId)
    suspend fun addNotification(title:String,message:String,pointId:Long?=null)=dao.insertNotification(NotificationEntity(title=title,message=message,pointId=pointId))
    suspend fun readNotification(item:NotificationEntity)=dao.updateNotification(item.copy(isRead=true))
    suspend fun changePassword(user:UserEntity,password:String) {
        val salt=PasswordHasher.newSalt()
        dao.updateUser(user.copy(passwordSalt=salt,passwordHash=PasswordHasher.hash(password,salt)))
    }
}
