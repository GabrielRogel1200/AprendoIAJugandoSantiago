package cl.triskeledu.aprendoia.data.entities

import androidx.room.*

@Entity(tableName="videos")
data class VideoEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val title:String, val topic:String, val author:String, val videoUri:String, val introduction:String, val durationSeconds:Int=0, val approved:Boolean=false)
