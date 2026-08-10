# Feature Backlog — Start Here

**Rename:** completed to **VoxCare**. Dial stack aligned with EstateCraft — see [`DIAL_INTEGRATION.md`](./DIAL_INTEGRATION.md).

## P0 — Unblock dialer booking

1. [x] Implement **patient-service** CRUD + `GET /patients/by-phone/{phone}`
2. [x] Implement **provider-service** list + specialty filters
3. [x] Expose **availability** open-slot APIs (`GET /providers/slots/open`)
4. [x] Align appointment create DTO with portal `startTime`/`endTime`
5. [x] Staff portal: “AI Call to Book / Remind” → Dial call API

## P1 — Live GetDial (EstateCraft-compatible)

1. [x] Same env contract: `VOICE_PROVIDER`, `DIAL_API_KEY`, `DIAL_BASE_URL`, `DIAL_FROM_NUMBER_ID`
2. [x] Same Dial REST client payloads (`fromNumberId`, `outboundInstruction`)
3. [x] Voice rules table + API (`/api/voice-rules`) matching EstateCraft fields
4. [x] `POST /api/communications/call` + `GET /api/communications/call/{id}/status`
5. [x] `POST /api/webhooks/dial` (EstateCraft path)
6. [x] Orchestrator: scheduled retries (`scheduled_follow_ups`) + SMS fallback templates
7. [x] `{{leadName}}` / `{{patientName}}` template personalization

## P2 — Platform

1. Multi-tenant (`tenant_id`, JWT claim, per-tenant Dial keys)
2. Service-account auth for voice/tool callbacks
3. Background worker for `POST /api/voice/retries/process` on a schedule
4. RCM only if product requires it

## Quick local checks

```bash
curl -s http://localhost:8087/voice/providers/health | jq .
curl -s -X POST http://localhost:8087/communications/call \
  -H 'Content-Type: application/json' \
  -d '{"patientId":1,"purpose":"BOOKING"}'
curl -s http://localhost:8087/voice-rules | jq .
```

> Real Dial keys are **not** in EstateCraft git. Set them in `.env` when going live.
