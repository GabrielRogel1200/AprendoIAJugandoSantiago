# Aprendo IA Jugando en Santiago

Aplicación Android educativa en **Kotlin**, con **Room**, componentes **MVVM**, códigos QR y ubicación del dispositivo. Proyecto académico para **DSY1105 — Evaluaciones Parciales 2 y 4**.

## Responsable
- Estudiante: Gabriel Rogel.
- Modalidad: desarrollo individual informado por el estudiante. Confirmar con el docente si la evaluación exige parejas.

## Requisitos para ejecutar
1. Instalar Android Studio y Android SDK compatibles con el proyecto.
2. Abrir la carpeta raíz `AprendoIAJugando` (contiene `settings.gradle.kts`).
3. Sincronizar Gradle con acceso a Internet y compilar mediante **Build > Make Project**.
4. Ejecutar en un emulador o teléfono Android compatible, autorizando los permisos que se soliciten.
5. Para generar un APK de prueba, usar **Build > Build APK(s)** y buscar `app/build/outputs/apk/debug/app-debug.apk`.

## Funcionalidades
- Bienvenida, selección de perfiles, registro e inicio de sesión.
- Panel de jugador, administrador y creador de contenido.
- Mapa interactivo dentro de la app basado en OpenStreetMap/osmdroid, con calles, marcadores, desplazamiento y zoom.
- Generador visual de códigos QR desde el rol Creador, escáner QR, colección, puntaje, progreso e historial. Cada perfil recibe puntos solo la primera vez que registra cada lugar; repetir un QR muestra +0 puntos.
- Cámara para selfie, reproducción de video, perfil y notificaciones locales.
- Persistencia de usuarios, puntos y descubrimientos con Room.
- Validaciones de registro, perfil, puntos y cápsulas de video con mensajes por campo.
- Diseño actualizado con encabezados degradados, botones redondeados, transiciones y animación de puntaje QR.
- Mejoras de accesibilidad: descripciones y teclados adecuados a los campos.

## Datos de demostración
Las cuentas iniciales son **Admin / 123** y **GenCont / 123**, únicamente para fines académicos. Los jugadores nuevos requieren contraseña de 8 caracteres o más.

Códigos QR de demostración:
- `TRISKEL-PLAZA-ARMAS`
- `TRISKEL-SANTA-LUCIA`
- `TRISKEL-PARQUE-FORESTAL`
- `TRISKEL-LA-MONEDA`

Cada QR válido otorga puntos solo una vez por jugador.

## Arquitectura
- `data/entities`: entidades de Room.
- `data/dao`: consultas de la base de datos.
- `data/database`: configuración y datos iniciales.
- `data/repository`: operaciones y reglas de negocio.
- `domain`: validadores y manejo de contraseñas.
- `viewmodel`: ViewModels de la aplicación.
- `AppScreenActivity.kt`: implementación actual de diversas pantallas. **Pendiente:** trasladar toda la lógica de interacción a ViewModels separados para un MVVM completo.

## Novedades de la actualización: mapa, códigos QR y diseño
1. Inicia como **GenCont / 123**; en Gestión de contenidos crea un lugar con nombre, descripción y coordenadas válidas. Al guardar aparece su QR.
2. También puedes tocar «Generar / mostrar QR» junto a los puntos ya registrados. El contenido QR permanece asociado a su lugar (no cambies el payload al compartirlo).
3. En otro teléfono o mostrando el QR impreso, inicia como un **jugador**, abre **Mapa de Santiago → Escanear código QR desde el mapa** y escanea el código.
4. La primera lectura válida de ese punto otorga hasta 10 puntos. Si escaneas **el mismo código con el mismo perfil** otra vez, se muestra **+0 puntos**. Otro jugador puede obtener sus propios puntos una vez.
5. Las descripciones ampliadas de los cuatro puntos de demostración se actualizarán al iniciar la app sin borrar los descubrimientos existentes. Las descripciones personalizadas no se sobrescriben.
6. El QR generado se muestra dentro de la app. La opción «Compartir código» comparte el **texto**, no una imagen PNG; para imprimirlo utiliza una captura de la imagen del QR.

## Pruebas manuales sugeridas
1. Registrar un jugador con campos vacíos y verificar errores por campo.
2. Registrar un jugador válido, cerrar la aplicación y comprobar que el perfil persiste.
3. Iniciar sesión con los perfiles iniciales y con un jugador nuevo.
4. Validar campos erróneos en perfil, creación de puntos y videos.
5. Abrir varias pantallas y comprobar la animación de entrada.
6. Escanear un QR válido dos veces con el mismo perfil y comprobar +10 (o hasta el límite) primero, +0 después. Comprobar que diferentes usuarios mantienen sus avances separados.
7. Probar permisos de cámara y ubicación, incluidos los casos de rechazo.
8. Comprobar funcionamiento en el teléfono físico.

## Limitaciones conocidas
- El mapa utiliza OpenStreetMap dentro de la aplicación, con calles y marcadores; no incluye navegación GPS paso a paso.
- La selfie se captura, pero **no** se valida automáticamente rostro y QR ni se entregan los 100 puntos extra sin verificación.
- Las notificaciones son locales, no push remotas.
- La gestión administrativa y de contenidos es parcial. El creador genera QR para los puntos guardados localmente; no existe sincronización entre celulares sin servidor/backend.
- Las cuentas iniciales con contraseña `123` son para demostración, no producción.
- No se ha certificado la compilación de las mejoras en todos los dispositivos; probar en Android Studio antes de cada commit.

## Control de versiones y entrega
Mantener commits auténticos que describan cambios realmente implementados y comprobados. No alterar el repositorio después del plazo de entrega indicado por el docente. Completar la planificación en Trello o equivalente con tareas y evidencias reales.


## Mapa interactivo (OpenStreetMap)

- Mapa real dentro de la aplicación, con calles, gestos de desplazamiento y zoom. Usa la librería `org.osmdroid:osmdroid-android:6.1.20`.
- El creador y el administrador eligen el lugar tocando el mapa (sin introducir coordenadas manuales); éstas se guardan internamente en Room.
- Selecciona un marcador para leer la descripción y entrar al punto turístico.
- Requiere conexión a Internet para cargar mosaicos nuevos, sujetos a disponibilidad y política de uso de OpenStreetMap. No utiliza Google Maps.
- El escaneo QR de un lugar sigue otorgando puntos solo una vez **por perfil** y conserva la protección en Room.


## Evaluación Parcial 4 — Etapa 01: API externa

Se integra [Open-Meteo](https://open-meteo.com/en/docs) mediante Retrofit 2.11.0 y Gson en `data/remote`, con una pantalla de **Clima de Santiago** desde el panel del jugador. Se muestran temperatura, humedad, viento, fecha/hora y descripción; hay un estado de error y un botón para reintentar. El endpoint usado es `GET https://api.open-meteo.com/v1/forecast` con `latitude`, `longitude`, `current` y `timezone`. Se incorporan tres pruebas unitarias iniciales para la traducción de códigos meteorológicos. No constituye el 80% de cobertura exigido. **Pendiente:** Spring Boot, Compose, pruebas adicionales y APK firmado. Consultar `GUIA_EVALUACION_4_ETAPA_01.txt`.


## Evaluación Parcial 4 — Etapa 02
Se añadió `backend/`, microservicio REST Spring Boot independiente con H2 para lugares y registro QR una sola vez por jugador. **El Android aún no está conectado al backend**; ver `backend/README.md` para instalar, probar y conocer limitaciones de seguridad.


## EP4 Etapa 03 — App conectada a Spring Boot
La vista **Lugares del servidor** consulta la API `GET /api/v1/points` con Retrofit. La cuenta Administrador puede usar una interfaz de demostración para crear, editar y eliminar lugares por POST/PUT/DELETE; la autenticación del servidor aún no está implementada. El flujo de QR del jugador sigue funcionando localmente con Room (sin sincronización automática). Dirección inicial del emulador: `http://10.0.2.2:8080/`; para teléfono físico usar IP LAN del PC. HTTP habilitado exclusivamente en `debug`. Ver `GUIA_EVALUACION_4_ETAPA_03.txt`.

## EP4 Etapa 04: migración incremental a Compose
El dashboard de jugador utiliza Jetpack Compose + Material 3. Las demás pantallas conservan la implementación Android View/XML hasta fases posteriores. Ver `GUIA_EVALUACION_4_ETAPA_04.txt`.


## EP4 Etapa 05: pruebas
Se agregaron pruebas JVM en Android (`app/src/test`) y pruebas de integración y reporte JaCoCo en backend (`backend/src/test`, `mvn clean verify`). **La cobertura mínima del 80% todavía debe comprobarse con mediciones**, especialmente para Android. Consultar `GUIA_EVALUACION_4_ETAPA_05.txt`.

## EP4 Etapa 06: firma y documentación
La guía `docs/GUIA_APK_FIRMADO_EP4.md` explica cómo generar una versión release firmada usando Android Studio y una llave privada creada por el estudiante. El archivo `.jks` y el APK de release **no están incluidos ni generados** en esta distribución.

Para la entrega, utilizar `docs/PLAN_PRUEBAS_EP4.md` y `docs/REGISTRO_ENTREGA_EP4.md`, completando solamente evidencias verificadas. Tener en cuenta que el proyecto sigue con migración parcial a Jetpack Compose y que la conexión a backend por HTTP solo se permite en debug.
