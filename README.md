# BanSystem

> Plugin di gestione sanzioni per server Minecraft Spigot/Paper/Purpur 1.8 → 1.21+
> Con supporto **BungeeCord** e **Velocity**

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.8%20--%201.21+-green)
![Author](https://img.shields.io/badge/author-4Wizm-red)

## ✨ Features

- 🚫 **4 tipi di sanzioni**: Ban, Kick, Mute, Blacklist
- ⏱️ **Durata flessibile**: `1s`, `1m`, `1h`, `1d`, `1w` o **permanente**
- 👤 **Funziona anche su giocatori offline** (se hanno giocato almeno una volta sul server)
- 🌐 **Sistema IP**: identifica account collegati via stesso IP
- 📝 **Log persistente** in `logs.yml` con data, staff, motivo, durata
- ⏳ **Tempo reale**: mostra il tempo rimanente esatto quando entri
- 🔄 **Modalità Proxy** (BungeeCord / Velocity): ON/OFF dal config
- 👻 **Comandi invisibili**: chi non ha permesso non li vede nel TAB e non riceve risposta
- 📢 **Broadcast staff**: solo chi ha il permesso vede il messaggio di punizione
- 🛡️ **Protezione highstaff**: non puoi punire te stesso né chi ha `bansystem.staff.highstaff`
- 🎨 **Messaggi 100% personalizzabili** con colori `&`
- 🕐 **Orario italiano** (Europe/Rome) nei log
- 📜 **Multi-riga** per ban/kick/mute/blacklist screen
- 🎯 **Compatibile** Spigot / Paper / Purpur da **1.8** a **1.21+**

---

## 📦 Installazione

1. Scarica `BanSystem-1.0.0.jar`
2. Copia in `plugins/` del tuo server
3. Riavvia il server
4. Il plugin genera automaticamente:
   - `plugins/BanSystem/config.yml`
   - `plugins/BanSystem/logs.yml`
5. Configura i permessi con **LuckPerms**

**Se usi BungeeCord/Velocity**: installa il plugin in **ogni server** (non sul proxy) e imposta `proxy.enabled: true` nel config.

---

## 🎮 Comandi

### 🚫 Punizioni (staff)

| Comando | Descrizione |
|---------|-------------|
| `/ban <utente> <motivo> [durata]` | Banna un utente (online o offline) |
| `/kick <utente> <motivo> [durata]` | Kicka un utente (solo se online) |
| `/mute <utente> <motivo> [durata]` | Muta un utente (online o offline) |
| `/blacklist <utente> <motivo>` | Blacklist permanente + IP-ban |

**Formati durata**:

| Formato | Significato |
|---------|-------------|
| `30s` | 30 secondi |
| `15m` | 15 minuti |
| `2h` | 2 ore |
| `7d` | 7 giorni |
| `2w` | 2 settimane |
| `100` | Permanente (solo numero) |
| _(nessuna durata)_ | Permanente |

### ✅ Rimozione punizioni (staff)

| Comando | Descrizione |
|---------|-------------|
| `/unban <utente>` | Rimuove tutti i ban attivi |
| `/unmute <utente>` | Rimuove tutti i mute attivi |
| `/unkick <utente>` | Rimuove tutti i kick attivi |
| `/unblacklist <utente>` | Rimuove la blacklist |

### 📋 Info storico (staff)

| Comando | Descrizione |
|---------|-------------|
| `/baninfo <utente>` | Storico ban (data, staff, motivo, durata) |
| `/muteinfo <utente>` | Storico mute |
| `/kickinfo <utente>` | Storico kick |
| `/blacklistinfo <utente>` | Storico blacklist |

### 🔍 Controllo IP (staff)

| Comando | Descrizione |
|---------|-------------|
| `/dueip <utente>` | Account collegati via IP → ban |
| `/muteip <utente>` | Account collegati via IP → mute |
| `/kickip <utente>` | Account collegati via IP → kick |

**Output**:
- 🔴 Nome **rosso** = account attualmente sanzionato
- 🟢 Nome **verde** = account non sanzionato

### ⚙️ Amministrazione

| Comando | Descrizione |
|---------|-------------|
| `/bansystem` | Info plugin |
| `/bansystem reload` | Ricarica configurazione |

---

## 🔐 Permessi

### Permessi punizione

| Permesso | Comando |
|----------|---------|
| `bansystem.staff.use.ban` | `/ban` |
| `bansystem.staff.use.kick` | `/kick` |
| `bansystem.staff.use.mute` | `/mute` |
| `bansystem.staff.use.blacklist` | `/blacklist` |

### Permessi rimozione

| Permesso | Comando |
|----------|---------|
| `bansystem.staff.use.unban` | `/unban` |
| `bansystem.staff.use.unmute` | `/unmute` |
| `bansystem.staff.use.unkick` | `/unkick` |
| `bansystem.staff.use.unblacklist` | `/unblacklist` |

### Permessi info

| Permesso | Comando |
|----------|---------|
| `bansystem.staff.use.baninfo` | `/baninfo` |
| `bansystem.staff.use.muteinfo` | `/muteinfo` |
| `bansystem.staff.use.kickinfo` | `/kickinfo` |
| `bansystem.staff.use.blacklistinfo` | `/blacklistinfo` |

### Permessi controllo IP

| Permesso | Comando |
|----------|---------|
| `bansystem.staff.use.dueip` | `/dueip` |
| `bansystem.staff.use.muteip` | `/muteip` |
| `bansystem.staff.use.kickip` | `/kickip` |

### Permessi speciali

| Permesso | Descrizione |
|----------|-------------|
| `bansystem.staff.use.reload` | `/bansystem reload` |
| `bansystem.staff.highstaff` | 🛡️ **Immunità**: non puoi essere punito da altri staff |

---

## ⚙️ Configurazione

### Config di base

```yaml
proxy:
  enabled: false
  type: BUNGEECORD
