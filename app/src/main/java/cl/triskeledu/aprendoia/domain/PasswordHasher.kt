package cl.triskeledu.aprendoia.domain
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    fun newSalt():String = ByteArray(16).also { SecureRandom().nextBytes(it) }.let { Base64.encodeToString(it,Base64.NO_WRAP) }
    fun hash(password:String,salt:String):String {
        val spec=PBEKeySpec(password.toCharArray(),Base64.decode(salt,Base64.NO_WRAP),120000,256)
        return try { Base64.encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded,Base64.NO_WRAP) }
        finally { spec.clearPassword() }
    }
    fun verify(password:String,salt:String,expected:String):Boolean =
        java.security.MessageDigest.isEqual(Base64.decode(hash(password,salt),Base64.NO_WRAP),Base64.decode(expected,Base64.NO_WRAP))
}
