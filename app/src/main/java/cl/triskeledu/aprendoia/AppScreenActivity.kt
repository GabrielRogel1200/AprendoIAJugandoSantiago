package cl.triskeledu.aprendoia

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import android.graphics.Bitmap.Config
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cl.triskeledu.aprendoia.data.database.AppDatabase
import cl.triskeledu.aprendoia.data.entities.*
import cl.triskeledu.aprendoia.data.repository.*
import cl.triskeledu.aprendoia.domain.AuthValidator
import cl.triskeledu.aprendoia.domain.FormValidators
import cl.triskeledu.aprendoia.domain.PasswordHasher
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver

/** Navegador académico de las 15 pantallas. Los datos se consultan en Room, por perfil. */
class AppScreenActivity: AppCompatActivity() {
    private lateinit var db:AppDatabase
    private lateinit var actions:AppActions
    private lateinit var repo:GameRepository
    private lateinit var page:LinearLayout
    private var userId:Long=0
    private var pointId:Long=0
    private var screen="dashboard"
    private val blue=Color.rgb(17,46,83)
    private val teal=Color.rgb(0,148,146)
    private val purple=Color.rgb(83,70,172)
    private val currentScore get() = intent.getIntExtra("score",0)
    private val scanner=registerForActivityResult(ScanContract()) { result ->
        val value=result.contents
        if(value.isNullOrBlank()) toast("Escaneo cancelado")
        else lifecycleScope.launch {
            when(val result=repo.scanQr(userId,value.trim())) {
                is ScanResult.Awarded -> go("qrresult",result.point.id, result.points)
                is ScanResult.AlreadyFound -> go("qrresult",result.point.id,0)
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
        // OpenStreetMap requiere identificar la app ante el servidor de mosaicos.
        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm_map", MODE_PRIVATE))
        render()
    }
    private fun go(target:String,point:Long=0,score:Int=0) {
        startActivity(Intent(this,AppScreenActivity::class.java).putExtra("screen",target)
            .putExtra("userId",userId).putExtra("pointId",point).putExtra("score",score))
    }
    private fun toast(message:String)=Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    private fun rounded(color:Int,stroke:Int?=null,radius:Float=20f):GradientDrawable =
        GradientDrawable().apply {
            cornerRadius=radius*resources.displayMetrics.density
            setColor(color)
            if(stroke!=null) setStroke((1.3f*resources.displayMetrics.density).toInt(),stroke)
        }
    private fun setup(title:String,subtitle:String="") {
        val scroll=ScrollView(this).apply {setBackgroundColor(Color.rgb(244,248,254));isFillViewport=true}
        page=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL;setPadding(22,24,22,32) }
        scroll.addView(page);setContentView(scroll)
        page.alpha=0f;page.translationY=24f * resources.displayMetrics.density
        page.animate().alpha(1f).translationY(0f).setDuration(330L)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
        if(screen!="login" && screen!="register") button("← Volver") {finish()}
        val hero=LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            setPadding(22,22,22,22)
            background=GradientDrawable(GradientDrawable.Orientation.TL_BR,intArrayOf(blue,purple,teal)).apply {
                cornerRadius=24f*resources.displayMetrics.density
            }
            elevation=5f*resources.displayMetrics.density
        }
        val header=TextView(this).apply {text=title;textSize=26f;setTextColor(Color.WHITE);setTypeface(null,Typeface.BOLD)}
        hero.addView(header)
        if(subtitle.isNotBlank()) hero.addView(TextView(this).apply {
            text=subtitle;textSize=14f;setTextColor(Color.rgb(230,245,255));setPadding(0,10,0,0)
        })
        page.addView(hero,LinearLayout.LayoutParams(-1,-2).apply {bottomMargin=16.dp()})
    }
    private fun Int.dp()=(this*resources.displayMetrics.density).toInt()
    private fun text(value:String,size:Int=16,bold:Boolean=false):TextView {
        val t=TextView(this).apply {
            text=value;textSize=size.toFloat();setTextColor(blue)
            setPadding(12.dp(),12.dp(),12.dp(),12.dp())
            if(bold)setTypeface(null,Typeface.BOLD)
        }
        page.addView(t);return t
    }
    private fun button(label:String,click:()->Unit) {
        val b=Button(this).apply {
            text=label;isAllCaps=false;contentDescription=label
            textSize=15f;setTextColor(Color.WHITE);setTypeface(null,Typeface.BOLD)
            background=rounded(if(label.startsWith("←"))purple else teal)
            elevation=3f*resources.displayMetrics.density
            minHeight=52.dp()
            setOnClickListener {
                animate().scaleX(.97f).scaleY(.97f).setDuration(90L).withEndAction {
                    animate().scaleX(1f).scaleY(1f).setDuration(130L).start()
                }.start()
                click()
            }
        }
        page.addView(b,LinearLayout.LayoutParams(-1,54.dp()).apply {bottomMargin=10.dp()})
    }
    private fun field(hint:String,initial:String="",password:Boolean=false):EditText {
        val e=EditText(this).apply {
            this.hint=hint
            contentDescription=hint
            setText(initial)
            textSize=16f
            isSingleLine=true
            inputType=when {
                password -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint.contains("Correo",ignoreCase=true) -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                hint.contains("Teléfono",ignoreCase=true) -> InputType.TYPE_CLASS_PHONE
                hint.contains("Latitud",ignoreCase=true) || hint.contains("Longitud",ignoreCase=true) ->
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                else -> InputType.TYPE_CLASS_TEXT
            }
        }
        page.addView(e)
        return e
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
    /** Mapa de calles reales basado en OpenStreetMap, integrado en esta actividad. */
    private fun newStreetMap(center:GeoPoint=GeoPoint(-33.4400,-70.6490),zoom:Double=15.0):MapView =
        MapView(this).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(zoom)
            controller.setCenter(center)
            minZoomLevel=3.0
            maxZoomLevel=20.0
            // El mapa debe recibir los gestos, aunque se encuentre en un ScrollView.
            setOnTouchListener { v, event ->
                if(event.actionMasked==android.view.MotionEvent.ACTION_DOWN || event.actionMasked==android.view.MotionEvent.ACTION_MOVE)
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                false
            }
        }

    private fun map() {
        setup("Explora Santiago", "Mapa interactivo con calles y lugares de aprendizaje")
        text("🗺️ Desliza para moverte y junta o separa dos dedos para cambiar el zoom.",14)
        button("📍 Consultar mi ubicación GPS") {showLocation()}
        if(userId>0) button("📷 Escanear código QR desde el mapa") {go("scan")}
        lifecycleScope.launch {
            val points=db.gameDao().observePoints().first()
            val mapView=newStreetMap()
            page.addView(mapView,LinearLayout.LayoutParams(-1,500.dp()).apply {bottomMargin=14.dp()})
            if(points.isNotEmpty()) {
                points.forEach {p ->
                    val marker=Marker(mapView).apply {
                        position=GeoPoint(p.latitude,p.longitude)
                        title=p.title
                        snippet=p.description.take(110)
                        setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { m,_ ->
                            AlertDialog.Builder(this@AppScreenActivity)
                                .setTitle(p.title).setMessage(p.description)
                                .setNegativeButton("Seguir explorando",null)
                                .setPositiveButton("Ver lugar") {_,_ ->go("point",p.id)}.show()
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
                mapView.controller.setCenter(GeoPoint(points.map{it.latitude}.average(),points.map{it.longitude}.average()))
            }
            text("📌 Lugares disponibles",19,true)
            points.forEach {p-> button("📍 ${p.title}") {go("point",p.id)} }
            if(points.isEmpty()) text("Todavía no hay lugares registrados.")
            text("© OpenStreetMap colaboradores · Requiere conexión para descargar nuevas calles.",12)
        }
    }

    /** Selección visual: el usuario toca el mapa; los valores geográficos quedan ocultos. */
    private fun choosePlaceOnMap(onChosen:(GeoPoint)->Unit) {
        val container=LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            setPadding(12.dp(),10.dp(),12.dp(),12.dp())
        }
        val instruction=TextView(this).apply {
            text="Mueve el mapa hasta encontrar el sitio y tócalo para marcarlo. Puedes ampliar con dos dedos."
            textSize=15f;setPadding(8.dp(),4.dp(),8.dp(),12.dp())
        }
        container.addView(instruction)
        val mapView=newStreetMap(GeoPoint(-33.4400,-70.6490),15.5)
        container.addView(mapView,LinearLayout.LayoutParams(-1,430.dp()))
        val selectionLabel=TextView(this).apply {
            text="Todavía no has elegido un lugar";textSize=15f
            setPadding(8.dp(),12.dp(),8.dp(),6.dp())
        }
        container.addView(selectionLabel)
        var selected:GeoPoint?=null
        var marker:Marker?=null
        val dialog=AlertDialog.Builder(this)
            .setTitle("Selecciona un punto en Santiago")
            .setView(container)
            .setNegativeButton("Cancelar",null)
            .setPositiveButton("Usar este lugar",null)
            .create()
        mapView.overlays.add(MapEventsOverlay(object:MapEventsReceiver {
            override fun singleTapConfirmedHelper(p:GeoPoint):Boolean {
                selected=p
                marker?.let {mapView.overlays.remove(it)}
                marker=Marker(mapView).apply {
                    position=p;title="Lugar seleccionado"
                    setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(marker)
                selectionLabel.text="✅ Punto seleccionado. Presiona «Usar este lugar» para confirmar."
                mapView.invalidate()
                return true
            }
            override fun longPressHelper(p:GeoPoint):Boolean=singleTapConfirmedHelper(p)
        }))
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val place=selected
                if(place==null) Toast.makeText(this,"Primero toca un sitio del mapa",Toast.LENGTH_SHORT).show()
                else {onChosen(place);dialog.dismiss()}
            }
        }
        dialog.setOnDismissListener {mapView.onPause();mapView.onDetach()}
        dialog.show()
        mapView.onResume()
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
            text("📍 Ubicación marcada en el mapa interactivo")
            val found=db.gameDao().discovery(userId,p.id)
            text(if(found==null)"🔒 QR pendiente · escanéalo para obtener puntos" else "✅ QR ya descubierto: las próximas lecturas otorgan 0 puntos")
            if(p.isTouristic) text("⭐ Hito turístico: bonificación de selfie sujeta a validación")
            button("🗺️ Volver al mapa de la aplicación") {go("map")}
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
                    is ScanResult.AlreadyFound -> go("qrresult",r.point.id,0)
                    ScanResult.InvalidQr -> toast("Código no registrado")
                    ScanResult.Unauthorized -> toast("Acceso no autorizado")
                }
            }
        }
    }
    private fun qrResult() {
        val score=intent.getIntExtra("score",0)
        setup(if(score>0) "🎉 ¡Lugar descubierto!" else "✅ QR ya registrado",if(score>0) "¡Sumaste puntos a tu aventura!" else "Este código ya fue escaneado por tu perfil. No se suman puntos nuevos.")
        val pointsLabel=text("+$score puntos",27,true)
        pointsLabel.scaleX=0.75f;pointsLabel.scaleY=0.75f
        pointsLabel.animate().scaleX(1f).scaleY(1f).setDuration(420L)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
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
                val errors=FormValidators.profile(name.text.toString(),email.text.toString(),phone.text.toString())
                name.error=errors["name"];email.error=errors["email"];phone.error=errors["phone"]
                if(errors.isNotEmpty()) {toast("Revisa los campos marcados");return@launch}
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
        var chosen:GeoPoint?=null
        val locationInfo=text("📍 Elige una ubicación en el mapa antes de guardar.",14)
        button("🗺️ Elegir ubicación en el mapa") {choosePlaceOnMap {place ->
            chosen=place;locationInfo.text="✅ Ubicación seleccionada en el mapa"
        }}
        val qr=field("Contenido QR único")
        button("Guardar punto") {lifecycleScope.launch {
            val place=chosen
            if(place==null){toast("Selecciona una ubicación en el mapa");return@launch}
            val errors=FormValidators.point(title.text.toString(),place.latitude.toString(),place.longitude.toString(),qr.text.toString())
            title.error=errors["title"];qr.error=errors["qr"]
            if(errors.isNotEmpty()){toast("Revisa los campos marcados");return@launch}
            actions.savePoint(PointEntity(title=title.text.toString(),description=desc.text.toString(),latitude=place.latitude,longitude=place.longitude,qrPayload=qr.text.toString()))
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
        text("🎫 Generador de códigos QR para lugares",20,true)
        text("Crea un lugar para asociarlo a un QR único. Compártelo o muéstralo a los jugadores; cada perfil podrá ganar puntos solo en el primer escaneo.",14)
        val placeTitle=field("Nombre del nuevo lugar")
        val placeDescription=field("Descripción educativa del lugar")
        var chosen:GeoPoint?=null
        val locationInfo=text("📍 Aún no has seleccionado el lugar en el mapa.",14)
        button("🗺️ Seleccionar lugar en el mapa") {choosePlaceOnMap {place ->
            chosen=place;locationInfo.text="✅ Ubicación seleccionada; ya puedes crear el QR."
        }}
        button("➕ Crear lugar y generar QR") {lifecycleScope.launch {
            val titleValue=placeTitle.text.toString().trim()
            val place=chosen
            if(titleValue.length<3){placeTitle.error="Escribe al menos 3 caracteres";return@launch}
            if(place==null){toast("Primero selecciona un punto en el mapa");return@launch}
            val payload="TRISKEL-"+java.util.UUID.randomUUID().toString().uppercase(Locale.ROOT)
            db.gameDao().insertPoint(PointEntity(title=titleValue,
                description=placeDescription.text.toString().trim().ifBlank {"Punto de aprendizaje creado por el equipo de contenidos. Descubre sus alrededores y registra tu visita mediante el QR."},
                latitude=place.latitude,longitude=place.longitude,qrPayload=payload))
            toast("Lugar creado con QR exclusivo")
            showQrDialog(titleValue,payload)
        }}
        text("📍 Códigos de los lugares disponibles",19,true)
        lifecycleScope.launch {db.gameDao().observePoints().first().forEach {p ->
            button("🔳 Generar / mostrar QR: ${p.title}") {showQrDialog(p.title,p.qrPayload)}
        }}
        text("Nueva cápsula",20,true)
        val title=field("Título");val topic=field("Tema de IA");val author=field("Autor")
        val uri=field("URL de video MP4 o HTTPS");val intro=field("Descripción")
        button("Guardar cápsula") {lifecycleScope.launch {
            val errors=FormValidators.video(title.text.toString(),topic.text.toString(),uri.text.toString())
            title.error=errors["title"];topic.error=errors["topic"];uri.error=errors["uri"]
            if(errors.isNotEmpty()){toast("Revisa los campos marcados");return@launch}
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
    private fun showQrDialog(title:String,payload:String) {
        try {
            val dimension=700
            val matrix=QRCodeWriter().encode(payload,BarcodeFormat.QR_CODE,dimension,dimension)
            val bitmap=Bitmap.createBitmap(dimension,dimension,Config.ARGB_8888)
            for(y in 0 until dimension) for(x in 0 until dimension)
                bitmap.setPixel(x,y,if(matrix[x,y])Color.BLACK else Color.WHITE)
            val layout=LinearLayout(this).apply {orientation=LinearLayout.VERTICAL;setPadding(16.dp(),12.dp(),16.dp(),12.dp())}
            layout.addView(ImageView(this).apply {
                setImageBitmap(bitmap);contentDescription="QR único del lugar $title"
                adjustViewBounds=true
            },LinearLayout.LayoutParams(-1,300.dp()))
            layout.addView(TextView(this).apply {text=payload;textSize=12f;setTextIsSelectable(true);gravity=Gravity.CENTER;setPadding(8.dp(),12.dp(),8.dp(),8.dp())})
            AlertDialog.Builder(this).setTitle("QR de $title").setView(layout)
                .setPositiveButton("Cerrar",null)
                .setNeutralButton("Compartir código") { _,_ ->
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type="text/plain";putExtra(Intent.EXTRA_TEXT,"Lugar: $title\nCódigo QR: $payload")
                    },"Compartir código"))
                }.show()
        } catch(e:Exception) {toast("No se pudo generar el QR: ${e.message}")}
    }
    private fun date(millis:Long)=SimpleDateFormat("dd/MM/yyyy HH:mm",Locale("es","CL")).format(Date(millis))
}
