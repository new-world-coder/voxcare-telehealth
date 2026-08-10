# Feature Backlog — Start Here

Prioritized work after the GetDial strategy scaffold. Details: [`ASSESSMENT.md`](./ASSESSMENT.md), [`GETDIAL_INTEGRATION_STRATEGY.md`](./GETDIAL_INTEGRATION_STRATEGY.md).

## P0 — Unblock dialer booking

1. Implement **patient-service** CRUD + `GET /patients/by-phone/{e164}`
2. Implement **provider-service** list + specialty filters
3. Expose **availability** open-slot APIs (table already exists in SQL)
4. Align appointment create DTO with patient portal fields
5. Staff portal: “Call to book / remind” → `POST /api/voice/calls`

## P1 — Live GetDial

1. Set `VOICE_PROVIDER=dial` + Dial credentials in staging
2. Register webhook `POST /api/voice/webhooks/dial`
3. SMS fallback on no-answer
4. Reminder job from `/appointments/reminders`

## P2 — Platform

1. Multi-tenant (`tenant_id`, JWT claim, per-tenant Dial keys)
2. Service-account auth for voice/tool callbacks
3. RCM only if product requires it (separate bounded context; current RCM ≈ 0%)

## Quick local voice check (mock)

```bash
# after stack is up
curl -s http://localhost:8087/voice/providers/health | jq .
curl -s -X POST http://localhost:8087/voice/calls \
  -H 'Content-Type: application/json' \
  -d '{"to":"+15551234567","purpose":"BOOKING","patientId":1}'
```
