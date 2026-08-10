# Dial Integration Guide (VoxCare)

Ported **as-is** from [EstateCraft Dial Integration](https://github.com/new-world-coder/estatecraft/blob/main/docs/DIAL_INTEGRATION.md), with healthcare naming (`patientId` instead of `leadId`).

## Overview

VoxCare integrates [Dial](https://getdial.ai) as the production `VoiceProvider`, using the same REST contract EstateCraft validated:

- Bearer auth
- camelCase payloads (`fromNumberId`, `outboundInstruction`)
- Endpoints: `/v1/calls`, `/v1/calls/{id}`, `/v1/messages`, `/v1/numbers`

Dial provides outbound AI voice calls and SMS on a single phone number.

## Configuration

Same env vars as EstateCraft:

```env
VOICE_PROVIDER=dial
DIAL_API_KEY=sk_live_your_key_here
DIAL_FROM_NUMBER_ID=your_number_id
DIAL_BASE_URL=https://api.getdial.ai
```

| Variable | Required | Description |
|----------|----------|-------------|
| `VOICE_PROVIDER` | Yes | Set to `dial` |
| `DIAL_API_KEY` | Yes | API key from Dial dashboard |
| `DIAL_FROM_NUMBER_ID` | Recommended | ID of provisioned Dial number |
| `DIAL_BASE_URL` | No | Defaults to `https://api.getdial.ai` |

> EstateCraft does **not** store real Dial keys in git. Put yours in `.env` / deployment secrets — never commit them.

## Provisioning a Number

1. Sign up at [getdial.ai](https://getdial.ai)
2. Provision a US number via Dial dashboard or API
3. Copy the number ID to `DIAL_FROM_NUMBER_ID`

If `DIAL_FROM_NUMBER_ID` is not set, the provider calls `listNumbers()` and uses the first available number.

**Note (from EstateCraft UI):** Dial live calls expect a US `+1...` number. Some regions (including `+91`) may be blocked until Dial unlocks them.

## Outbound Calls

Calls are triggered by:

1. **Staff dashboard** — AI Call to Book / Remind
2. **EstateCraft-compatible API** — `POST /api/communications/call` with `{ "patientId": 1 }` (also accepts `leadId`)
3. **VoxCare API** — `POST /api/voice/calls`
4. **Reminder enqueue** — `POST /api/voice/reminders/enqueue`

Active **voice rules** supply `outboundInstruction`, `maxRetries`, `retryDelayMinutes`, and SMS templates — same fields as EstateCraft `VoiceRule`.

Templates support EstateCraft placeholders:

- `{{leadName}}` / `{{patientName}}` → patient full name
- `{{firstName}}` → patient first name

## SMS Fallback

When a voice call fails or receives no answer after max retries, the orchestrator sends SMS via Dial `/v1/messages` if `smsFallbackEnabled` is true on the voice rule (same as EstateCraft).

## Webhooks

Register Dial webhooks to either path (both work):

```
https://your-domain/api/webhooks/dial
https://your-domain/api/voice/webhooks/dial
```

The handler updates call status/transcripts and schedules retries / SMS fallback using the EstateCraft orchestrator logic.

## Retry Logic

Configured per voice rule (defaults match EstateCraft seed):

- `maxRetries` — default 3
- `retryDelayMinutes` — default 30

Retries create `scheduled_follow_ups` with type `voice_retry`. Process due retries with:

```bash
curl -X POST http://localhost:8087/voice/retries/process
```

## Switching Providers

```env
VOICE_PROVIDER=mock   # local, no Dial
VOICE_PROVIDER=dial   # production GetDial
```

Twilio remains a future stub (same as EstateCraft).

## Code Location

| File | Purpose |
|------|---------|
| `provider/VoiceProvider.java` | EstateCraft `IVoiceProvider` |
| `provider/DialVoiceProvider.java` | Dial REST client (same payloads) |
| `provider/VoiceProviderFactory.java` | `mock` / `dial` / `twilio` selection |
| `orchestrator/CommunicationOrchestrator.java` | Retry + SMS fallback |
| `model/VoiceRule.java` | EstateCraft voice rules |
| `controller/CommunicationsController.java` | `POST /communications/call` |
| `controller/DialWebhookController.java` | `POST /webhooks/dial` |

No Dial-specific HTTP details exist outside `provider/DialVoiceProvider.java`.
