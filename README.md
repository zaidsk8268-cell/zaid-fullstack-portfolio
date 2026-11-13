# Zaid Khan — Full-Stack Portfolio

A responsive portfolio backed by Spring Boot. The frontend loads portfolio data from a REST API, and the contact form validates and saves messages to a persistent H2 database.

## Run the project

Requirements: Java 17 or newer. Maven does not need to be installed because the project includes Maven Wrapper.

```bash
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080).

On Windows, run:

```bat
mvnw.cmd spring-boot:run
```

## Test

```bash
./mvnw test
```

## API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/health` | Checks that the backend is online |
| `GET` | `/api/portfolio` | Returns skills, statistics and projects |
| `POST` | `/api/messages` | Validates and stores a contact message |

Example message request:

```json
{
  "name": "Aisha Khan",
  "email": "aisha@example.com",
  "subject": "Project enquiry",
  "message": "I would like to discuss a project with you."
}
```

## Database

Contact messages are stored under `data/portfolio-db*`. The local H2 console is available at [http://localhost:8080/h2-console](http://localhost:8080/h2-console) with:

- JDBC URL: `jdbc:h2:file:./data/portfolio-db`
- User: `sa`
- Password: leave blank

Set `H2_CONSOLE_ENABLED=false` when deploying publicly.
