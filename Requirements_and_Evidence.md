# Requirements & Evidence

## Notification & Job Processing Service

This document provides a structured mapping between the project requirements,
implemented functionality, validation activities, and supporting evidence.

It is maintained as an evaluation and submission document for the internship
project while the main application remains a standalone backend project.

---

# 1. Project Overview

## Project

Notification & Job Processing Service

## Objective

Build a secure backend service that accepts notification jobs, queues work,
processes jobs asynchronously, retries failed processing attempts, tracks job
status, and exposes operational metrics.

## Primary Stakeholder

The primary stakeholder is a backend/service operator or development team
responsible for reliable notification processing and operational monitoring.

## Scope

The project focuses on:

- Secure RESTful job APIs
- Authentication and role-based authorization
- Notification job creation and tracking
- RabbitMQ-based asynchronous processing
- Background job processing
- Retry and failure handling
- Input validation
- Structured exception handling
- Metrics
- Audit logging
- Automated testing
- Integration validation
- Technical documentation

## Out of Scope

The current implementation does not include:

- Actual email/SMS provider integration
- Frontend user interface
- Distributed microservices architecture
- Production cloud deployment
- Advanced monitoring platforms
- External notification provider APIs

The worker demonstrates the notification-processing workflow through the
backend processing pipeline and application logging rather than delivering
notifications through an external provider.

---

# 2. Technical Stack

| Area | Technology | Notes |
|---|---|---|
| Programming Language | Java | Backend implementation |
| Framework | Spring Boot | Application framework |
| Security | Spring Security + JWT | Authentication and authorization |
| ORM | Spring Data JPA / Hibernate | Persistence layer |
| Database | MySQL | Relational database |
| Message Broker | RabbitMQ | Asynchronous job processing |
| RabbitMQ Runtime | Docker | RabbitMQ runs in a Docker container |
| Testing | JUnit + Mockito | Automated unit testing |
| API Testing | Postman | Integration and API validation |
| Version Control | Git + GitHub | Source control |
| IDE | Eclipse | Development environment |

## Database Decision

The recommended technology stack included PostgreSQL. MySQL was selected as
the relational database alternative for this implementation.

The application uses JPA/Hibernate for persistence, keeping database access
largely independent of vendor-specific SQL.

This allowed the project to maintain a reliable relational persistence layer
while focusing on the core notification-processing workflow.

---

# 3. Functional Requirements Mapping

## 3.1 RESTful Endpoints

### Requirement

The service must expose RESTful endpoints for interacting with the backend.

### Implementation

Implemented REST endpoints include:

- `/api/auth/**`
- `/api/users/**`
- `/api/jobs`
- `/api/jobs/{id}`
- `/api/metrics/jobs`

### Validation

The endpoints were tested through Postman and automated tests.

### Evidence

- Job creation
- Job completion
- Validation responses
- Authorization response
- Authentication failure response
- Resource-not-found response
- Metrics response

### Status

**COMPLETED**

---

# 4. Authentication and Authorization

### Requirement

The application must provide authentication and authorization.

### Implementation

Implemented:

- JWT-based authentication
- BCrypt password hashing
- Stateless Spring Security
- USER and ADMIN roles
- Role-based authorization
- Invalid/expired JWT handling
- Custom access-denied handling

### Authorization Behavior

- Authentication endpoints are publicly accessible.
- Protected endpoints require authentication.
- Administrative user-management endpoints require ADMIN role.
- Unauthorized role access returns HTTP 403.
- Invalid or expired JWT returns HTTP 401.

### Evidence

`Testing Evidence/Integration Tests/07_Unauthorized_Role_403.png`

`Testing Evidence/Integration Tests/09_Invalid_JWT_401.png`

### Status

**COMPLETED**

---

# 5. Input Validation

### Requirement

The application must validate incoming request data.

### Implementation

`NotificationJobRequest` uses Bean Validation:

- Recipient cannot be blank.
- Message cannot be blank.

Invalid requests are rejected before job creation.

### Evidence

`Testing Evidence/Integration Tests/03_Invalid_Recipient_Validation.png`

`Testing Evidence/Integration Tests/04_Invalid_Message_Validation.png`

`Testing Evidence/Integration Tests/05_Both_Fields_Invalid_Validation.png`

### Status

**COMPLETED**

---

# 6. Structured Error Handling

### Requirement

The service must handle invalid requests, missing resources,
authentication failures, and authorization failures appropriately.

### Implementation

Implemented:

- Global exception handling
- `ResourceNotFoundException`
- HTTP 400 for validation errors
- HTTP 401 for invalid authentication
- HTTP 403 for insufficient authorization
- HTTP 404 for missing resources

### Evidence

`Testing Evidence/Integration Tests/07_Unauthorized_Role_403.png`

`Testing Evidence/Integration Tests/09_Invalid_JWT_401.png`

`Testing Evidence/Integration Tests/10_Job_Not_Found_404.png`

Validation evidence:

`03_Invalid_Recipient_Validation.png`

`04_Invalid_Message_Validation.png`

`05_Both_Fields_Invalid_Validation.png`

### Status

**COMPLETED**

---

# 7. Logging and Audit Events

### Requirement

The application should maintain meaningful logging and audit events.

### Implementation

An `AuditLog` entity and `AuditLogService` were implemented.

The application records events including:

- `USER_REGISTERED`
- `USER_LOGIN_SUCCESS`
- `USER_LOGIN_FAILED`
- `JOB_CREATED`
- `JOB_PROCESSING`
- `JOB_COMPLETED`
- `JOB_RETRYING`
- `JOB_FAILED`

Each audit record stores:

- Audit ID
- Job ID when applicable
- Username when applicable
- Event
- Timestamp

### Status

**COMPLETED**

---

# 8. Automated Testing

### Requirement

The project must include automated tests.

### Implementation

JUnit and Mockito tests cover the major service and worker workflows.

## UserServiceTest

Tests:

- User creation
- Password encoding
- Default USER role
- Username lookup

## NotificationJobServiceTest

Tests:

- Successful job lookup
- Missing job exception
- Job creation
- PENDING status
- Audit event generation
- Queue publishing

## NotificationJobWorkerTest

Tests:

- Successful job processing
- Retry after processing failure
- Permanent failure after maximum retries

### Test Result

- Tests executed: **8**
- Tests passed: **8**
- Failures: **0**
- Errors: **0**

### Evidence

`Testing Evidence/Automated Tests/01_All_Automated_Tests_Passed.png`

### Status

**COMPLETED**

---

# 9. Core Job Processing Workflow

The implemented workflow is:

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

The worker allows a maximum of three processing attempts.

---

# 10. Asynchronous Queue Processing

### Requirement

The system must queue work and process notification jobs asynchronously.

### Implementation

RabbitMQ was integrated using:

- Durable queue
- Direct exchange
- Routing key
- Queue binding
- `RabbitTemplate`
- `@RabbitListener`

### Messaging Configuration

Queue:

`notification.queue`

Exchange:

`notification.exchange`

Routing key:

`notification.created`

### Processing Flow

1. Client submits a notification job.
2. Job is stored with PENDING status.
3. Job is published to RabbitMQ.
4. Background worker receives the message.
5. Job status changes to PROCESSING.
6. Worker processes the notification.
7. Successful processing changes status to COMPLETED.
8. Processing failure triggers retry handling.
9. The job is eventually marked FAILED after the retry limit.

### Evidence

`Testing Evidence/Integration Tests/08_RabbitMQ_Queue_Activity.png`

### Status

**COMPLETED**

---

# 11. Retry and Failure Handling

### Requirement

Failed notification processing must be retried.

### Implementation

The worker maintains a `retryCount`.

Maximum attempts:

`3`

When processing fails:

1. Retry count is incremented.
2. Job status changes to RETRYING.
3. A retry audit event is recorded.
4. The job is requeued.

After the maximum number of attempts:

1. Job status changes to FAILED.
2. A failure audit event is recorded.
3. The job is not requeued again.

### Automated Validation

The worker tests verify:

- Successful processing
- Retry after processing failure
- Permanent failure after maximum retries

### Evidence

`Testing Evidence/Automated Tests/01_All_Automated_Tests_Passed.png`

### Status

**COMPLETED**

---

# 12. Job Status Tracking

The application tracks the following job states:

- `PENDING`
- `PROCESSING...`
- `RETRYING`
- `COMPLETED`
- `FAILED`

These states provide visibility into the lifecycle of each notification job.

### Evidence

`Testing Evidence/Integration Tests/01_Job_Created.png`

`Testing Evidence/Integration Tests/02_Job_Completed.png`

### Status

**COMPLETED**

---

# 13. Metrics

### Requirement

The service must provide status and operational metrics.

### Implementation

The `/api/metrics/jobs` endpoint exposes:

- Total jobs
- Completed jobs
- Failed jobs
- Retrying jobs

Example:

    {
        "totalJobs": 13,
        "completedJobs": 4,
        "failedJobs": 2,
        "retryingJobs": 1
    }

The individual status counters represent persisted jobs currently matching
those statuses. Other states such as PENDING or PROCESSING contribute to the
total job count but not to these individual counters.

### Evidence

`Testing Evidence/Integration Tests/06_Job_Metrics_Verification.png`

### Status

**COMPLETED**

---

# 14. Security and Data Integrity

The project applies security and data-integrity practices relevant to the
application.

Implemented controls include:

- JWT authentication
- BCrypt password hashing
- Stateless security
- Role-based authorization
- Invalid JWT handling
- Access-denied handling
- Input validation
- Password exclusion from user API responses
- Protected administrative endpoints
- Database-backed job state
- Audit event recording

User passwords are never returned through user response DTOs.

### Status

**COMPLETED**

---

# 15. Edge Cases and Error Scenarios

| Scenario | Expected Result | Evidence |
|---|---|---|
| Empty recipient | HTTP 400 | `03_Invalid_Recipient_Validation.png` |
| Empty message | HTTP 400 | `04_Invalid_Message_Validation.png` |
| Both fields empty | HTTP 400 | `05_Both_Fields_Invalid_Validation.png` |
| Invalid JWT | HTTP 401 | `09_Invalid_JWT_401.png` |
| Unauthorized role | HTTP 403 | `07_Unauthorized_Role_403.png` |
| Job does not exist | HTTP 404 | `10_Job_Not_Found_404.png` |
| Processing succeeds | COMPLETED | `02_Job_Completed.png` |
| Processing failure | RETRYING / FAILED | Automated worker tests |
| Maximum retries reached | FAILED | Automated worker tests |

### Status

**COMPLETED**

---

# 16. Validation and Review

The project was validated through both automated and manual integration
testing.

## Automated Validation

- 8 automated tests executed
- 8 tests passed
- 0 failures
- 0 errors

Evidence:

`Testing Evidence/Automated Tests/01_All_Automated_Tests_Passed.png`

## Integration Validation

The following workflows were validated using Postman:

- Job creation
- Job completion
- Input validation
- Metrics
- Authorization
- JWT authentication
- Resource-not-found handling

## Messaging Validation

RabbitMQ queue activity was verified through the RabbitMQ Management UI.

Evidence:

`Testing Evidence/Integration Tests/08_RabbitMQ_Queue_Activity.png`

### Status

**COMPLETED**

---

# 17. Testing Evidence Index

All evidence is maintained under:

`Testing Evidence/`

## Automated Tests

    Testing Evidence/
    └── Automated Tests/
        └── 01_All_Automated_Tests_Passed.png

## Integration Tests

    Testing Evidence/
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

# 18. Requirements-to-Evidence Summary

| Requirement | Implementation | Evidence | Status |
|---|---|---|---|
| RESTful endpoints | Spring Boot REST controllers | API integration evidence | COMPLETED |
| Authentication | JWT + Spring Security | `09_Invalid_JWT_401.png` | COMPLETED |
| Authorization | USER/ADMIN RBAC | `07_Unauthorized_Role_403.png` | COMPLETED |
| Input validation | Bean Validation | `03–05` validation evidence | COMPLETED |
| Structured errors | Global/security exception handling | `07`, `09`, `10` | COMPLETED |
| Logging/audit events | AuditLog + AuditLogService | Source implementation | COMPLETED |
| Automated tests | JUnit + Mockito | `01_All_Automated_Tests_Passed.png` | COMPLETED |
| Queue processing | RabbitMQ | `08_RabbitMQ_Queue_Activity.png` | COMPLETED |
| Retry handling | Worker retry mechanism | Automated worker tests | COMPLETED |
| Status tracking | Job status lifecycle | `01`, `02` | COMPLETED |
| Metrics | `/api/metrics/jobs` | `06_Job_Metrics_Verification.png` | COMPLETED |
| Version control | Git + GitHub | Repository history | COMPLETED |
| Security/privacy | JWT, BCrypt, RBAC, DTOs | Security tests/source | COMPLETED |
| Data integrity | JPA persistence + controlled job states | Integration tests | COMPLETED |

---

# 19. Professional Requirements

## Target Stakeholder

Backend/service operators and development teams responsible for reliable
notification processing and operational monitoring.

## Scope Boundaries

The project focuses on:

- Job acceptance
- Validation
- Persistence
- Queueing
- Background processing
- Retry handling
- Status tracking
- Metrics
- Security
- Auditability

External notification providers and frontend interfaces remain outside the
current scope.

## Success Metrics

The solution is considered successful when:

- A valid job can be accepted through a REST API.
- The job is persisted correctly.
- The job is placed on the RabbitMQ queue.
- The background worker processes the job.
- Successful processing produces COMPLETED status.
- Processing failures trigger retry handling.
- Jobs exceeding the retry limit become FAILED.
- Job status can be queried.
- Operational metrics can be retrieved.
- Unauthorized access is blocked.
- Invalid input is rejected.
- Automated tests pass.
- The workflow can be reproduced from the project documentation.

## Repeatable Workflow

    Request
       |
       v
    Validation
       |
       v
    Persistence
       |
       v
    Queue
       |
       v
    Background Processing
       |
       v
    Status Update
       |
       v
    Metrics / Audit Evidence

## Traceable Development

The project maintains traceability through:

- Git commits
- Organized source packages
- Testing Evidence folder
- README documentation
- This requirements and evidence document

---

# 20. Version Control

The project is maintained using Git and GitHub.

Major development milestones were committed separately, including:

- Project initialization
- JWT authentication and role-based authorization
- Notification job management
- API validation and exception handling
- RabbitMQ integration
- Background notification worker
- Retry and failure handling
- Job status tracking, metrics, and audit logging
- Automated tests and integration validation
- Testing and integration evidence
- README documentation

The Git history provides a traceable record of the development process.

---

# 21. Known Limitations

The current implementation has the following limitations:

1. The worker demonstrates notification processing but does not integrate
   with a real email, SMS, or push-notification provider.

2. Retry handling is implemented at the application level rather than using
   advanced RabbitMQ mechanisms such as delayed exchanges or dead-letter
   exchanges.

3. Metrics are application-level database counts rather than a complete
   production monitoring system.

4. MySQL is used as the relational database instead of PostgreSQL.

5. The project is designed as a focused, reviewable backend service rather
   than a production-scale distributed deployment.

---

# 22. Realistic Future Improvements

Potential improvements include:

- Integration with an actual email/SMS notification provider
- Dead-letter queue support
- Delayed retry scheduling
- Exponential backoff
- Redis-based caching
- Prometheus/Grafana monitoring
- Centralized structured logging
- Docker Compose for the complete application stack
- Production PostgreSQL deployment
- Cloud deployment
- Rate limiting
- Idempotency handling
- Distributed tracing
- Horizontal worker scaling

These improvements are intentionally outside the current core scope.

---

# 23. Deliverables Checklist

| Deliverable | Project Artifact | Status |
|---|---|---|
| Source files | GitHub repository | COMPLETED |
| Requirements documentation | `Requirements_and_Evidence.md` | COMPLETED |
| Implementation documentation | `README.md` | COMPLETED |
| Test/validation evidence | `Testing Evidence/` | COMPLETED |
| Technical/user guide | `README.md` | COMPLETED |
| Version-controlled repository | GitHub | COMPLETED |
| Final demonstration | Project demonstration | FINAL QA |
| Final project verification | Final QA checklist | FINAL QA |

---

# 24. Acceptance Criteria Mapping

## Core requirements completed and mapped to evidence

**Status: COMPLETED**

All major functional and technical requirements are mapped to implementation
details and supporting evidence in this document.

## Reproducible workflow

**Status: COMPLETED**

The source code, README, configuration instructions, API information, and
testing evidence provide the information required to understand and reproduce
the project workflow.

## Quality checks and validation

**Status: COMPLETED**

Automated tests, integration tests, validation scenarios, and known
limitations are documented.

## Professional organization

**Status: FINAL QA**

The repository is organized into source code, documentation, and a dedicated
Testing Evidence directory. Final repository verification and packaging will
be performed before submission.

## Technical decisions and trade-offs

**Status: COMPLETED**

The project documents its database selection, queue-processing approach,
retry mechanism, security design, scope boundaries, limitations, and future
improvements.

---

# 25. Final Project Readiness

The project currently demonstrates the complete core notification job
processing workflow:

    REST API
       |
       v
    Authentication / Authorization
       |
       v
    Input Validation
       |
       v
    Job Creation
       |
       v
    MySQL Persistence
       |
       v
    RabbitMQ Queue
       |
       v
    Background Worker
       |
       v
    Processing
       |
       v
    Retry / Failure Handling
       |
       v
    Status Tracking
       |
       v
    Metrics + Audit Logging
       |
       v
    Automated + Integration Validation

The implementation, testing evidence, documentation, and version history
together provide a structured and reviewable project package.

Final submission readiness will be confirmed through final QA, repository
verification, and project demonstration checks.

---

# 26. Conclusion

The Notification & Job Processing Service fulfills the core functional,
technical, security, validation, documentation, and evidence requirements
defined for the project.

The implementation demonstrates practical backend engineering concepts
including:

- REST API development
- JWT authentication
- Role-based authorization
- Asynchronous message processing
- RabbitMQ integration
- Background workers
- Retry handling
- Relational persistence
- Input validation
- Exception handling
- Metrics
- Audit logging
- Automated testing
- Integration testing

The project is intentionally scoped as a focused and reviewable backend
service while documenting realistic improvements for future production use.
