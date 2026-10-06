# Validación

Trabajo realizado: 2026-10-05–2026-10-06 (America/Lima).

- Backend: Java 21.0.6, Maven 3.9.9. Pruebas locales ejecutadas: 2, sin fallos; confirmadas también en GitHub Actions.
- Angular: build de producción con comprobación estricta de TypeScript/templates.
- Docker Compose: configuración validada con docker compose config --quiet.
- Docker Engine local no disponible. **Docker Compose sí se ejecutó y pasó en GitHub Actions**, con API, Angular y servicios auxiliares reales.
- Código verificado: `3620d69`. [Ejecución exitosa: backend + frontend + stack](https://github.com/LuisDeveloper-Fer/secure-api-demo/actions/runs/37416765982).
- El commit posterior de capturas/documentación no modifica el código validado.


## Reproducir

```bash
mvn clean package
cd frontend && npm ci && npm run build
cd ..
docker compose up -d --build
python scripts/smoke.py
docker compose down
```

Se usan datos ficticios. El smoke test crea registros nuevos; no debe ejecutarse contra entornos ajenos al laboratorio.

## mTLS local

Certificados generados con keytool y perfil mtls ejecutado sobre Java 21: un cliente sin certificado fue rechazado en el handshake; un cliente con certificado firmado por la CA de laboratorio recibió HTTP 200. La confianza en el certificado de servidor se verificó explícitamente, sin omitir la validación TLS. Los certificados están excluidos de Git.
