# Notification & Job Processing Service

A secure Java Spring Boot backend service for accepting, queueing, processing, and monitoring notification jobs using REST APIs, JWT authentication, RabbitMQ, retry handling, job status tracking, metrics, and audit logging.

---

## 📌 Project Overview

The **Notification & Job Processing Service** is a backend application designed to process notification jobs asynchronously.

Instead of processing every notification request directly within the API request, the service stores the job in the database, publishes it to a RabbitMQ queue, and allows a background worker to process it independently.

The project demonstrates practical backend engineering concepts including:

- RESTful API development
- JWT-based authentication
- Role-based authorization
- Secure password handling using BCrypt
- Asynchronous job processing
- RabbitMQ message queuing
- Retry and failure handling
- Job status tracking
- Request validation
- Global exception handling
- Structured HTTP error responses
- Audit logging
- Processing metrics
- Automated unit testing
- Integration testing
- Database persistence

---

## 🎯 Problem Statement

Synchronous notification processing can make API requests slower and can make failure handling difficult.

This project addresses this problem by separating **job creation** from **job processing**.

A client creates a notification job through the REST API. The job is stored in MySQL and published to RabbitMQ. A background worker consumes the queued job and processes it independently.

If processing fails, the job can be retried. After reaching the maximum retry limit, the job is marked as failed.

This architecture demonstrates a basic asynchronous job-processing workflow suitable for backend systems that need reliable background processing.

---

## 🏗️ Architecture

    Client / Postman
           |
           v
      REST API
     Spring Boot
           |
           v
      Job Service
        /     \
       /       \
      v         v
    MySQL    RabbitMQ
                |
                v
             Worker
                |
          +-----+-----+
          |           |
       Success     Failure
          |           |
          v           v
      COMPLETED    RETRYING
                      |
              Retry limit reached
                      |
                      v
                    FAILED

---

## 🔄 Job Processing Flow

                          CREATE JOB
                             |
                             v
                          PENDING
                             |
                             v
                      RabbitMQ Queue
                             |
                             v
                        PROCESSING
                       /          \
                      /            \
                 SUCCESS          FAILURE
                    |                |
                    v                v
                COMPLETED         RETRYING
                                      |
                                      v
                            Retry Limit Reached?
                              /              \
                             /                \
                           NO                  YES
                           |                    |
                           v                    v
                         RETRY                FAILED
---

## ✨ Core Features

### 1. User Authentication

The application provides secure user authentication using JWT.

Implemented features:

- User registration
- Secure password hashing using BCrypt
- User login
- JWT token generation
- JWT token validation
- Invalid token handling
- Authentication audit events

### 2. Role-Based Authorization

The application supports two roles:

- `USER`
- `ADMIN`

Administrative endpoints are protected using Spring Security role-based authorization.

Implemented authorization behavior includes:

- Protected API endpoints
- ADMIN-only user management endpoints
- USER/ADMIN access control
- Meaningful `403 Forbidden` responses
- Stateless authentication using JWT

### 3. Notification Job Management

The service provides REST APIs for notification job management.

Implemented operations:

- Create notification jobs
- Retrieve a specific job
- Retrieve all jobs
- Track job processing status
- Track retry count
- Handle missing job resources

Each job contains:

- Job ID
- Recipient
- Message
- Status
- Retry count

### 4. Message Queue

**RabbitMQ** is used to decouple job creation from job processing.

Basic workflow:

    REST API → MySQL → RabbitMQ → Background Worker → Notification Processing

RabbitMQ provides asynchronous communication between the API layer and the background worker.

### 5. Background Processing

A RabbitMQ listener consumes notification jobs from the configured queue.

The worker:

1. Receives the queued job.
2. Retrieves the corresponding job from the database.
3. Updates the job status to `PROCESSING...`.
4. Processes the notification.
5. Updates the final status.
6. Records the corresponding audit event.

### 6. Retry and Failure Handling

The worker implements retry handling for failed processing attempts.

The current maximum retry limit is **3 attempts**.

Processing flow:

                       Processing Failure
                           |
                           v
                  Increment Retry Count
                           |
                           v
                  Retry Limit Reached?
                     /           \
                    /             \
                  No               Yes
                  |                 |
                  v                 v
              RETRYING           FAILED
                  |
                  v
              Requeue Job

The retry mechanism and failure behavior are covered by automated worker tests.

### 7. Job Status Tracking

Jobs maintain their processing state throughout their lifecycle.

Implemented statuses include:

- `PENDING`
- `PROCESSING...`
- `COMPLETED`
- `RETRYING`
- `FAILED`

The status and retry count are persisted in the database.

### 8. Validation and Error Handling

The API implements request validation and centralized exception handling.

Implemented features include:

- Request body validation using Jakarta Bean Validation
- Required field validation
- Meaningful validation messages
- Global exception handling
- Resource-not-found handling
- Appropriate HTTP status codes
- Structured authentication and authorization error responses

Validated HTTP responses include:

- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`

### 9. Logging and Audit Events

Important application and job lifecycle events are recorded through an audit logging mechanism.

Implemented audit events include:

- `USER_REGISTERED`
- `USER_LOGIN_SUCCESS`
- `USER_LOGIN_FAILED`
- `JOB_CREATED`
- `JOB_PROCESSING`
- `JOB_COMPLETED`
- `JOB_RETRYING`
- `JOB_FAILED`

Audit records include:

- Event
- Job ID where applicable
- Username where applicable
- Timestamp

### 10. Processing Metrics

The service exposes basic job-processing metrics through a REST endpoint.

Available metrics include:

- Total jobs
- Completed jobs
- Failed jobs
- Retrying jobs

Endpoint:

`GET /api/metrics/jobs`

Example response:

    {
      "totalJobs": 13,
      "completedJobs": 4,
      "failedJobs": 2,
      "retryingJobs": 1
    }

The remaining jobs may have other statuses such as `PENDING` or `PROCESSING...`.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Backend framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| BCrypt | Password hashing |
| Spring Data JPA | Database persistence |
| Hibernate | ORM |
| MySQL | Relational database |
| RabbitMQ | Message queue |
| Maven | Build and dependency management |
| Postman | API and integration testing |
| JUnit | Automated testing |
| Mockito | Unit testing and mocking |
| Docker | Containerized infrastructure |

---

## 🗄️ Database

The application currently uses **MySQL** for persistent data storage.

The database stores information related to:

- Users
- Notification jobs
- Retry information
- Audit events

### Database Configuration

Sensitive configuration such as database passwords and JWT secrets should be supplied through environment/configuration mechanisms rather than committed directly to source control.

> **Note:** PostgreSQL was specified as the preferred database in the original project requirements. MySQL is used in this implementation as a documented alternative.

---

## 🔐 Security

Security is implemented using **Spring Security and JWT**.

Security features include:

- BCrypt password hashing
- JWT-based authentication
- Stateless session management
- Role-based authorization
- Protected REST endpoints
- ADMIN-only user management
- Invalid token handling
- Custom `401 Unauthorized` responses
- Custom `403 Forbidden` responses
- Sensitive configuration protection

Sensitive information such as:

- Database passwords
- JWT secrets
- Credentials

should not be committed to the repository.

---

## 🔗 API Overview

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`

### User Management

- `GET /api/users`
- `GET /api/users/{id}`

> User management endpoints are restricted to `ADMIN` users.

### Notification Jobs

- `POST /api/jobs`
- `GET /api/jobs`
- `GET /api/jobs/{id}`

### Metrics

- `GET /api/metrics/jobs`

---

## 📋 Example Job Request

    {
      "recipient": "user@example.com",
      "message": "Your notification message"
    }

### Example Job Response

    {
      "id": 13,
      "recipient": "user@example.com",
      "message": "Your notification message",
      "status": "COMPLETED",
      "retryCount": 0
    }

---

## 🧪 Testing and Validation

The project includes both **automated testing** and **API-level integration testing**.

### Automated Tests

The automated test suite covers:

- User service behavior
- Password encoding
- User lookup
- Notification job creation
- Job retrieval
- Missing job handling
- Successful job processing
- Retry behavior
- Maximum retry/failure handling

Current automated test result:

    Tests Run: 8
    Failures: 0
    Errors: 0

### Integration Testing

The API was validated using Postman and the running Spring Boot application.

Integration testing covered:

- Job creation
- Successful job completion
- Request validation
- Invalid JWT handling
- Unauthorized role access
- Missing job handling
- Job metrics
- RabbitMQ queue activity

### HTTP Response Validation

| Scenario | Expected Response |
|---|---|
| Valid job creation | `201 Created` |
| Successful job retrieval | `200 OK` |
| Invalid request data | `400 Bad Request` |
| Invalid authentication token | `401 Unauthorized` |
| Unauthorized role | `403 Forbidden` |
| Job not found | `404 Not Found` |

### Testing Evidence

Testing evidence is maintained in:

    Testing Evidence/
    ├── Automated Tests/
    │   └── 01_All_Automated_Tests_Passed.png
    │
    └── Integration Tests/
        ├── 01_Job_Created.png
        ├── 02_Job_Completed.png
        ├── 03_Invalid_Recipient_Validation.png
        ├── 04_Invalid_Message_Validation.png
        ├── 05_Both_Fields_Invalid_Validation.png
        ├── 06_Job_Metrics_Verification.png
        ├── 07_Unauthorized_Role_403.png
        ├── 08_RabbitMQ_Queue_Activity.png
        ├── 09_Invalid_JWT_401.png
        └── 10_Job_Not_Found_404.png

---

## 📁 Project Structure

    notification-job-processing-service/
    │
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/Kashish/notification_job_service/
    │   │   │       ├── config/
    │   │   │       ├── controller/
    │   │   │       ├── dto/
    │   │   │       ├── entity/
    │   │   │       ├── exception/
    │   │   │       ├── repository/
    │   │   │       ├── security/
    │   │   │       └── service/
    │   │   │
    │   │   └── resources/
    │   │       └── application.properties
    │   │
    │   └── test/
    │       └── java/
    │
    ├── Testing Evidence/
    │   ├── Automated Tests/
    │   └── Integration Tests/
    │
    ├── pom.xml
    ├── README.md
    └── .gitignore

---

## 🔄 Development Milestones

### Project Foundation

- [x] Project initialization
- [x] MySQL database configuration
- [x] User entity and repository
- [x] Password encryption
- [x] User registration
- [x] User login
- [x] JWT authentication

### Security

- [x] JWT authentication filter
- [x] Role-based authorization
- [x] USER/ADMIN access control
- [x] Protected endpoints
- [x] Invalid token handling
- [x] Security error handling

### Job Management

- [x] Notification Job entity
- [x] Job DTO
- [x] Job repository
- [x] Job service
- [x] Job REST APIs
- [x] Request validation
- [x] Global exception handling
- [x] Resource-not-found handling

### Asynchronous Processing

- [x] RabbitMQ configuration
- [x] Job producer
- [x] Background worker
- [x] Notification processing

### Reliability

- [x] Retry mechanism
- [x] Failure handling
- [x] Maximum retry handling
- [x] Failed job status tracking

### Monitoring

- [x] Job status tracking
- [x] Processing metrics
- [x] Audit logging
- [x] Processing logs

### Testing

- [x] Automated unit tests
- [x] Worker retry tests
- [x] Failure handling tests
- [x] API validation testing
- [x] Authentication testing
- [x] Authorization testing
- [x] Integration testing
- [x] RabbitMQ validation
- [x] Metrics validation

### Finalization

- [ ] Docker deployment setup
- [ ] Requirements and evidence documentation
- [ ] Final QA
- [ ] Final GitHub verification
- [ ] Final project submission

---

## 📊 Requirements Coverage

The implementation addresses the major project requirements:

| Requirement | Implementation |
|---|---|
| RESTful endpoints | Spring Boot REST Controllers |
| Authentication | JWT + Spring Security |
| Authorization | USER/ADMIN RBAC |
| Input validation | Jakarta Bean Validation |
| Structured errors | Global exception handling |
| Job queue | RabbitMQ |
| Background processing | RabbitMQ Worker |
| Retry handling | Worker retry mechanism |
| Database persistence | MySQL + JPA/Hibernate |
| Job tracking | NotificationJob status |
| Metrics | `/api/metrics/jobs` |
| Audit logging | AuditLog entity/service |
| Automated tests | JUnit + Mockito |
| Integration validation | Postman |
| Version control | Git + GitHub |
| Infrastructure | Docker/RabbitMQ container |

---

## 🚀 Running the Project

### Prerequisites

Make sure the following are installed:

- Java
- Maven
- MySQL
- Docker
- RabbitMQ
- Postman

### Start RabbitMQ

RabbitMQ can be run using Docker.

    docker start notification-rabbitmq

RabbitMQ Management UI:

`http://localhost:15672`

### Start the Application

Run the Spring Boot application from Eclipse or using Maven:

    mvn spring-boot:run

The application will start on the configured server port.

### API Testing

Postman can be used to test the REST endpoints.

Authentication flow:

    Register User
          ↓
        Login
          ↓
      Receive JWT
          ↓
    Use JWT in Authorization Header
          ↓
    Access Protected APIs

---

## ⚠️ Scope and Limitations

This project focuses on demonstrating a reliable backend workflow for notification job processing.

Current limitations include:

- Notification delivery is simulated rather than connected to an external email/SMS provider.
- RabbitMQ is currently used as a single queue-based processing mechanism.
- Metrics are basic database-backed counters rather than a full monitoring platform.
- Retry processing uses a fixed maximum retry limit.
- The application does not currently implement a dedicated dead-letter queue.
- Docker deployment configuration is part of the final project deployment setup.

These limitations keep the project focused while providing a foundation for future enhancements.

---

## 🔮 Future Improvements

Potential future improvements include:

- Email/SMS notification provider integration
- Scheduled notifications
- Dead-letter queues
- Exponential backoff for retries
- Advanced monitoring and dashboards
- Prometheus/Grafana integration
- Rate limiting
- Notification templates
- Distributed worker processing
- Message prioritization
- Advanced job filtering and search

---

## 📌 Project Status

**Current Status:** `Finalization in Progress`

The core functionality of the project has been implemented and validated through automated and integration testing.

The remaining work focuses on:

- Deployment configuration
- Requirements/evidence documentation
- Final QA
- GitHub verification
- Final submission

---

## 👨‍💻 Author

**Kashish Bhatnagar**

Java Backend Developer | Spring Boot | Spring Security | REST APIs | MySQL
