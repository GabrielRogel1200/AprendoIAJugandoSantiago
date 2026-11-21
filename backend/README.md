# Aprendo IA Jugando — backend académico (Etapa 02)

Este microservicio Spring Boot expone una API REST para **lugares del mapa** y **descubrimientos QR**. Está incluido junto al proyecto Android, pero **la aplicación Android aún usa su repositorio Room local**: la conexión con el servidor será parte de la Etapa 03. No se debe anunciar como sincronización completa todavía.

## Requisitos
- JDK 17 o superior.
- Apache Maven 3.9+ (o abrir `backend/pom.xml` como proyecto Maven en IntelliJ IDEA).
- Internet la primera vez para descargar dependencias.

## Ejecutar en Windows (CMD o PowerShell)
```
cd backend
mvn test
mvn spring-boot:run
```
Desde el navegador, visitar: `http://localhost:8080/api/v1/points`
El servidor crea una base de datos H2 persistente en `backend/data/` (ignorada por Git).

## Endpoints
| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/v1/points` | Listar lugares |
| GET | `/api/v1/points/{id}` | Detalle |
| POST | `/api/v1/points` | Crear lugar |
| PUT | `/api/v1/points/{id}` | Editar lugar |
| DELETE | `/api/v1/points/{id}` | Eliminar lugar |
| POST | `/api/v1/discoveries/scan` | Registrar descubrimiento; +0 si ya existe |
| GET | `/api/v1/discoveries/player/{playerId}` | Historial de jugador |

### Ejemplo de escaneo desde PowerShell
```
$body = @{ playerId = 42; qrPayload = 'TRISKEL-PLAZA-ARMAS' } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/discoveries/scan' -Method Post -ContentType 'application/json' -Body $body
```
Repetir exactamente el mismo comando: `earnedPoints` será 0 la segunda vez. Utilizar otro `playerId` permitirá su primer descubrimiento independiente.

### Crear un lugar (PowerShell)
```
$point = @{ title='Nuevo lugar'; description='Descripción educativa'; latitude=-33.44; longitude=-70.65; qrPayload='TRISKEL-NUEVO-001'; points=10 } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/points' -Method Post -ContentType 'application/json' -Body $point
```

## Limitaciones importantes
- **Servidor de demostración en localhost, sin autenticación**. No publicarlo en Internet: cualquier persona con acceso podría modificar lugares o presentar un `playerId` ajeno. Para producción: Spring Security, autorización por rol y autenticar la identidad del jugador en el servidor.
- No hay sincronización con Room ni con el Android hasta Etapa 03.
- Los QR existentes funcionan por su payload de texto, compatible con los identificadores de demostración anteriores.
- La regla "solo una vez" está implementada en el servidor mediante restricción única por jugador/lugar y bloqueo transaccional del lugar para escaneos concurrentes.
- El test automatizado incluido prueba la regla de puntos por jugador. No equivale a cobertura del 80% solicitada por la rúbrica.
- No se incluye APK ni keystore.
