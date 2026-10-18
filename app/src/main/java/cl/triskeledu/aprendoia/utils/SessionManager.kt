package cl.triskeledu.aprendoia.utils
class SessionManager { var activeUserId:Long?=null; private set
    fun activate(id:Long){ activeUserId=id }
    fun logout(){ activeUserId=null }
}
