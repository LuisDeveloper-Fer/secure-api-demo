#!/usr/bin/env bash
set -euo pipefail
BASE="${BASE:-http://localhost:8080}"
TOKEN=$(curl -fsS http://localhost:8180/realms/backend-lab/protocol/openid-connect/token -d grant_type=client_credentials -d client_id=lab-reader -d client_secret=lab-reader-local-only | node -e "let s='';process.stdin.on('data',d=>s+=d).on('end',()=>console.log(JSON.parse(s).access_token))")
curl -i "$BASE/api/me" -H "Authorization: Bearer $TOKEN"
curl -i "$BASE/api/reports" -H "Authorization: Bearer $TOKEN" # 403 esperado
# Para probar admin: client_id=lab-admin, client_secret=lab-admin-local-only.
