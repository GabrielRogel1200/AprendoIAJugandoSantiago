# Plan de verificación de Evaluación Parcial 4

**Estado:** plantilla de pruebas manuales; marcar solo resultados realmente comprobados. No representa evidencia de ejecución.

| ID | Área | Procedimiento | Resultado esperado | Estado (pendiente/OK/error) | Evidencia |
|---|---|---|---|---|---|
| A01 | Instalación | Compilar `assembleDebug` | BUILD SUCCESSFUL | Pendiente | |
| A02 | Registro | Registrar usuario válido e inválido | Errores por campo y registro persistente | Pendiente | |
| A03 | QR | Escanear dos veces el mismo QR con el mismo jugador | Primera vez puntos; segunda +0 | Pendiente | |
| A04 | Mapa | Deslizar, hacer zoom y seleccionar marcador | Mapa interactivo y detalle del lugar | Pendiente | |
| A05 | API externa | Abrir Clima de Santiago con red | Datos reales o error controlado | Pendiente | |
| A06 | API REST propia | GET /api/v1/points en servidor iniciado | HTTP 200 y JSON de lugares | Pendiente | |
| A07 | CRUD remoto | Crear, modificar, eliminar lugar de prueba | Cambios visibles en backend | Pendiente | |
| A08 | Seguridad | Backend detenido o sin red | Error controlado, sin cierre inesperado | Pendiente | |
| A09 | Tests Android | Ejecutar `testDebugUnitTest` | BUILD SUCCESSFUL y número de tests | Pendiente | |
| A10 | Tests backend | Ejecutar `mvn clean verify` | BUILD SUCCESS y reporte JaCoCo | Pendiente | |
| A11 | Cobertura | Leer reportes de todas las capas de lógica | Evidencia medible del mínimo 80% exigido | Pendiente | |
| A12 | Release | Generar APK firmado, instalar y abrirlo | APK release firmado funcionando | Pendiente | |
| A13 | GitHub | Revisar diff, commits y README | Código real y documentación | Pendiente | |
| A14 | Trello | Revisar tablero y asignación real | Evidencias de planificación | Pendiente | |

### Nota sobre cobertura
El reporte de JaCoCo de Spring Boot (`backend/target/site/jacoco/index.html`) mide el backend, **no toda la lógica de Android**. Para afirmar el 80% global, el equipo debe medir y justificar el alcance que pide el profesor; una compilación exitosa no equivale a cobertura.
