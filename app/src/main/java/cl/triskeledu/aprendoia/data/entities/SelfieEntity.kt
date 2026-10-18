package cl.triskeledu.aprendoia.data.entities

import androidx.room.*

@Entity(tableName="selfies", indices=[Index(value=["userId","pointId"],unique=true)])
data class SelfieEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val userId:Long, val pointId:Long, val photoUri:String, val validatedAt:Long=System.currentTimeMillis())
