# Aprendo IA Jugando en Santiago

Aplicación Android educativa en **Kotlin**, con **Room**, componentes **MVVM**, códigos QR y ubicación del dispositivo. Proyecto académico para **DSY1105 — Evaluación Parcial 2**.

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
- Listado de puntos turísticos, coordenadas GPS y apertura de mapa externo.
- Escáner QR, colección, puntaje, progreso e historial.
- Cámara para selfie, reproducción de video, perfil y notificaciones locales.
- Persistencia de usuarios, puntos y descubrimientos con Room.
- Validaciones de registro, perfil, puntos y cápsulas de video con mensajes por campo.
- Animación de entrada de pantallas y animación de puntaje QR.
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

## Pruebas manuales sugeridas
1. Registrar un jugador con campos vacíos y verificar errores por campo.
2. Registrar un jugador válido, cerrar la aplicación y comprobar que el perfil persiste.
3. Iniciar sesión con los perfiles iniciales y con un jugador nuevo.
4. Validar campos erróneos en perfil, creación de puntos y videos.
5. Abrir varias pantallas y comprobar la animación de entrada.
6. Escanear un QR válido y comprobar que el puntaje no se duplique.
7. Probar permisos de cámara y ubicación, incluidos los casos de rechazo.
8. Comprobar funcionamiento en el teléfono físico.

## Limitaciones conocidas
- El mapa abre una aplicación externa; no incluye mapa interactivo integrado.
- La selfie se captura, pero **no** se valida automáticamente rostro y QR ni se entregan los 100 puntos extra sin verificación.
- Las notificaciones son locales, no push remotas.
- La gestión administrativa y de contenidos es parcial.
- Las cuentas iniciales con contraseña `123` son para demostración, no producción.
- No se ha certificado la compilación de las mejoras en todos los dispositivos; probar en Android Studio antes de cada commit.

## Control de versiones y entrega
Mantener commits auténticos que describan cambios realmente implementados y comprobados. No alterar el repositorio después del plazo de entrega indicado por el docente. Completar la planificación en Trello o equivalente con tareas y evidencias reales.
