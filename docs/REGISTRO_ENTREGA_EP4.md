# Registro de entrega — Evaluación Parcial 4

- Aplicación: Aprendo IA Jugando en Santiago
- Asignatura: DSY1105
- Integrante(s): Gabriel Rogel (confirmar integrantes y modalidad con el docente)
- URL repositorio: https://github.com/GabrielRogel1200/AprendoIAJugandoSantiago
- Enlace tablero Trello: **pendiente de completar con enlace real**
- Fecha límite oficial del curso: **pendiente de confirmar en AVA**
- Commit/version final presentada: **pendiente**
- Nombre de APK firmado: **pendiente**
- Ubicación de APK para entrega: **pendiente**
- Evidencia visual de generación de JKS: **pendiente**
- Entrega código fuente AVA grupal: **pendiente**
- Activación AVA individual: **pendiente**

## Servicios
- API externa: Open-Meteo — `https://api.open-meteo.com/v1/forecast`.
- Backend propio: Spring Boot en `backend/`, puerto de prueba 8080; `GET/POST/PUT/DELETE /api/v1/points`, endpoint de descubrimientos documentado en `backend/README.md`.
- Backend de prueba: H2 local. No exponer públicamente sin autenticación, autorización y HTTPS.

## Limitaciones declaradas
- Solo el dashboard del jugador fue migrado a Jetpack Compose; otras vistas todavía usan Android Views.
- Las lecturas QR y puntos locales Room todavía no están sincronizados totalmente con el backend.
- El mapa usa OpenStreetMap/osmdroid y necesita acceso a mosaicos para mostrar calles nuevas.
- El backend de pruebas no dispone de autenticación ni control remoto de roles.
- La conexión HTTP a backend se permite únicamente en compilaciones debug; la versión release requiere HTTPS para uso remoto.
- No se afirma cobertura >=80% sin medición real de Android + backend.

## Checklist de entrega
- [ ] Android y backend compilados
- [ ] Pruebas e informe de cobertura revisados
- [ ] APK release firmado e instalado
- [ ] Capturas de evidencia guardadas
- [ ] README actualizado
- [ ] Trello con participación auténtica
- [ ] Repositorio revisado por secretos y archivos temporales
- [ ] ZIP de entrega descargado de GitHub
- [ ] AVA grupal entregado
- [ ] AVA individual activado
- [ ] **No modificar GitHub después del plazo de entrega**
