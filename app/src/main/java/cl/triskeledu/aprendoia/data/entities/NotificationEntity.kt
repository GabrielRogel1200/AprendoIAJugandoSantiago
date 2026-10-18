package cl.triskeledu.aprendoia.data.entities

import androidx.room.*

@Entity(tableName="notifications", indices=[Index("userId")])
data class NotificationEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val userId:Long?=null, val title:String, val message:String, val pointId:Long?=null, val isRead:Boolean=false, val createdAt:Long=System.currentTimeMillis())
