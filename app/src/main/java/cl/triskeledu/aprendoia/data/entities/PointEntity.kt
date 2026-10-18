package cl.triskeledu.aprendoia.data.entities

import androidx.room.*

@Entity(tableName="points")
data class PointEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val title:String, val description:String, val latitude:Double, val longitude:Double, val altitude:Double?=null, val isTouristic:Boolean=true, val qrPayload:String, val videoId:Long?=null)
