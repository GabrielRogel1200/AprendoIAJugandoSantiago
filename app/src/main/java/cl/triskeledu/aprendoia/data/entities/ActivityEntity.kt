package cl.triskeledu.aprendoia.data.entities

import androidx.room.*

@Entity(tableName="activities", indices=[Index("userId")])
data class ActivityEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val userId:Long, val kind:String, val description:String, val points:Int=0, val createdAt:Long=System.currentTimeMillis())
