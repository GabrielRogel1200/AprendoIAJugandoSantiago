package cl.triskeledu.aprendoia.data.entities

import androidx.room.*

@Entity(tableName="discoveries", indices=[Index(value=["userId","pointId"],unique=true)])
data class DiscoveryEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val userId:Long, val pointId:Long, val videoId:Long?, val discoveredAt:Long=System.currentTimeMillis(), val watchedAt:Long?=null)
