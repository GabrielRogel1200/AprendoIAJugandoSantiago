package cl.triskeledu.aprendoia

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cl.triskeledu.aprendoia.data.database.AppDatabase
import cl.triskeledu.aprendoia.data.entities.*
import cl.triskeledu.aprendoia.data.repository.*
import cl.triskeledu.aprendoia.domain.AuthValidator
import cl.triskeledu.aprendoia.domain.PasswordHasher
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Navegador académico de las 15 pantallas. Los datos se consultan en Room, por perfil. */
class AppScreenActivity: AppCompatActivity() {
    private lateinit var db:AppDatabase
    private lateinit var actions:AppActions
    private lateinit var repo:GameRepository
    private lateinit var page:LinearLayout
    private var userId:Long=0
    private var pointId:Long=0
    private var screen="dashboard"
    private val blue=Color.rgb(18,58,112)
    private val scanner=registerForActivityResult(ScanContract()) { result ->
        val value=result.contents
        if(value.isNullOrBlank()) toast("Escaneo cancelado")
        else lifecycleScope.launch {
            when(val result=repo.scanQr(userId,value.trim())) {
                is ScanResult.Awarded -> go("qrresult",result.point.id, result.points)
                ScanResult.AlreadyFound -> toast("Este QR ya fue descubierto por este jugador")
                ScanResult.InvalidQr -> toast("QR no registrado en esta aplicación")
                ScanResult.Unauthorized -> toast("Solo los clientes pueden registrar QR")
            }
        }
    }
    private val camera=registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap:Bitmap? ->
        if(bitmap==null){ toast("Fotografía cancelada");return@registerForActivityResult }
        val image=MediaStore.Images.Media.insertImage(contentResolver,bitmap,"Triskeledu_${System.currentTimeMillis()}","Selfie turística (pendiente de validación)")
        if(image==null) toast("No se pudo guardar la imagen") else {
            toast("Foto guardada. La bonificación requiere validación de rostro y QR; no se asigna automáticamente.")
            showSelfie(image)
        }
    }
    private val locationPermission=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if(results.values.any { it }) showLocation() else toast("Permiso de ubicación no concedido")
    }
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        db=AppDatabase.get(this);actions=AppActions(db);repo=GameRepository(db)
        userId=intent.getLongExtra("userId",0);pointId=intent.getLongExtra("pointId",0)
        screen=intent.getStringExtra("screen")?:"dashboard"
        render()
    }
    private fun go(target:String,point:Long=0,score:Int=0) {
        startActivity(Intent(this,AppScreenActivity::class.java).putExtra("screen",target)
            .putExtra("userId",userId).putExtra("pointId",point).putExtra("score",score))
    }
    private fun toast(message:String)=Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    private fun setup(title:String,subtitle:String="") {
        val scroll=ScrollView(this).apply { setBackgroundColor(Color.rgb(234,243,255));isFillViewport=true }
        page=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL;setPadding(22,22,22,22) }
        scroll.addView(page);setContentView(scroll)
        if(screen!="login" && screen!="register") {
            button("← Volver") { finish() }
        }
        text(title,25,true)
        if(subtitle.isNotBlank()) text(subtitle,14)
    }
    private fun text(value:String,size:Int=16,bold:Boolean=false):TextView {
        val t=TextView(this).apply { text=value;textSize=size.toFloat();setTextColor(blue);setPadding(4,12,4,12);if(bold)setTypeface(null,Typeface.BOLD) }
        page.addView(t);return t
    }
    private fun button(label:String,click:()->Unit) {
        page.addView(Button(this).apply { text=label;isAllCaps=false;setOnClickListener { click() } })
    }
    private fun field(hint:String,initial:String="",password:Boolean=false):EditText {
        val e=EditText(this).apply { this.hint=hint;setText(initial);textSize=16f
            if(password) inputType=InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        };page.addView(e);return e
    }
    private fun render() {
        when(screen) {
            "register" -> register()
            "login" -> login()
            "dashboard" -> dashboard()
            "map" -> map()
            "point" -> point()
            "scan" -> scan()
            "qrresult" -> qrResult()
            "video" -> video()
            "selfie" -> selfie()
            "collection" -> collection()
            "progress" -> progress()
            "history" -> history()
            "notifications" -> notifications()
            "profile" -> profile()
            "admin" -> admin()
            "content" -> content()
            else -> dashboard()
        }
    }
    private fun register() {
        setup("Crear nuevo jugador","Tus datos y descubrimientos se guardan localmente")
        val name=field("Nombre completo");val alias=field("Alias (3 a 20 caracteres)")
        val email=field("Correo electrónico");val phone=field("Teléfono (+569...)")
        val address=field("Dirección (opcional)");val password=field("Contraseña (mínimo 8 caracteres)",password=true)
        val avatars=listOf("🤖","🧑","👩","👨")
        text("Avatar:")
        val spinner=Spinner(this).apply { adapter=ArrayAdapter(this@AppScreenActivity,android.R.layout.simple_spinner_dropdown_item,avatars) }
        page.addView(spinner)
        button("Crear jugador") {
            // La validación de negocio sigue centralizada en AuthValidator.
            // Esta pantalla solamente presenta los errores junto a cada campo.
            val inputs = mapOf(
                "name" to name,
                "alias" to alias,
                "email" to email,
                "phone" to phone,
                "password" to password
            )
            inputs.values.forEach { it.error = null }
            val errors = AuthValidator.validate(
                name.text.toString(), alias.text.toString(), email.text.toString(),
                phone.text.toString(), password.text.toString()
            )
            if (errors.isNotEmpty()) {
                errors.forEach { (key, message) -> inputs[key]?.error = message }
                inputs.entries.firstOrNull { errors.containsKey(it.key) }?.value?.requestFocus()
                toast("Revisa los campos marcados para continuar")
            } else {
                lifecycleScope.launch {
                    val result = repo.register(
                        name.text.toString(), alias.text.toString(), email.text.toString(),
                        phone.text.toString(), password.text.toString(),
                        address.text.toString(), avatars[spinner.selectedItemPosition]
                    )
                    if (result.isSuccess) {
                        toast("Jugador creado correctamente")
                        startActivity(Intent(this@AppScreenActivity, SeleccionJugadorActivity::class.java))
                        finish()
                    } else {
                        toast(result.exceptionOrNull()?.message ?: "Error al registrar")
                    }
                }
            }
        }
    }
    private fun login() {
        setup("Iniciar sesión","Introduce la contraseña de tu perfil")
        val name=text("Cargando usuario...")
        val pass=field("Contraseña",password=true)
        lifecycleScope.launch { name.text=actions.user(userId)?.let { "${it.avatar} ${it.alias}" }?:"Usuario no encontrado" }
        button("Ingresar") {
            lifecycleScope.launch {
                val u=actions.user(userId)
                if(u!=null && PasswordHasher.verify(pass.text.toString(),u.passwordSalt,u.passwordHash)) {
                    go(when(u.role){"ADMIN"->"admin";"CREATOR"->"content";else->"dashboard"})
                    finish()
                } else {pass.error="Contraseña incorrecta";toast("Credenciales incorrectas")}
            }
        }
    }
    private fun dashboard() {
        setup("Mi aventura en Santiago","Panel principal del cliente")
        lifecycleScope.launch {
            val user=actions.user(userId)?:return@launch
            if(user.role!="CLIENT"){toast("Acceso restringido a clientes");finish();return@launch}
            text("¡Hola, ${user.name}!",21,true)
            val score=db.gameDao().totalPoints(userId)
            val found=db.gameDao().observeDiscoveries(userId).first()
            text("🏆 $score puntos  ·  ${found.size} códigos QR",21,true)
            text("${found.count{it.watchedAt!=null}} videos revisados · ${found.size} lugares visitados")
        }
        button("🗺️ Mapa de Santiago") {go("map")}
        button("📷 Escanear QR") {go("scan")}
        button("📚 Mi colección") {go("collection")}
        button("🏆 Puntaje y progreso") {go("progress")}
        button("🕒 Historial") {go("history")}
        button("🔔 Notificaciones") {go("notifications")}
        button("👤 Perfil y configuración") {go("profile")}
    }
    private fun map() {
        setup("Mapa de Santiago","Puntos turísticos guardados en Room. Abre el mapa externo para ver las coordenadas.")
        button("📍 Mi ubicación GPS") {showLocation()}
        lifecycleScope.launch { db.gameDao().observePoints().first().forEach { p ->
            button("📍 ${p.title} (${p.latitude}, ${p.longitude})") {go("point",p.id)}
        } }
    }
    private fun showLocation() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            locationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION));return
        }
        try {
            val lm=getSystemService(LOCATION_SERVICE) as LocationManager
            val l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)?:lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            toast(if(l==null) "Ubicación no disponible. Activa GPS en el emulador." else "Tu ubicación aproximada: ${l.latitude}, ${l.longitude}")
        } catch(e:Exception) {toast("No se pudo consultar el GPS: ${e.message}")}
    }
    private fun point() {
        setup("Detalle del punto")
        lifecycleScope.launch {
            val p=actions.point(pointId)?:return@launch
            text(p.title,22,true);text(p.description)
            text("Latitud: ${p.latitude} · Longitud: ${p.longitude}")
            val found=db.gameDao().discovery(userId,p.id)
            text(if(found==null)"🔒 QR pendiente" else "✅ QR descubierto (+10 puntos si se otorgaron)")
            if(p.isTouristic) text("⭐ Hito turístico: bonificación de selfie sujeta a validación")
            button("Ver en mapa") {
                val uri=Uri.parse("geo:${p.latitude},${p.longitude}?q=${p.latitude},${p.longitude}(${Uri.encode(p.title)})")
                try {startActivity(Intent(Intent.ACTION_VIEW,uri))}catch(e:Exception){toast("Instala una aplicación de mapas")}
            }
            if(userId>0)button("Escanear QR") {go("scan",p.id)}
            if(found!=null && p.videoId!=null)button("Ver video") {go("video",p.id)}
            if(found!=null && p.isTouristic)button("Selfie turística") {go("selfie",p.id)}
        }
    }
    private fun scan() {
        setup("Escáner QR","Apunta la cámara a un QR registrado en la aplicación")
        button("📷 Abrir cámara QR") {
            val options=ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE).setPrompt("Escanea un QR de Triskeledu").setBeepEnabled(false).setOrientationLocked(false)
            scanner.launch(options)
        }
        text("Prueba académica: también puedes introducir el contenido exacto de un QR.")
        val input=field("Ej.: TRISKEL-PLAZA-ARMAS")
        button("Validar código de prueba") {
            lifecycleScope.launch {
                when(val r=repo.scanQr(userId,input.text.toString().trim())) {
                    is ScanResult.Awarded -> go("qrresult",r.point.id,r.points)
                    ScanResult.AlreadyFound -> toast("Ya descubierto: no se repiten los puntos")
                    ScanResult.InvalidQr -> toast("Código no registrado")
                    ScanResult.Unauthorized -> toast("Acceso no autorizado")
                }
            }
        }
    }
    private fun qrResult() {
        setup("🎉 ¡QR encontrado!")
        val score=intent.getIntExtra("score",0)
        text("+$score puntos",27,true)
        lifecycleScope.launch { actions.point(pointId)?.let { p ->
            text(p.title,20,true)
            if(p.isTouristic) text("⭐ Este punto permite una selfie turística con validación para obtener hasta 100 puntos extra")
            if(p.videoId!=null)button("▶ Ver video") {go("video",p.id)}
            if(p.isTouristic)button("📷 Tomar selfie") {go("selfie",p.id)}
            button("Volver al mapa") {go("map")}
        } }
    }
    private fun video() {
        setup("Video educativo")
        lifecycleScope.launch {
            val point=actions.point(pointId)?:return@launch
            val vid=point.videoId?.let { actions.video(it) }
            if(vid==null){text("No hay un video asociado");return@launch}
            text(vid.title,22,true);text("Tema: ${vid.topic} · Autor: ${vid.author}")
            text(vid.introduction)
            if(!vid.approved){text("Video pendiente de aprobación");return@launch}
            val player=VideoView(this@AppScreenActivity)
            player.layoutParams=LinearLayout.LayoutParams(-1,520)
            page.addView(player)
            val controller=MediaController(this@AppScreenActivity);controller.setAnchorView(player);player.setMediaController(controller)
            try {player.setVideoURI(Uri.parse(vid.videoUri));player.setOnPreparedListener { player.start() };player.setOnErrorListener { _,_,_ -> toast("No se pudo reproducir el video. Revisa la conexión y la URL.");true }}
            catch(e:Exception){toast("URL de video inválida")}
            button("Marcar como revisado") {
                lifecycleScope.launch {actions.recordVideo(userId,point.id);toast("Video registrado en tu historial")}
            }
        }
    }
    private fun selfie() {
        setup("Selfie turística","Debe aparecer el participante y el QR del punto")
        text("⚠️ La aplicación guarda la foto, pero NO valida automáticamente rostro y QR. Por seguridad no otorga los 100 puntos sin una validación real.")
        button("📷 Abrir cámara") {camera.launch(null)}
        button("Volver al punto") {go("point",pointId)}
    }
    private fun showSelfie(uri:String) { text("Fotografía guardada: $uri") }
    private fun collection() {
        setup("Mi colección","Descubrimientos exclusivos de este jugador")
        lifecycleScope.launch {
            val items=db.gameDao().observeDiscoveries(userId).first()
            if(items.isEmpty()) text("Todavía no has descubierto códigos QR")
            items.forEach { d ->
                val p=actions.point(d.pointId)
                button("📚 ${p?.title?:"Punto"} · ${date(d.discoveredAt)} ${if(d.watchedAt!=null)"✓ video visto" else ""}") {go("point",d.pointId)}
            }
        }
    }
    private fun progress() {
        setup("Mi puntaje y progreso")
        lifecycleScope.launch {
            val score=db.gameDao().totalPoints(userId)
            val found=db.gameDao().observeDiscoveries(userId).first()
            val points=db.gameDao().observePoints().first()
            val selfies=db.gameDao().observeSelfiesAll(userId).first()
            text("🏆 $score / 10.000 puntos",24,true)
            page.addView(ProgressBar(this@AppScreenActivity,null,android.R.attr.progressBarStyleHorizontal).apply {max=10000;progress=score})
            text("QR descubiertos: ${found.size} / ${points.size}")
            text("Videos revisados: ${found.count { it.watchedAt!=null }}")
            text("Selfies verificadas: ${selfies.size}")
            text("Progreso de lugares: ${if(points.isEmpty())0 else found.size*100/points.size}%")
        }
    }
    private fun history() {
        setup("Historial de actividades")
        lifecycleScope.launch {val items=db.gameDao().observeActivities(userId).first()
            if(items.isEmpty())text("Sin actividades todavía")
            items.forEach { text("${date(it.createdAt)} · ${it.kind}: ${it.description} (${it.points} puntos)") }
        }
    }
    private fun notifications() {
        setup("Notificaciones")
        lifecycleScope.launch {val items=db.gameDao().observeNotifications(userId).first()
            if(items.isEmpty())text("Sin notificaciones")
            items.forEach { item ->button("${if(item.isRead)"✓" else "●"} ${item.title}: ${item.message}") {
                lifecycleScope.launch {actions.readNotification(item);if(item.pointId!=null)go("point",item.pointId)else render()}
            } }
        }
    }
    private fun profile() {
        setup("Perfil y configuración")
        lifecycleScope.launch {val user=actions.user(userId)?:return@launch
            text("${user.avatar} ${user.name} (${user.alias})",21,true)
            val name=field("Nombre",user.name);val email=field("Correo",user.email?:"")
            val phone=field("Teléfono",user.phone?:"");val address=field("Dirección",user.address?:"")
            val notify=CheckBox(this@AppScreenActivity).apply {text="Notificaciones habilitadas";isChecked=user.notificationsEnabled};page.addView(notify)
            button("Guardar cambios") {lifecycleScope.launch {
                if(name.text.toString().trim().length<2){name.error="Nombre inválido";return@launch}
                actions.updateUser(user.copy(name=name.text.toString().trim(),email=email.text.toString().ifBlank{null},phone=phone.text.toString().ifBlank{null},address=address.text.toString(),notificationsEnabled=notify.isChecked))
                toast("Perfil actualizado")
            }}
            val pass=field("Nueva contraseña (mínimo 8 caracteres)",password=true)
            button("Cambiar contraseña") {lifecycleScope.launch {
                if(pass.text.length<8){pass.error="Mínimo 8 caracteres";return@launch}
                actions.changePassword(user,pass.text.toString());toast("Contraseña actualizada")
            }}
            button("Ver permisos de cámara y ubicación") {toast("Cámara: ${ContextCompat.checkSelfPermission(this@AppScreenActivity,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED}; ubicación: ${ContextCompat.checkSelfPermission(this@AppScreenActivity,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED}")}
            button("Cerrar sesión / cambiar jugador") {startActivity(Intent(this@AppScreenActivity,SeleccionJugadorActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish()}
        }
    }
    private fun admin() {
        setup("Panel Administrador","Gestiona perfiles, puntos turísticos y contenidos")
        button("🗺️ Ver puntos") {go("map")}
        button("🔔 Notificaciones") {go("notifications")}
        text("Crear punto turístico",20,true)
        val title=field("Nombre del lugar");val desc=field("Descripción")
        val lat=field("Latitud, ej. -33.4372");val lon=field("Longitud, ej. -70.6506")
        val qr=field("Contenido QR único")
        button("Guardar punto") {lifecycleScope.launch {
            val a=lat.text.toString().toDoubleOrNull();val b=lon.text.toString().toDoubleOrNull()
            if(title.text.isBlank()||a==null||b==null||a !in -90.0..90.0||b !in -180.0..180.0||qr.text.isBlank()) {toast("Revisa título, coordenadas y QR");return@launch}
            actions.savePoint(PointEntity(title=title.text.toString(),description=desc.text.toString(),latitude=a,longitude=b,qrPayload=qr.text.toString()))
            toast("Punto guardado");render()
        }}
        text("Usuarios registrados",20,true)
        lifecycleScope.launch {db.gameDao().observeUsers().first().forEach { u ->
            if(u.role=="CLIENT")button("Eliminar jugador: ${u.alias}") {
                AlertDialog.Builder(this@AppScreenActivity).setTitle("Eliminar ${u.alias}?")
                    .setMessage("Se eliminará el perfil. Esta acción no se puede deshacer.")
                    .setNegativeButton("Cancelar",null).setPositiveButton("Eliminar") { _,_ -> lifecycleScope.launch {
                        actions.deleteUser(u);toast("Jugador eliminado");render()
                    }}.show()
            } else text("🔒 ${u.alias} (${u.role})")
        }}
        button("Administrar videos") {go("content")}
        button("Cerrar sesión") {startActivity(Intent(this,SeleccionJugadorActivity::class.java));finish()}
    }
    private fun content() {
        setup("Gestión de contenidos","Creador de contenido / Administrador")
        text("Nueva cápsula",20,true)
        val title=field("Título");val topic=field("Tema de IA");val author=field("Autor")
        val uri=field("URL de video MP4 o HTTPS");val intro=field("Descripción")
        button("Guardar cápsula") {lifecycleScope.launch {
            if(title.text.isBlank()||topic.text.isBlank()||!uri.text.toString().startsWith("https://")) {toast("Completa título, tema y URL HTTPS");return@launch}
            actions.saveVideo(VideoEntity(title=title.text.toString(),topic=topic.text.toString(),author=author.text.toString(),videoUri=uri.text.toString(),introduction=intro.text.toString(),approved=false))
            toast("Cápsula guardada, pendiente de aprobación");render()
        }}
        text("Cápsulas existentes",20,true)
        lifecycleScope.launch {db.gameDao().observeVideos().first().forEach { v ->
            button("${v.title} · ${if(v.approved)"Aprobado" else "Pendiente"}") {
                AlertDialog.Builder(this@AppScreenActivity).setTitle(v.title).setItems(arrayOf("Aprobar / desaprobar", "Eliminar")) { _,choice ->
                    lifecycleScope.launch {
                        if(choice==0) actions.saveVideo(v.copy(approved=!v.approved)) else {
                            toast("El borrado de videos vinculados requiere revisión del administrador")
                        }
                        render()
                    }
                }.show()
            }
        }}
        button("Cerrar sesión") {startActivity(Intent(this,SeleccionJugadorActivity::class.java));finish()}
    }
    private fun date(millis:Long)=SimpleDateFormat("dd/MM/yyyy HH:mm",Locale("es","CL")).format(Date(millis))
}
