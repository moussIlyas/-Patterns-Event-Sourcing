# mouss.com - Event Sourcing and CQRS with Spring Boot and Axon

A Spring Boot 3 application that implements an **account management system** using **Event Sourcing** and **CQRS** with [Axon Framework](https://www.axoniq.io/). Commands and events are handled on the write side, while the read side maintains a projected PostgreSQL model and exposes real-time Server-Sent Events.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Run the Application](#run-the-application)
- [API Overview](#api-overview)
- [Request Examples](#request-examples)
- [Screenshots](#screenshots)
- [Diagrams](#diagrams)
- [Notes](#notes)

## Overview

The application manages bank-style accounts:

- Create an account with an initial balance and currency.
- Credit and debit an account.
- Update an account status (`CREATED`, `ACTIVATED`, `SUSPENDED`, `BLOCKED`).
- Query all accounts.
- Get a full account statement with operation history.
- Read the full event history stored in Axon Server.
- Subscribe to account operations in real time with Server-Sent Events (SSE).

The command side stores domain events in Axon Server. The query side listens to those events, projects them into PostgreSQL, and serves fast read models.

## Architecture

```mermaid
flowchart LR
    Client["API client<br/>Swagger UI / IntelliJ HTTP Client"]

    subgraph App["mouss.com - Spring Boot application"]
        subgraph CommandSide["Command side - CQRS write model"]
            CC["AccountCommandController<br/>/commands/accounts"]
            CG["Axon CommandGateway"]
            AGG["AccountAggregate<br/>business invariants"]
            CC --> CG --> AGG
        end

        subgraph QuerySide["Query side - CQRS read model"]
            QC["AccountQueryController<br/>/query/accounts"]
            QG["Axon QueryGateway"]
            QH["AccountQueryHandler"]
            AR["AccountRepository"]
            OR["OperationRepository"]
            QC --> QG --> QH
            QH --> AR
            QH --> OR
        end

        EH["AccountEventHandler<br/>read-model projector"]
        QE["QueryUpdateEmitter<br/>SSE updates"]
    end

    AX[("Axon Server<br/>event store + message routing<br/>ports 8024 / 8124")]
    DB[("PostgreSQL<br/>read model<br/>port 5432")]

    Client -->|"POST / PUT commands"| CC
    Client -->|"GET queries"| QC
    AGG -->|"domain events"| AX
    CC -->|"GET /events/{accountId}"| AX
    AX -->|"event publication"| EH
    EH --> AR
    EH --> OR
    AR --> DB
    OR --> DB
    EH -->|"operation update"| QE
    QE -->|"GET /watch/{accountId}<br/>text/event-stream"| QC
```

## Technology Stack

| Layer | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.4.3 |
| CQRS / Event Sourcing | Axon Framework 4.10.3 |
| Event store / command bus | Axon Server |
| Read database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| API documentation | Springdoc OpenAPI / Swagger UI |
| Build tool | Maven |
| Containerization | Docker Compose |
| Testing helper | IntelliJ HTTP Client (`.http`) |

## Project Structure

```text
.
├── compose.yaml
├── pom.xml
├── src
│   ├── main
│   │   ├── java/com/mouss
│   │   │   ├── commands
│   │   │   │   ├── aggregates
│   │   │   │   ├── commands
│   │   │   │   ├── controllers
│   │   │   │   └── dto
│   │   │   ├── enums
│   │   │   ├── events
│   │   │   └── query
│   │   │       ├── controllers
│   │   │       ├── dto
│   │   │       ├── entities
│   │   │       ├── handlers
│   │   │       ├── queries
│   │   │       └── repository
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java/com/mouss
├── docs/diagrams
└── images
```

## Prerequisites

- Java 21
- Maven (or the included Maven Wrapper)
- Docker and Docker Compose

## Run the Application

Start the required infrastructure:

```bash
docker compose up -d axonserver postgres
```

Start the Spring Boot application:

```bash
./mvnw spring-boot:run
```

The application is available at:

- Application: `http://localhost:8066`
- Swagger UI: `http://localhost:8066/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8066/v3/api-docs`

## API Overview

Base path for commands: `/commands/accounts`

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/add` | Create an account |
| `POST` | `/credit` | Credit an account |
| `POST` | `/debit` | Debit an account |
| `PUT` | `/updateStatus` | Update account status |
| `GET` | `/events/{accountId}` | Read event history from Axon Server |

Base path for queries: `/query/accounts`

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/all` | List all accounts |
| `GET` | `/accountsStatement/{accountId}` | Get account statement |
| `GET` | `/watch/{accountId}` | Subscribe to account operations (SSE) |

## Request Examples

Create an account:

```json
{
  "initialBalance": 1000.0,
  "currency": "MAD"
}
```

Credit an account:

```json
{
  "accountId": "generated-account-id",
  "amount": 250.0,
  "currency": "MAD"
}
```

Debit an account:

```json
{
  "accountId": "generated-account-id",
  "amount": 100.0,
  "currency": "MAD"
}
```

Update account status:

```json
{
  "accountId": "generated-account-id",
  "status": "SUSPENDED"
}
```

The complete executable request collection is available in:

- `images/accounts-api.http`

## Screenshots

### Swagger UI

![Swagger UI overview](images/01-swagger-ui-overview.png)

![Swagger endpoints expanded](images/02-swagger-endpoints-expanded.png)

### Command and query tests

![Create account](images/03-create-account.png)

![Get all accounts](images/04-get-all-accounts.png)

![Credit account](images/05-credit-account.png)

![Debit account](images/06-debit-account.png)

![Account statement](images/07-account-statement.png)

![Update account status](images/08-update-account-status.png)

![Account after status update](images/09-account-after-status-update.png)

![Event history](images/10-event-history.png)

### Real-time SSE test

![Watch account events](images/11-watch-events-sse.png)

## Diagrams

### Architecture

![Architecture diagram](images/diagrams/architecture.png)

Source: `docs/diagrams/architecture.mmd`

### Command class diagram

![Command class diagram](images/diagrams/command-class-diagram.png)

Source: `docs/diagrams/command-class-diagram.mmd`

### Query class diagram

![Query class diagram](images/diagrams/query-class-diagram.png)

Source: `docs/diagrams/query-class-diagram.mmd`

### Command sequence

![Command sequence diagram](images/diagrams/command-sequence.png)

Source: `docs/diagrams/command-sequence.mmd`

### Query sequence

![Query sequence diagram](images/diagrams/query-sequence.png)

Source: `docs/diagrams/query-sequence.mmd`

### Account state

![Account state diagram](images/diagrams/account-state.png)

Source: `docs/diagrams/account-state.mmd`

### Read model

![Read model diagram](images/diagrams/read-model.png)

Source: `docs/diagrams/read-model.mmd`

## Notes

- The project package is `com.mouss`.
- Axon Server stores and routes commands, events, and queries.
- PostgreSQL stores the query-side read model.
- The SSE endpoint keeps the HTTP connection open and emits an `AccountOperation` after a credit or debit operation.
- No commits were created during this work; all generated files are currently untracked and ready to be staged or committed.

## Author

moussIlyas