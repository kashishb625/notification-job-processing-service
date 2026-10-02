# Notification & Job Processing Service

A secure backend service for accepting, queueing, processing, and monitoring notification jobs with authentication, authorization, retry handling, and job status tracking.

---

## 📌 Project Overview

The **Notification & Job Processing Service** is a backend application designed to process notification jobs asynchronously.

Instead of processing every notification request directly inside the API request, the service stores the job, places it into a message queue, and allows a background worker to process it independently.

The system demonstrates real-world backend concepts such as:

- RESTful API development
- JWT-based authentication
- Role-based authorization
- Asynchronous job processing
- Message queuing
- Retry and failure handling
- Job status tracking
- Validation and exception handling
- Logging and audit events
- Automated testing
- Database persistence

---

## 🎯 Problem Statement

Synchronous notification processing can make API requests slow and can make failure handling difficult.

This project addresses the problem by separating **job creation** from **job processing**.

A client creates a notification job through the REST API. The job is stored in the database and sent to a message queue. A background worker consumes the queued job and processes it independently.

Failed jobs can be retried automatically, and jobs that exceed the maximum retry limit are marked as permanently failed.

---

## 🏗️ Architecture

```text
                    ┌─────────────────┐
                    │     Client      │
                    │    / Postman    │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │    REST API     │
                    │   Spring Boot   │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   Job Service   │
                    └──────┬─────┬────┘
                           │     │
                    Save   │     │ Send
                           ▼     ▼
                    ┌────────┐ ┌───────────┐
                    │  MySQL │ │ RabbitMQ  │
                    └────────┘ └─────┬─────┘
                                     │
                                     ▼
                              ┌─────────────┐
                              │   Worker    │
                              └──────┬──────┘
                                     │
                              ┌──────┴──────┐
                              │             │
                           Success       Failure
                              │             │
                              ▼             ▼
                         COMPLETED       RETRY
                                            │
                                    Retry limit reached?
                                      │            │
                                     No           Yes
                                      │            │
                                      ▼            ▼
                                   PROCESS   PERMANENTLY_FAILED
```

---

## 🔄 Job Processing Flow

```text
CREATE JOB
    ↓
PENDING
    ↓
QUEUED
    ↓
PROCESSING
   ↙     ↘
SUCCESS  FAILURE
           ↓
         RETRY
           ↓
     Retry limit reached?
       ↙           ↘
     NO             YES
      ↓              ↓
   PROCESS       PERMANENTLY_FAILED
```

---

## ✨ Core Features

### 1. User Authentication

- User registration
- Secure password hashing using BCrypt
- User login
- JWT token generation
- JWT token validation

### 2. Role-Based Authorization

The system will support two roles:

- `USER`
- `ADMIN`

Users will have access to their permitted operations, while administrators will have access to administrative operations.

### 3. Notification Job Management

The API will allow clients to:

- Create notification jobs
- View job details
- View job status
- Track processing results
- Handle failed jobs

### 4. Message Queue

**RabbitMQ** will be used to decouple job creation from job processing.

```text
REST API → Database → RabbitMQ → Worker → Processing
```

### 5. Background Processing

A background worker will consume notification jobs from the queue and process them asynchronously.

### 6. Retry Mechanism

When a job fails:

```text
Processing Failure
       ↓
    Retry Job
       ↓
Retry Limit Reached?
   ↓            ↓
 No            Yes
 ↓              ↓
Retry        Permanent Failure
```

The maximum retry count will be configurable.

### 7. Job Status Tracking

Jobs will maintain their processing state.

Planned statuses include:

- `PENDING`
- `QUEUED`
- `PROCESSING`
- `COMPLETED`
- `RETRYING`
- `FAILED`
- `PERMANENTLY_FAILED`

### 8. Validation & Error Handling

The API will include:

- Request validation
- Meaningful validation messages
- Structured error responses
- Global exception handling
- Appropriate HTTP status codes

### 9. Logging & Audit Events

Important job lifecycle events will be recorded, such as:

- `JOB_CREATED`
- `JOB_QUEUED`
- `JOB_PROCESSING`
- `JOB_SUCCESS`
- `JOB_FAILED`
- `JOB_RETRY`
- `JOB_PERMANENTLY_FAILED`

### 10. Metrics

The service will expose basic processing metrics such as:

- Total jobs
- Pending jobs
- Processing jobs
- Successful jobs
- Failed jobs
- Permanently failed jobs
- Retry count

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Backend framework |
| Spring Security | Authentication & authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database persistence |
| Hibernate | ORM |
| MySQL | Relational database |
| RabbitMQ | Message queue |
| Maven | Build & dependency management |
| Postman | API testing |
| JUnit | Automated testing |
| Docker | Containerization |

---

## 🗄️ Database

The project currently uses **MySQL** for persistence.

The main data expected to be stored includes:

- Users
- Notification Jobs
- Job processing information
- Retry information
- Audit information

> **Note:** PostgreSQL is recommended in the original project specification, but MySQL is being used as a documented alternative for this implementation.

---

## 🔐 Security

The application will use:

- BCrypt password hashing
- JWT authentication
- Role-based authorization
- Protected API endpoints
- Environment variables for sensitive configuration

Sensitive information such as:

- Database passwords
- JWT secrets
- Other credentials

will not be committed to the repository.

---

## 🔗 API Overview

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Notification Jobs

```text
POST /api/jobs
GET  /api/jobs/{id}
GET  /api/jobs
```

Additional endpoints may be added as the project develops.

---

## 🧪 Testing Strategy

The project will include automated and API-level testing for:

### Authentication

- Successful registration
- Successful login
- Invalid credentials
- Invalid JWT
- Expired JWT

### Job Processing

- Job creation
- Successful processing
- Failed processing
- Retry behavior
- Maximum retry handling
- Permanent failure

### Authorization

- USER access
- ADMIN access
- Unauthorized requests
- Forbidden requests

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/Kashish/notification_job_service/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
```

The package structure may evolve as additional components such as messaging, workers, retry handling, metrics, and audit logging are implemented.

---

## 🗺️ Development Roadmap

### Project Foundation

- [x] Project initialization
- [x] MySQL database configuration
- [x] User entity and repository
- [x] Password encryption
- [x] User registration
- [x] Basic login
- [x] JWT foundation

### Security

- [ ] JWT authentication filter
- [ ] Role-based authorization
- [ ] USER/ADMIN access control

### Job Management

- [ ] Notification Job entity
- [ ] Job DTOs
- [ ] Job repository
- [ ] Job service
- [ ] Job REST APIs
- [ ] Request validation
- [ ] Global exception handling

### Asynchronous Processing

- [ ] RabbitMQ integration
- [ ] Job producer
- [ ] Background worker
- [ ] Notification processing

### Reliability

- [ ] Retry mechanism
- [ ] Failure handling
- [ ] Maximum retry handling
- [ ] Permanent failure handling

### Monitoring

- [ ] Job status tracking
- [ ] Metrics
- [ ] Audit logging
- [ ] Processing logs

### Testing & Deployment

- [ ] Automated tests
- [ ] Integration testing
- [ ] Bug fixing
- [ ] Docker setup
- [ ] Final documentation
- [ ] Final QA
- [ ] Demo preparation

---

## 🚀 Future Improvements

Potential future improvements include:

- Email/SMS notification providers
- Scheduled notifications
- Dead-letter queues
- Advanced monitoring
- Distributed job processing
- Rate limiting
- Notification templates
- Advanced metrics and dashboards

---

## 📌 Project Status

**Current Status:** `In Development`

The project is being developed incrementally with a focus on secure backend architecture, asynchronous processing, reliability, and production-oriented backend practices.

---

## 👨‍💻 Author

**Kashish Bhatnagar**

Java Backend Developer | Spring Boot | Spring Security | REST APIs | MySQL
