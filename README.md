<h1 align="center">INVOX</h1>

<p align="center">
  <i>A desktop invoicing & inventory platform built in Java + JavaFX.</i><br>
  Log in as a company, manage products and clients and issue invoices that keep stock in sync — backed by either in-memory storage or PostgreSQL.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/JavaFX-UI-1E90FF" alt="JavaFX">
  <img src="https://img.shields.io/badge/PostgreSQL-JDBC-336791?logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Maven-build-C71A36?logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/patterns-Singleton%20%7C%20Factory%20%7C%20Builder-success" alt="Patterns">
</p>

---

## Overview

**INVOX** is an invoicing and inventory app. A company account manages products,
clients (companies *or* individuals) and invoices. Issuing an invoice decreases stock atomically;
cancelling one restores it. The application runs fully **in memory** for instant demos, or persists
to **PostgreSQL** via JDBC — the same business logic, no code changes. Every action is written to an
audit log.


## Features

| Area        | Capabilities                                                                         |
|-------------|--------------------------------------------------------------------------------------|
| **Auth**    | Register / log in as a company account (hashed passwords)                            |
| **Products**| Create, edit, delete · dynamic categories                                            |
| **Clients** | Companies & individuals · create, view, edit, delete                                 |
| **Invoices**| Multi-line issuing · realistic invoice view (supplier / client / lines / totals) · mark paid · cancel (restores stock) · delete |
| **Stock**   | Automatic adjustment on issue / cancel                                               |
| **Account** | Edit company details · change password                                               |
| **Audit**   | `audit.csv` log (`action_name, timestamp`)                                           |

## Tech stack

| Layer        | Technology              |
|--------------|-------------------------|
| Language     | Java 17+                |
| UI           | JavaFX                  |
| Persistence  | PostgreSQL + JDBC       |
| Build        | Maven                   |

## Architecture

```mermaid
flowchart LR
    UI[JavaFX UI] --> SVC[Service layer]
    SVC --> REPO{{Repository interface}}
    REPO --> MEM[In-memory<br/>collections]
    REPO --> JDBC[JDBC<br/>PostgreSQL]
```

Services talk only to repository **interfaces**. A single `USE_DATABASE` flag in `InvoxApp` selects
the backend, so identical logic runs on volatile in-memory data or a real database.

## Design patterns

| Pattern    | Where                                                      | Why                                              |
|------------|------------------------------------------------------------|--------------------------------------------------|
| Singleton  | `AuditService`, `database.Database`, `database.GenericDao` | One shared connection source / audit sink        |
| Factory    | `patterns.ClientFactory`                                   | Builds the right `Client` subtype (company / individual) |
| Builder    | `patterns.InvoiceBuilder`                                  | Assembles a multi-line invoice and computes totals |

## Getting started

**Prerequisites:** JDK 17+ and Maven.

```bash
mvn clean compile javafx:run
```

Runs in memory by default — no database required. Demo account: **`demo` / `demo`**.

<details>
<summary><b>Run with PostgreSQL</b></summary>

```bash
psql -U postgres -d invox -f src/main/resources/db/schema.sql
psql -U postgres -d invox -f src/main/resources/db/seed.sql   # optional sample data
```

Edit `src/main/resources/db.properties`, then set in `ui/InvoxApp.java`:

```java
private static final boolean USE_DATABASE = true;
```

> The flag switches **all** repositories to JDBC at once — don't mix backends.
</details>

## Project structure

```
src/main/java/invox/
├── model/        domain classes (Product, Client, Invoice, User, ...)
├── repository/   data access — in-memory & JDBC implementations
├── service/      application logic (CRUD, invoicing, audit, auth)
├── exception/    custom exception hierarchy
├── database/     connection + generic DAO (Singleton)
├── patterns/     ClientFactory (Factory), InvoiceBuilder (Builder)
└── ui/           JavaFX interface
src/main/resources/
├── db/           schema.sql
├── styles.css    visual theme
└── db.properties connection config
```
