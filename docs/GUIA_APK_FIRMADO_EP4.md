# EP4 — Generación segura del APK firmado

## Antes de comenzar
1. Comprobar que el código fuente compile: desde la raíz, `./gradlew assembleDebug` (Windows PowerShell: `.\gradlew.bat assembleDebug`).
2. Ejecutar las pruebas JVM: `./gradlew testDebugUnitTest`.
3. Ejecutar el backend en `backend/` y comprobar las operaciones REST en un entorno de prueba.
4. El proyecto **no incluye un APK firmado ni una llave `.jks`**: ambos deben generarse en el equipo del estudiante. No declares este requisito cumplido antes de verificar el archivo.

## Generar la llave y el APK de release con Android Studio
1. Abrir el proyecto en Android Studio, esperar a que Gradle sincronice.
2. Menú **Build > Generate Signed App Bundle or APK...**.
3. Seleccionar **APK** y **Next**.
4. En **Key store path**, pulsar **Create new...** y crear un almacén JKS fuera del repositorio, por ejemplo en una carpeta privada de Documentos. Elegir contraseñas fuertes, alias, validez y datos requeridos. Guardar las credenciales en un lugar seguro; **no enviarlas a ChatGPT ni subirlas a GitHub**.
5. Seleccionar el nuevo almacén, alias y contraseñas, pulsar **Next**.
6. Seleccionar variante **release**, opciones de firma ofrecidas por Android Studio, y **Finish**.
7. Localizar el APK generado en `app/build/outputs/apk/release/` o en la ubicación que muestre el asistente. El nombre y ruta pueden variar.
8. Instalar el APK en un dispositivo de pruebas y abrir la app. Comprobar permisos, navegación, mapa, QR, clima y el acceso a backend disponible. La conexión HTTP por LAN puede estar **deshabilitada en release** por seguridad; documentar esta limitación sin confundirla con una prueba exitosa de conexión segura.

## Evidencia y entrega
- Captura del asistente de firma ocultando rutas sensibles/contraseñas.
- Captura de la carpeta que muestra el APK release; registrar nombre y tamaño.
- Captura de la app release instalada y ejecutándose.
- Registrar en `docs/REGISTRO_ENTREGA_EP4.md` la ubicación del APK y el estado de firma y pruebas.
- Si el docente exige expresamente entregar el APK y el JKS, confirmar con él **un medio privado** para el JKS. No publicar la clave privada o contraseñas en un repositorio GitHub público; el requisito académico y la seguridad de la firma deben conciliarse con el docente.

## Control de versiones
El `.gitignore` excluye archivos `.jks`, `.keystore` y salidas `build/`. No anular estos filtros por accidente. Un APK firmado puede adjuntarse por la plataforma institucional o un release privado/según las instrucciones del docente, sin publicar la llave privada.
