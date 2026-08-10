# GetDial Voice AI Integration Strategy (VoxCare)

**Status:** Ready to implement  
**Derived from:** [EstateCraft Dial integration](https://github.com/new-world-coder/estatecraft) (`docs/DIAL_INTEGRATION.md`, `DialVoiceProvider`, provider factory, webhooks)  
**Target product flow:** Voice AI books / confirms / reminds telehealth appointments in VoxCare

---

## 1. Goals

| Goal | Description |
|------|-------------|
| Outbound booking calls | Dial AI calls a patient, offers slots, books via VoxCare APIs |
| Inbound (phase 2) | Patient calls clinic number; Dial agent books/reschedules |
| Reminders + SMS fallback | Same Dial number for voice reminder + SMS if no answer |
| Provider isolation | No Dial SDK leaks outside a provider adapter (EstateCraft pattern) |

Non-goals for v1: full RCM, multi-tenant Dial credentials, human call-center softphone UI.

---

## 2. Lessons From EstateCraft

EstateCraft already solved the vendor boundary. Reuse the **pattern**, not the Node runtime.

| EstateCraft artifact | VoxCare equivalent |
|----------------------|----------------------|
| `IVoiceProvider` | `VoiceProvider` (Java interface) |
| `DialVoiceProvider` | `DialVoiceProvider` calling `https://api.getdial.ai` |
| `MockVoiceProvider` / factory | `MockVoiceProvider` + `VoiceProviderFactory` |
| `POST /api/communications/call` | `POST /api/voice/calls` |
| `POST /api/webhooks/dial` | `POST /api/voice/webhooks/dial` |
| `VOICE_PROVIDER` / `DIAL_*` env | Same env names in `env.example` |
| Lead + voice rules | Patient + **appointment voice scripts** (healthcare-specific instructions) |
| SMS fallback on retry exhaustion | Reuse Dial `/v1/messages`; optionally also `notification-service` |

### Dial API contract (validated in EstateCraft)

```http
POST /v1/calls
Authorization: Bearer {DIAL_API_KEY}
Content-Type: application/json

{
  "to": "+15551234567",
  "fromNumberId": "{DIAL_FROM_NUMBER_ID}",
  "outboundInstruction": "You are VoxCare's scheduling assistant..."
}
```

Also used: `GET /v1/calls/{id}`, `POST /v1/messages`, `GET /v1/numbers`.  
Webhook URL to register in Dial dashboard: `https://{gateway}/api/voice/webhooks/dial`.

---

## 3. Target Architecture

```
┌──────────────────┐     ┌─────────────────┐     ┌──────────────────────┐
│ Staff Portal     │────►│  API Gateway    │────►│ voice-service :8087  │
│ (trigger call)   │     │  /api/voice/**  │     │  VoiceProvider       │
└──────────────────┘     └────────┬────────┘     │   ├─ Dial (prod)     │
                                  │              │   └─ Mock (local)    │
┌──────────────────┐              │              └──────────┬───────────┘
│ Dial (GetDial)   │──webhook─────┘                         │
│ AI voice + SMS   │◄── REST /v1/calls ─────────────────────┘
└────────┬─────────┘
         │ (during / after call, VoxCare tools)
         ▼
┌────────────────────────────────────────────────────────────┐
│ appointment-service  │ patient-service │ provider-service  │
│ POST /appointments   │ lookup by phone │ availability CRUD │
└────────────────────────────────────────────────────────────┘
```

**Why a dedicated `voice-service` (not stuffing Dial into notification-service)?**

- Voice has call state, transcripts, retries, and webhooks — different lifecycle than email/SMS notifications.
- Matches EstateCraft’s engagement boundary and keeps notification-service focused on channel delivery.
- SMS fallback can still call Dial from voice-service **or** hand off to notification-service later.

---

## 4. Prerequisites Before Live Dial Traffic

These are **blockers** for reliable booking calls:

| # | Gap | Work |
|---|-----|------|
| 1 | `patient-service` is a stub | Implement CRUD + `GET /patients/by-phone/{e164}` |
| 2 | `provider-service` is a stub | Implement list providers + specialties |
| 3 | Availability has SQL but no API | CRUD on `availability` + “open slots” query |
| 4 | Appointment DTO mismatch | Align portal/`CreateAppointmentRequest` fields (`appointmentDate`/`durationMinutes` vs `startTime`/`endTime`) |
| 5 | Machine auth | Service account / API key for Dial tool callbacks (do not use patient passwords) |
| 6 | PHI logging | Mask phone/transcripts in logs (HIPAA-friendly baseline) |

Phases 0–1 in §7 can ship with mock provider while these APIs are completed.

---

## 5. Voice Script & Booking Tool Flow

### Outbound instruction template (example)

```text
You are VoxCare's appointment scheduling assistant for {{clinicName}}.
Patient name: {{patientName}}.
Offer available slots for {{specialty}} with these options:
{{slotList}}
If the patient confirms a slot, call the booking tool with patientId, providerId, and ISO start time.
If they decline, offer to call back later. Be concise, polite, and HIPAA-aware: do not discuss diagnoses.
```

### Tool / backend sequence (outbound booking)

1. Staff or scheduler triggers `POST /api/voice/calls` with `{ patientId, purpose: BOOKING|REMINDER|RESCHEDULE }`.
2. `voice-service` loads patient phone, builds `outboundInstruction` with open slots from provider/availability APIs.
3. `DialVoiceProvider.initiateCall(...)` → Dial places AI call.
4. Persist `voice_calls` row (`external_id`, status, purpose, patient_id).
5. Dial webhook updates status/transcript; on success transcript or Dial tool callback, create appointment via `POST /api/appointments`.
6. On `NO_ANSWER` / `BUSY` after retries → Dial SMS with booking link or callback message.

> **Note:** EstateCraft’s Dial client only sends `to`, `fromNumberId`, `outboundInstruction`. If Dial’s product adds structured tool webhooks / function calling for booking, map those into `AppointmentBookingClient` inside voice-service. Until then, parse confirmed outcomes from completion webhooks + staff confirmation, or use Dial’s documented tool/callback features when available in your Dial plan.

---

## 6. Data Model (new)

```sql
CREATE TABLE voice_calls (
    id              BIGSERIAL PRIMARY KEY,
    external_id     VARCHAR(128) UNIQUE,
    provider        VARCHAR(32) NOT NULL DEFAULT 'dial',
    purpose         VARCHAR(32) NOT NULL, -- BOOKING, REMINDER, RESCHEDULE, FOLLOW_UP
    patient_id      BIGINT,
    appointment_id  BIGINT,
    to_number       VARCHAR(32) NOT NULL,
    status          VARCHAR(32) NOT NULL,
    outcome         VARCHAR(32),
    duration_seconds INTEGER,
    transcript      TEXT,
    retry_count     INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at        TIMESTAMP
);

CREATE TABLE voice_call_events (
    id            BIGSERIAL PRIMARY KEY,
    voice_call_id BIGINT NOT NULL REFERENCES voice_calls(id) ON DELETE CASCADE,
    event_type    VARCHAR(64) NOT NULL,
    payload_json  TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Optional later: `voice_scripts` table for editable instructions (like EstateCraft voice rules).

---

## 7. Phased Implementation Roadmap

### Phase 0 — Scaffold (this PR) ✅

- [x] Assessment + strategy docs  
- [x] `voice-service` module with `VoiceProvider`, Mock + Dial adapters, factory  
- [x] REST stubs: initiate call, get status, Dial webhook  
- [x] Gateway route `/api/voice/**`  
- [x] Env vars for Dial  
- [x] Schema migration snippet for `voice_calls`

### Phase 1 — Local mock end-to-end

- Complete patient/provider/availability APIs (minimum for booking)
- Implement `AppointmentBookingClient` (WebClient → appointment-service)
- Staff portal: “Call to book / remind” button → `POST /api/voice/calls`
- Unit tests for Dial payload mapping + mock provider
- Default `VOICE_PROVIDER=mock`

### Phase 2 — Live Dial outbound

- Set `VOICE_PROVIDER=dial` + `DIAL_API_KEY` + `DIAL_FROM_NUMBER_ID`
- Register webhook URL
- Harden webhook signature verification if Dial provides signing secrets
- Reminder job: poll `/appointments/reminders` → enqueue voice calls
- SMS fallback via Dial `/v1/messages`

### Phase 3 — Inbound + multi-tenant

- Inbound Dial number → clinic routing
- Per-tenant Dial credentials once multi-tenancy lands
- Compliance: consent flags, call recording policy, audit retention

---

## 8. Configuration

```env
VOICE_PROVIDER=mock          # mock | dial
VOICE_SERVICE_PORT=8087
DIAL_API_KEY=
DIAL_BASE_URL=https://api.getdial.ai
DIAL_FROM_NUMBER_ID=
DIAL_WEBHOOK_SECRET=         # if Dial supports signature verification
VOICE_MAX_RETRIES=3
VOICE_RETRY_DELAY_MINUTES=30
VOICE_SMS_FALLBACK_ENABLED=true
```

Never commit real Dial keys. Local default remains `mock`.

---

## 9. API Surface (v1)

| Method | Path | Purpose |
|--------|------|---------|
| `POST` | `/api/voice/calls` | Start outbound AI call |
| `GET` | `/api/voice/calls/{id}` | Local call record |
| `GET` | `/api/voice/calls/external/{externalId}` | Lookup by Dial id |
| `POST` | `/api/voice/webhooks/dial` | Dial status / transcript events |
| `GET` | `/api/voice/providers/health` | Provider configured + numbers list (admin) |

### Initiate call body

```json
{
  "patientId": 1,
  "to": "+15551234567",
  "purpose": "BOOKING",
  "providerId": 1,
  "appointmentId": null,
  "outboundInstruction": "optional override"
}
```

---

## 10. Security & Compliance Notes

- Treat transcripts as **PHI-adjacent**; encrypt at rest when moving beyond demo.
- Mask phone numbers in application logs (existing VoxCare PII masking pattern).
- Do not put diagnosis text into Dial instructions.
- Webhook endpoint must be authenticated or signature-verified in production.
- Document Dial as a BA under HIPAA if used with real PHI (legal/ops, outside code).

---

## 11. How to Start Building Features

1. Read [`ASSESSMENT.md`](./ASSESSMENT.md) for product boundaries.  
2. Run stack with `VOICE_PROVIDER=mock` once `voice-service` is wired in compose.  
3. Implement patient-by-phone + availability open-slots (highest leverage).  
4. Connect staff “Call patient” UI to `POST /api/voice/calls`.  
5. Flip to Dial in a staging env with a test number.  

Scaffold code lives under `backend/voice-service/`. Keep all GetDial HTTP details inside `provider/DialVoiceProvider.java`.
