package cl.triskeledu.aprendoia.domain
object AuthValidator {
    fun validate(name:String,alias:String,email:String,phone:String,password:String):Map<String,String> = buildMap {
        if(name.trim().length<2) put("name","Ingresa un nombre válido")
        if(!alias.matches(Regex("[A-Za-z0-9_]{3,20}"))) put("alias","Alias de 3 a 20 caracteres")
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) put("email","Correo inválido")

        if (!phone.matches(Regex("""\+?[0-9]{8,15}"""))) {
            put("phone", "Teléfono inválido")
        }

        if(password.length<8) put("password","Mínimo 8 caracteres")
    }
}
