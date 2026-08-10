# Proposed Project Rename (needs your confirmation)

**Do not rename until you reply with a choice.** Implementation of the GetDial plan continues under `PulseCare` / `pulsecare-telehealth` until then.

## Recommendation

**Primary pick: `VoxCare`**  
Short, brandable, signals **voice + care**, fits telehealth + GetDial booking without sounding like a billing/RCM product.

Repo would become `voxcare-telehealth` (or just `voxcare`). Java base package `com.voxcare`.

## Alternatives

| Name | Why it works | Caveat |
|------|----------------|--------|
| **VoxCare** ★ | Voice-first, modern SaaS, easy to say | “Vox” is used by some other products |
| **CareLine** | Phone-line + care; clear for dialer scheduling | Slightly generic |
| **PulseLine** | Keeps “Pulse” equity, adds dialer signal | Closer to current name |
| **Resona Health** | Distinctive (resonance / voice) | Longer; may need trademark check |
| **HelixCare** | Clinical/modern without voice cue | Less dialer-specific |

## What rename would touch (after you confirm)

- GitHub repo name (`gh` rename / settings)
- Local folder branding: README, `docker-compose`, k8s namespace, DB name, package `com.pulsecare` → new package
- Frontend titles, env prefixes, Eureka service names

Reply with one of: `VoxCare`, `CareLine`, `PulseLine`, `Resona Health`, `HelixCare`, or another name you prefer.
