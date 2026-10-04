# WhistleDrop

WhistleDrop is a confidential reporting backend built with Spring Boot, PostgreSQL, Spring Security and JWT. Reporters do not create accounts and no reporter identity is stored.

## Requirements

- Java 17+
- Maven 3.9+
- PostgreSQL 14+

## Setup

Create a PostgreSQL database named `whistledrop`.

```sql
CREATE DATABASE whistledrop;
```

Set environment variables or edit `src/main/resources/application.properties`:

```text
DB_URL=jdbc:postgresql://localhost:5432/whistledrop
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=replace-with-a-random-secret-at-least-32-bytes-long
JWT_EXPIRATION_MS=3600000
MODERATOR_USERNAME=moderator
MODERATOR_PASSWORD=replace-with-a-strong-password
```

Run:

```bash
mvn spring-boot:run
```



Swagger UI is available at (https://whistledrop-vignesh.onrender.com/swagger-ui/index.html)

## API

### Submit anonymous report

`POST /api/reports`

```json
{
  "category": "SECURITY",
  "description": "A security issue was discovered in the internal application.",
  "evidenceUrl": "https://example.com/evidence"
}
```

Response `201 Created`:

```json
{
  "caseCode": "WD-7KQ8M2N4R6T9W3X5Y7Z2",
  "category": "SECURITY",
  "description": "A security issue was discovered in the internal application.",
  "evidenceUrl": "https://example.com/evidence",
  "status": "SUBMITTED",
  "createdAt": "2026-10-03T05:00:00Z",
  "updatedAt": "2026-10-03T05:00:00Z",
  "updates": [
    {
      "status": "SUBMITTED",
      "message": "Report submitted successfully and is awaiting review.",
      "createdAt": "2026-10-03T05:00:00Z"
    }
  ]
}
```

The case code is the only credential the reporter receives. It must be kept private.

### Track a report

`GET /api/reports/{caseCode}`

No account or login is required.

A malformed code returns `400 Bad Request`. A correctly formatted but unknown code returns `404 Not Found`.

### Moderator login

`POST /api/auth/login`

```json
{
  "username": "moderator",
  "password": "your-password"
}
```

Response:

```json
{
  "token": "eyJ...",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```

Use the token in moderator requests:

```text
Authorization: Bearer <token>
```

### List reports

`GET /api/moderator/reports`

Optional filters:

```text
GET /api/moderator/reports?category=SECURITY
GET /api/moderator/reports?status=UNDER_REVIEW
GET /api/moderator/reports?category=SECURITY&status=UNDER_REVIEW
```

### Update status

`PATCH /api/moderator/reports/{id}/status`

```json
{
  "status": "UNDER_REVIEW",
  "message": "The report is being reviewed by the moderation team."
}
```

Valid workflow:

```text
SUBMITTED -> UNDER_REVIEW -> RESOLVED
                              -> DISMISSED
```

Invalid transitions return `400 Bad Request`.

## Privacy model

The report schema contains no reporter name, email, phone number, account ID, IP address or authentication identity. The API does not ask the reporter for identifying information.

The case code is generated with `SecureRandom` and contains 20 random characters from a 32-character alphabet. This gives 100 bits of randomness. The plaintext case code is returned only on report creation and is not stored in the database. The database stores only a SHA-256 hash of the case code.

Moderator responses use the internal report ID rather than exposing the case code. Reporter responses never expose the internal database ID.

Application logs should also be configured so request bodies, case codes and authorization headers are never logged.

## Security

Moderator endpoints require a signed JWT. Sessions are stateless. CSRF is disabled because this is a stateless bearer-token API. The JWT secret must be a strong secret of at least 32 bytes in production.

The default moderator credentials in `application.properties` are development defaults and must be replaced through environment variables before deployment.

For a production deployment, use HTTPS, a managed secret store, database encryption/access controls, rate limiting, restricted application logs, backups with appropriate access controls, and a reverse proxy/WAF.

## Assumptions

- A single moderator credential is sufficient for the assignment. A production system should use a moderator table with individually managed accounts and roles.
- Evidence is represented by an optional URL. File uploads are not implemented in the base version.
- Reports cannot move backward in the workflow.
- A resolved or dismissed case is terminal.
- The reporter must retain the case code because there is no recovery mechanism that can identify the reporter.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for development. Production should use database migrations such as Flyway.
