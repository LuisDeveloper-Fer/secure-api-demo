"""Exercise the local Compose stack with fictional data; Python standard library only."""
import json
import time
import uuid
import urllib.request
import urllib.error

BASE = 'http://localhost:8080'

def request(route, body=None, headers=None):
    payload = None if body is None else json.dumps(body).encode()
    req = urllib.request.Request(BASE + route, data=payload, headers={'Content-Type':'application/json', **(headers or {})})
    try:
        with urllib.request.urlopen(req, timeout=5) as response:
            raw=response.read()
            return response.status, json.loads(raw) if raw else {}
    except urllib.error.HTTPError as error:
        raw=error.read()
        return error.code, json.loads(raw) if raw else {}

for _ in range(90):
    try:
        if request('/actuator/health')[0] == 200: break
    except OSError: pass
    time.sleep(2)
else: raise RuntimeError('API did not become ready')

for _ in range(30):
    try:
        with urllib.request.urlopen('http://localhost:4200',timeout=5) as response:
            assert b'<app-root>' in response.read(), 'Angular shell missing'
            break
    except OSError: time.sleep(1)
else: raise RuntimeError('Frontend did not become ready')

from urllib.parse import urlencode
for _ in range(90):
    try:
        data = urlencode({'grant_type':'client_credentials','client_id':'lab-reader','client_secret':'lab-reader-local-only'}).encode()
        token_request = urllib.request.Request('http://localhost:8180/realms/backend-lab/protocol/openid-connect/token', data=data)
        with urllib.request.urlopen(token_request,timeout=5) as response: token=json.load(response)['access_token']
        break
    except (OSError,KeyError): time.sleep(2)
else: raise RuntimeError('Keycloak did not issue a token')
headers = {'Authorization': 'Bearer ' + token}
assert request('/api/me')[0] == 401
assert request('/api/me', headers=headers)[0] == 200
assert request('/api/reports', headers=headers)[0] == 403

print('secure-api-demo: HTTP smoke passed')
