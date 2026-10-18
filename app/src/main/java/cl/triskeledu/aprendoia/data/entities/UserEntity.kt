package cl.triskeledu.aprendoia.data.entities

import androidx.room.*

@Entity(tableName="users", indices=[Index(value=["alias"], unique=true), Index(value=["email"], unique=true)])
data class UserEntity(@PrimaryKey(autoGenerate=true) val id:Long=0, val name:String, val alias:String, val email:String?=null, val phone:String?=null, val address:String?=null, val avatar:String="default", val role:String="CLIENT", val passwordHash:String, val passwordSalt:String, val notificationsEnabled:Boolean=true)
