package cl.triskeledu.aprendoia.data.dao
import androidx.room.*
import cl.triskeledu.aprendoia.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict=OnConflictStrategy.ABORT) suspend fun insertUser(user:UserEntity):Long
    @Query("SELECT COUNT(*) FROM users") suspend fun countUsers():Int
    @Query("SELECT * FROM users WHERE alias=:alias LIMIT 1") suspend fun userByAlias(alias:String):UserEntity?
    @Query("SELECT * FROM users WHERE id=:id LIMIT 1") suspend fun userById(id:Long):UserEntity?
    @Query("SELECT * FROM users ORDER BY name") fun observeUsers():Flow<List<UserEntity>>
    @Query("SELECT * FROM points WHERE id=:id LIMIT 1") suspend fun pointById(id:Long):PointEntity?
    @Query("SELECT * FROM videos WHERE approved=1 ORDER BY title") fun approvedVideos():Flow<List<VideoEntity>>
    @Query("SELECT * FROM selfies WHERE userId=:userId") fun observeSelfiesAll(userId:Long):Flow<List<SelfieEntity>>
    @Query("SELECT * FROM notifications WHERE id=:id LIMIT 1") suspend fun notificationById(id:Long):NotificationEntity?
    @Update suspend fun updateNotification(item:NotificationEntity)
    @Update suspend fun updateVideo(video:VideoEntity)
    @Delete suspend fun deleteVideo(video:VideoEntity)
    @Update suspend fun updateUser(user:UserEntity)
    @Delete suspend fun deleteUser(user:UserEntity)
    @Insert suspend fun insertPoint(point:PointEntity):Long
    @Update suspend fun updatePoint(point:PointEntity)
    @Delete suspend fun deletePoint(point:PointEntity)
    @Query("SELECT * FROM points ORDER BY title") fun observePoints():Flow<List<PointEntity>>
    @Query("SELECT * FROM points WHERE qrPayload=:payload LIMIT 1") suspend fun pointByQr(payload:String):PointEntity?
    @Insert suspend fun insertVideo(video:VideoEntity):Long
    @Query("SELECT * FROM videos ORDER BY title") fun observeVideos():Flow<List<VideoEntity>>
    @Query("SELECT * FROM videos WHERE id=:id LIMIT 1") suspend fun videoById(id:Long):VideoEntity?
    @Insert(onConflict=OnConflictStrategy.IGNORE) suspend fun insertDiscovery(discovery:DiscoveryEntity):Long
    @Query("SELECT * FROM discoveries WHERE userId=:userId ORDER BY discoveredAt DESC") fun observeDiscoveries(userId:Long):Flow<List<DiscoveryEntity>>
    @Query("SELECT * FROM discoveries WHERE userId=:userId AND pointId=:pointId LIMIT 1") suspend fun discovery(userId:Long,pointId:Long):DiscoveryEntity?
    @Update suspend fun updateDiscovery(discovery:DiscoveryEntity)
    @Insert(onConflict=OnConflictStrategy.IGNORE) suspend fun insertSelfie(selfie:SelfieEntity):Long
    @Query("SELECT * FROM selfies WHERE userId=:userId ORDER BY validatedAt DESC") fun observeSelfies(userId:Long):Flow<List<SelfieEntity>>
    @Query("SELECT * FROM selfies WHERE userId=:userId AND pointId=:pointId LIMIT 1") suspend fun selfie(userId:Long,pointId:Long):SelfieEntity?
    @Insert suspend fun insertActivity(activity:ActivityEntity)
    @Query("SELECT * FROM activities WHERE userId=:userId ORDER BY createdAt DESC") fun observeActivities(userId:Long):Flow<List<ActivityEntity>>
    @Query("SELECT COALESCE(SUM(points),0) FROM activities WHERE userId=:userId") suspend fun totalPoints(userId:Long):Int
    @Query("SELECT COALESCE(SUM(points),0) FROM activities WHERE userId=:userId") fun observePointsTotal(userId:Long):Flow<Int>
    @Insert suspend fun insertNotification(notification:NotificationEntity)
    @Query("SELECT * FROM notifications WHERE userId=:userId OR userId IS NULL ORDER BY createdAt DESC") fun observeNotifications(userId:Long):Flow<List<NotificationEntity>>
}
