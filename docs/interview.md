# Demostración en cinco minutos

## Problema — 30 segundos
Un JWT válido no autoriza a leer cualquier recurso. Este laboratorio separa autenticación, roles y propiedad, con perfiles TLS y mTLS para estudiar la confianza entre servicios.

## Experimento — 2 minutos
Obtén un token de lab-reader: /api/me funciona y /api/reports devuelve 403. Con lab-admin el reporte funciona. Ninguno puede leer una cuenta con otro subject.

## Decisión — 1 minuto
Keycloak emite tokens y Spring Security valida. Comprobar propiedad evita BOLA/IDOR incluso con un rol válido. CSRF se deshabilita porque no hay autenticación por cookies; esa decisión cambiaría con sesiones. mTLS autentica al cliente de transporte y no reemplaza la autorización JWT.

## Discusión
- ¿Qué operación es atómica?
- ¿Qué ocurre entre confirmar una escritura y enviar una respuesta?
- ¿Qué impide agotar recursos?
- ¿Qué cambia al ejecutar dos réplicas?
- ¿Qué mide el dashboard y qué no permite concluir?

## Límites que conviene explicar
Keycloak está en modo desarrollo con credenciales de demo públicas. Rate limit por instancia, no distribuido. Flujo client_credentials; no implementa login humano con PKCE. Certificados generados válidos 30 días, solo locales. TLS/mTLS se prueban con curl, no con el panel.
