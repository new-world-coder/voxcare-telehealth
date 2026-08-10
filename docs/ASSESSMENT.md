# VoxCare Telehealth — Project Assessment

**Date:** August 2026  
**Repo:** [new-world-coder/voxcare-telehealth](https://github.com/new-world-coder/voxcare-telehealth)  
**Purpose:** Answer three product questions before feature expansion (RCM share, multi-tenant SaaS readiness, dialer fit).

---

## Executive Verdicts

| Question | Verdict |
|----------|---------|
| Is this a Revenue Cycle Management (RCM) project? | **No.** RCM is essentially absent (~**0–2%** of domain code). |
| Is it multi-tenant SaaS today? | **No.** Single-tenant deployment with role-based access only. |
| Can a voice AI dialer plug into appointment booking? | **Yes.** Appointment REST APIs exist; voice/SMS and some supporting services need to be built. |

**What VoxCare is today:** a telehealth + appointment scheduling platform (Spring Boot microservices, React staff portal, Vue patient portal, Jitsi sessions).

---

## 1. Revenue Cycle Management (RCM)

### Verdict: not an RCM product (~0–2%)

VoxCare is a **clinical / scheduling / telehealth** system. There are no billing, claims, insurance eligibility, coding, remittance, denial, AR, or payment-capture modules.

### Approximate domain mix (backend business code)

| Domain | Services | Approx. share |
|--------|----------|---------------|
| Telehealth / video | `telehealth-service` | ~25–30% |
| Notifications | `notification-service` | ~25–30% |
| Appointments / scheduling | `appointment-service` | ~20–25% |
| Auth / RBAC | `auth-service` | ~20–25% |
| Patient / provider | stubs only | ~1% |
| **RCM (billing, claims, ERA, coding, etc.)** | **none** | **~0%** |

### Evidence

- Gateway routes cover auth, patients, providers, appointments, notifications, telehealth only (`backend/api-gateway/.../application.yml`).
- Schema has `users`, `patients`, `providers`, `availability`, `appointments` — no insurance/billing tables (`scripts/init-db.sql`).
- Frontends are appointment/provider/profile/dashboard UX only.
- Sole RCM-adjacent traces: `PAYMENT_REMINDER` / `PAYMENT_CONFIRMATION` enum values in notification types (no payment service), plus aspirational PCI wording in `SECURITY-COMPLIANCE.md`.

### Implication for roadmap

If RCM is a future goal, treat it as a **new product surface** (claims, eligibility, charge capture, patient payments), not a refactor of existing code. Current assets that help later: patient/provider identity, appointment completion events, and notification channels.

---

## 2. Multi-Tenant SaaS

### Verdict: single-tenant only

| Check | Finding |
|-------|---------|
| `tenant` / `org_id` / clinic isolation in models | Not present |
| Auth model | `User` = email + password + role (`PATIENT` / `PROVIDER` / `ADMIN`) |
| Database | One `voxcare` DB; no tenant column |
| Routing | Single host / single deployment |
| Config | Global env (`env.example`) |

Isolation today is **RBAC**, not tenancy. One practice’s patients, providers, and appointments would share the same namespace.

### Implication for roadmap

To become multi-tenant SaaS, add at minimum:

1. `Organization` / `Tenant` entity and `tenant_id` on all domain tables  
2. Tenant context in JWT + gateway/filter enforcement  
3. Per-tenant config (Dial keys, Jitsi, notification branding)  
4. Provisioning / onboarding API  

See also EstateCraft’s SaaS notes for a shared-DB tenancy pattern; VoxCare already has stronger K8s/Docker packaging than EstateCraft, so tenancy is the main gap—not deployability.

---

## 3. Appointment Booking & Dialer Fit

### What exists

| Capability | Status |
|------------|--------|
| Create / cancel / reschedule appointments | Implemented REST (`appointment-service`) |
| Conflict detection | Implemented |
| Availability table | In SQL + seed data; **no Java CRUD API** (provider-service is a stub) |
| Patient / provider profile services | **Stubs** (application class only) |
| Email notifications | Partial (JavaMail / SendGrid flag) |
| SMS | Stub + Twilio env vars (not wired) |
| Voice / dialer | **None** |
| Calendar sync | None |
| Telehealth session after booking | Jitsi via `telehealth-service` |

### Dialer fit

A GetDial (or similar) voice AI agent can sit in front of booking if we:

1. Authenticate as a service account (or machine JWT) against `/api/auth`
2. Look up / create patients by phone (needs real patient APIs)
3. List providers + open slots (needs availability APIs)
4. Call `POST /api/appointments`
5. Confirm via SMS/email (`notification-service` / Dial SMS)
6. Optionally create a telehealth session

**Conclusion:** booking infrastructure is **sufficient as an integration target**; supporting lookup APIs and a voice microservice are the missing pieces. Full strategy: [`GETDIAL_INTEGRATION_STRATEGY.md`](./GETDIAL_INTEGRATION_STRATEGY.md).

---

## 4. Recommended Feature Expansion Order

1. **Foundation (this PR):** assessment + GetDial strategy + `voice-service` scaffold  
2. **Complete patient/provider/availability APIs** (unblock dialer and portals)  
3. **Wire Dial outbound + webhooks** for appointment booking / reminders  
4. **Multi-tenant isolation** before selling to multiple clinics  
5. **RCM only if product requires it** — separate bounded context, not bolted into telehealth

---

## Summary

VoxCare is a **telehealth scheduler**, not RCM, and not multi-tenant SaaS yet. Appointment APIs are a viable hook for GetDial voice AI. Proceed with the GetDial integration strategy and complete the patient/provider/availability gaps before production dialer traffic.
