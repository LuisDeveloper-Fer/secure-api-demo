# ADR 001 — TRUST

Estado: aceptada · 2026-10-05

## Contexto
Un JWT válido no autoriza a leer cualquier recurso. Este laboratorio separa autenticación, roles y propiedad, con perfiles TLS y mTLS para estudiar la confianza entre servicios.

## Decisión
Keycloak emite tokens y Spring Security valida. Comprobar propiedad evita BOLA/IDOR incluso con un rol válido. CSRF se deshabilita porque no hay autenticación por cookies; esa decisión cambiaría con sesiones. mTLS autentica al cliente de transporte y no reemplaza la autorización JWT.

## Alternativas
Separar más microservicios o incorporar un broker agregaría despliegue y operación fuera del objetivo. Concentrar todo en el controlador dificultaría probar fallos y razonar sobre el contrato. Se elige una aplicación pequeña con API, casos de uso y adaptadores diferenciados.

## Consecuencias
Keycloak está en modo desarrollo con credenciales de demo públicas. Rate limit por instancia, no distribuido. Flujo client_credentials; no implementa login humano con PKCE. Certificados generados válidos 30 días, solo locales. TLS/mTLS se prueban con curl, no con el panel.

## Validación
HTTP con RSA/JWKS reales: roles, propiedad, token ausente, falsificado, expirado y audiencia incorrecta.
