# Aprendo IA Jugando en Santiago — versión ampliada

Proyecto Android Studio Kotlin, Room y MVVM, basado en el proyecto de Fase 1 proporcionado.

## Abrir
1. Abrir **la carpeta AprendoIAJugando** (la que contiene `settings.gradle.kts`) en Android Studio.
2. Configurar **Gradle JDK 17 o 21**, sincronizar Gradle y ejecutar en emulador API 35/36/37.
3. Requiere conexión a Internet para descargar dependencias Gradle, reproducir el video de ejemplo y abrir mapas externos.
4. `MainActivity` crea las cuentas **Admin / 123** y **GenCont / 123** al iniciar, y cuatro puntos turísticos de prueba.
5. Para registrar clientes usar contraseña de **8 caracteres o más**.

## Pantallas
1 Bienvenida; 2 Registro; 3 Selección; 4 Dashboard; 5 Mapa/listado georreferenciado; 6 Detalle de punto; 7 Escáner QR; 8 Resultado; 9 Video; 10 Selfie; 11 Colección; 12 Progreso; 13 Historial; 14 Notificaciones; 15 Perfil. También paneles administrativos y gestión de contenido.

## Pruebas de QR
`TRISKEL-PLAZA-ARMAS`, `TRISKEL-SANTA-LUCIA`, `TRISKEL-PARQUE-FORESTAL`, `TRISKEL-LA-MONEDA`.
Los QR otorgan 10 puntos **una sola vez por jugador**; el límite de puntos es 10.000.

## Limitaciones y pendientes de evaluación
- **Mapa:** se muestra un listado de coordenadas Room y se abre un mapa instalado por intent; aún no es un mapa interactivo integrado con marcadores.
- **Selfie:** se captura y guarda una imagen, pero no se realiza validación automatizada de cara + QR; por ello **no se conceden automáticamente los 100 puntos**. La función `awardVerifiedSelfie` del repositorio solo debe llamarse después de implementar una validación confiable.
- **Video demo:** URL pública de prueba de reproducción, no corresponde a una cápsula real de IA. Reemplazar por contenido autorizado del equipo.
- **Notificaciones:** bandeja local Room; **push remotas** requieren infraestructura de mensajería y no están implementadas.
- **Administrador:** alta de puntos, vista de usuarios y eliminación básica de clientes; faltan formularios de edición de puntos, edición avanzada de perfiles y generación gráfica de QR.
- **Creador:** alta y cambio de estado de videos; falta vincular videos a puntos desde la interfaz y edición completa.
- **Diseño:** interfaces funcionales con componentes Android básicos; no son reproducciones exactas de las maquetas del PDF.
- **Sin compilación verificada en este entorno:** ejecutar `Build > Make Project` y revisar posibles incompatibilidades del emulador o dependencias.
- **GitHub/Trello:** debes crear tus commits, asignar tareas y completar nombres del equipo en la entrega. No se puede fabricar evidencia histórica de colaboración.

## Arquitectura
`data/entities` define tablas Room, `data/dao` define consultas, `data/repository` contiene lógica de negocio, `viewmodel` y `ui` contienen ViewModels de la fase original. `AppScreenActivity` implementa las interfaces de esta versión; para una evaluación MVVM estricta, conviene migrar la lógica de cada pantalla a ViewModels independientes.

## Privacidad y seguridad
Se usan datos ficticios; no introducir datos personales reales en las pruebas. El inicio de sesión compara hashes PBKDF2. Las cuentas académicas con contraseña `123` no son adecuadas para producción.
