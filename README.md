# Camping Manager — Backend

Backend REST per la gestione di un campeggio (gestionale ricettivo / PMS), realizzato come progetto finale del corso **EPICODE — Backend Programming**.

Lo **staff** gestisce i soggiorni come in un gestionale alberghiero; al **check-in** crea l'account dell'**ospite**, valido per la durata del soggiorno. Durante il soggiorno l'ospite può prenotare un tavolo al ristorante e noleggiare (e pagare) le bici. L'**admin** ha il controllo completo e accede a statistiche avanzate.

---

## Stack tecnologico

- **Java 21**, **Spring Boot 3.5**
- **PostgreSQL** (database) — **H2** in-memory per i test
- **Spring Data JPA** / Hibernate
- **Spring Security** + **JWT** (jjwt)
- **Maven** (con wrapper `mvnw`)
- API di terze parti: **Stripe** (pagamenti), **OpenWeatherMap** (meteo), **Brevo/SendGrid** (email)

## Requisiti d'esame coperti

| Requisito | Dove |
|-----------|------|
| ≥ 8 tabelle con relazioni coerenti | ~13 tabelle |
| ≥ 1 struttura di ereditarietà | **2** gerarchie JOINED: Utenti (Admin/Staff/Ospite) e Alloggi (Chalet/Piazzola) |
| Gestione utenti completa (email, password, immagine profilo aggiornabile, dati anagrafici) | modulo `users` |
| REST API coerenti | tutti i controller |
| Auth JWT + ≥ 3 ruoli con permessi | ADMIN / STAFF / OSPITE |
| Query (filtri, ordinamento, aggregazioni, condizioni multiple) | disponibilità alloggi/bici, statistiche |
| Error handling strutturato + validazione | `GlobalExceptionHandler` + Bean Validation |
| ≥ 2 API di terze parti integrate | Stripe + OpenWeatherMap + Email (3) |
| README + Postman collection | questo file + `postman/` |

---

## Prerequisiti

- **JDK 21**
- **PostgreSQL** in esecuzione (default: `localhost:5432`)

## Avvio

1. **Crea il database**:
   ```sql
   CREATE DATABASE camping_manager;
   ```

2. **Configura le variabili d'ambiente** (vedi sotto). Copia `application.properties.example` come riferimento; tutte le proprietà hanno valori di default per lo sviluppo locale, quindi l'app parte anche senza configurare nulla (tranne le API esterne, che restano disattivate finché non fornisci le chiavi).

3. **Avvia l'applicazione**:
   ```bash
   ./mvnw spring-boot:run
   ```
   L'app è disponibile su `http://localhost:8080`.

4. **Esegui i test**:
   ```bash
   ./mvnw test
   ```

Al primo avvio vengono creati automaticamente (`DataInitializer`):
- un **amministratore** con le credenziali da `ADMIN_EMAIL` / `ADMIN_PASSWORD` (default: `admin@camping.it` / `Admin123!`);
- **10 Chalet** e **10 Piazzole**.

---

## Variabili d'ambiente

| Variabile | Default | Descrizione |
|-----------|---------|-------------|
| `DB_NAME` | `camping_manager` | Nome del database |
| `DB_USERNAME` | `postgres` | Utente PostgreSQL |
| `DB_PASSWORD` | `postgres` | Password PostgreSQL |
| `JWT_SECRET` | (chiave di sviluppo) | Segreto per firmare i JWT (≥ 32 caratteri) |
| `JWT_EXPIRATION_MS` | `86400000` | Durata del token (ms) |
| `UPLOAD_DIR` | `uploads` | Cartella per le immagini profilo |
| `ADMIN_EMAIL` | `admin@camping.it` | Email admin iniziale |
| `ADMIN_PASSWORD` | `Admin123!` | Password admin iniziale |
| `APP_BASE_URL` | `http://localhost:8080` | URL base (redirect Stripe) |
| `STRIPE_SECRET_KEY` | (vuoto) | Chiave segreta Stripe |
| `STRIPE_WEBHOOK_SECRET` | (vuoto) | Segreto del webhook Stripe |
| `WEATHER_API_KEY` | (vuoto) | Chiave OpenWeatherMap |
| `WEATHER_CITY` | `Trento,IT` | Località per il meteo |
| `EMAIL_API_KEY` | (vuoto) | Chiave API Brevo/SendGrid |
| `EMAIL_FROM` | `noreply@campingmanager.it` | Mittente delle email |
| `EMAIL_FROM_NAME` | `Camping Manager` | Nome mittente |
| `SERVER_PORT` | `8080` | Porta del server |

> Le API esterne **degradano in modo sicuro**: senza chiave, l'email non viene inviata (solo log) e meteo/pagamenti restituiscono un errore chiaro, senza bloccare il resto dell'applicazione.

---

## Ruoli e permessi

| Ruolo | Permessi |
|-------|----------|
| **ADMIN** | Tutto: gestione utenti/staff, alloggi, tavoli, bici, statistiche avanzate |
| **STAFF** | Soggiorni (vista PMS, check-in/out), gestione tavoli/bici, restituzione noleggi |
| **OSPITE** | Solo durante il soggiorno: profilo, prenotazione tavolo, noleggio + pagamento bici, meteo |

Nessuna registrazione pubblica: l'ADMIN crea lo STAFF, lo STAFF crea gli OSPITI al check-in.

---

## Funzionalità ed endpoint

### Autenticazione
- `POST /api/auth/login` — login, restituisce il JWT

### Profilo utente
- `GET /api/users/me` — profilo corrente
- `PATCH /api/users/me/avatar` — upload immagine profilo (multipart `file`)
- `PATCH /api/users/me/password` — cambio password

### Gestione utenti (ADMIN)
- `GET /api/admin/users` — lista paginata (filtro `role`)
- `GET /api/admin/users/{id}`
- `POST /api/admin/staff` — crea account staff
- `DELETE /api/admin/users/{id}`

### Alloggi
- `GET /api/accommodations` — lista (filtri `type`, `status`)
- `GET /api/accommodations/available` — disponibili per date (`checkIn`, `checkOut`, `capacity`, `type`)
- `GET /api/accommodations/{id}`
- `POST` / `PUT /{id}` / `DELETE /{id}` — gestione (ADMIN)

### Soggiorni — PMS (STAFF/ADMIN)
- `GET /api/stays` — vista prenotazioni (filtri `status`, `accommodationId`)
- `GET /api/stays/today` — arrivi/partenze del giorno
- `GET /api/stays/{id}`
- `POST /api/stays` — crea soggiorno
- `PUT /api/stays/{id}` — modifica
- `DELETE /api/stays/{id}` — cancella
- `PATCH /api/stays/{id}/checkin` — check-in (crea account ospite + invio credenziali)
- `PATCH /api/stays/{id}/checkout` — check-out

### Ristorante
- `GET /api/restaurant/tables` — lista tavoli
- `POST /api/restaurant/tables` — crea tavolo (STAFF/ADMIN)
- `GET /api/restaurant/bookings` — prenotazioni (l'ospite vede le proprie)
- `POST /api/restaurant/bookings` — prenota tavolo (OSPITE)
- `PATCH /api/restaurant/bookings/{id}/cancel`

### Bici
- `GET /api/bikes` — lista (filtri `type`, `status`)
- `POST /api/bikes` — crea bici (STAFF/ADMIN)
- `GET /api/bikes/available` — disponibili per date (`start`, `end`)
- `POST /api/bikes/rentals` — crea noleggio (OSPITE) → `PENDING_PAYMENT`
- `GET /api/bikes/rentals` — noleggi (l'ospite vede i propri)
- `PATCH /api/bikes/rentals/{id}/return` — restituzione (STAFF/ADMIN)

### Pagamenti (Stripe — solo noleggio bici)
- `POST /api/payments/rentals/{rentalId}/checkout` — crea sessione Stripe (OSPITE)
- `GET /api/payments/success?session_id=...` — conferma pagamento (redirect)
- `POST /api/payments/webhook` — webhook Stripe
- `GET /api/payments/{id}` — stato pagamento

### Meteo
- `GET /api/weather` — meteo corrente della località (+ flag maltempo)

### Statistiche (ADMIN)
- `GET /api/admin/stats/occupancy` — % occupazione alloggi (`from`, `to`)
- `GET /api/admin/stats/rental-revenue` — fatturato noleggi per mese
- `GET /api/admin/stats/popular-bikes` — tipi di bici più noleggiati
- `GET /api/admin/stats/busy-nights` — serate ristorante più affollate
- `GET /api/admin/stats/in-house` — ospiti attualmente in struttura

---

## Postman

La collection completa è in [`postman/camping-manager.postman_collection.json`](postman/camping-manager.postman_collection.json).
Importala in Postman: la richiesta di **login** salva automaticamente il token nella variabile `{{token}}`, usata da tutte le richieste protette. Imposta la variabile `{{baseUrl}}` (default `http://localhost:8080`).

## Struttura del progetto

```
com.campingmanager
├── auth/            — login
├── security/        — JWT (util, filtro, configurazione)
├── users/           — utenti (gerarchia Admin/Staff/Ospite)
├── accommodations/  — alloggi (gerarchia Chalet/Piazzola)
├── stays/           — soggiorni (PMS, check-in/out)
├── restaurant/      — tavoli e prenotazioni
├── bikes/           — bici e noleggi
├── payments/        — Stripe
├── weather/         — OpenWeatherMap
├── email/           — Brevo/SendGrid
├── stats/           — statistiche admin
├── config/          — seed dati, risorse statiche
└── exceptions/      — gestione centralizzata errori
```
