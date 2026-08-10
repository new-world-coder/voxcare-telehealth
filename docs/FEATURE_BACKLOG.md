# Feature Backlog — Start Here

Prioritized work after the GetDial strategy scaffold. Details: [`ASSESSMENT.md`](./ASSESSMENT.md), [`GETDIAL_INTEGRATION_STRATEGY.md`](./GETDIAL_INTEGRATION_STRATEGY.md).

**Rename:** completed to **VoxCare** — see [`RENAME_PROPOSAL.md`](./RENAME_PROPOSAL.md). GitHub repo rename to `voxcare-telehealth` still needs owner action in GitHub Settings (API 403).

## P0 — Unblock dialer booking

1. [x] Implement **patient-service** CRUD + `GET /patients/by-phone/{phone}`
2. [x] Implement **provider-service** list + specialty filters
3. [x] Expose **availability** open-slot APIs (`GET /providers/slots/open`)
4. [x] Align appointment create DTO with portal `startTime`/`endTime`
5. [x] Staff portal: “AI Call to Book / Remind” → `POST /api/voice/calls`

## P1 — Live GetDial

1. [x] Env support for `VOICE_PROVIDER=dial` + Dial credentials
2. [x] Webhook `POST /api/voice/webhooks/dial` (+ optional `X-Dial-Webhook-Secret`)
3. [x] SMS fallback on no-answer / busy / failed
4. [x] Reminder enqueue `POST /api/voice/reminders/enqueue`
5. [x] Inject open slots into Dial `outboundInstruction` automatically
6. [x] Explicit retry `POST /api/voice/calls/{id}/retry`

## P2 — Platform

1. Multi-tenant (`tenant_id`, JWT claim, per-tenant Dial keys)
2. Service-account auth for voice/tool callbacks
3. Scheduled retry worker honoring `VOICE_RETRY_DELAY_MINUTES`
4. RCM only if product requires it (separate bounded context; current RCM ≈ 0%)

## Quick local voice check (mock)

```bash
curl -s http://localhost:8087/voice/providers/health | jq .
curl -s -X POST http://localhost:8087/voice/calls \
  -H 'Content-Type: application/json' \
  -d '{"patientId":1,"purpose":"BOOKING"}'
curl -s -X POST http://localhost:8087/voice/reminders/enqueue | jq .
```
