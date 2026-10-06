# API — Secure API Demo

Base: http://localhost:8080. Content-Type: application/json.

| Método | Ruta | Contrato |
| --- | --- | --- |
| GET | `/api/me` | Identidad; 401 inválido |
| GET | `/api/accounts/{subject}` | 200 propietario; 403 otro subject |
| GET | `/api/reports` | 200 admin; 403 reader |
| GET | `/actuator/health` | Público sin detalles |
| GET | `/actuator/prometheus` | Solo admin |

## Request
```json
{}
```

## Response (campos relevantes)
```json
{
  "subject": "service-account-subject",
  "roles": [
    "reader"
  ],
  "issuer": "http://localhost:8180/realms/backend-lab"
}
```

## Errores
400 indica validación o formato inválido; 404 indica recurso inexistente. Los estados específicos se detallan en la tabla. ProblemDetail se usa para errores de negocio y validación donde aplica; autenticación puede devolver cuerpo vacío y WWW-Authenticate. Los clientes deben usar códigos, no parsear mensajes internos.

Audience=backend-api; roles=reader/admin. /api/accounts/{subject} exige coincidencia con sub incluso para admin. 401 token inválido; 403 permiso insuficiente; 429 rate limit de instancia.

