# Backend Architecture Documentation

This document contains the UML diagrams for the Career Align Intelligence backend project.

## 1. High-Level Design (HLD) - Component Diagram

```mermaid
flowchart TD
    %% Define Nodes
    Client["🌐 Web Client (Frontend)"]
    
    subgraph Backend_App [Backend Application]
        direction TB
        Controllers["🔌 API Gateway / Controllers"]
        Services["⚙️ Business Logic / Services"]
        Consumers["👷 Async Workers"]
    end

    DB[("🐘 PostgreSQL DB")]
    Redis[("🚀 Redis Cache")]
    Kafka["📨 Kafka (Message Broker)"]
    Ollama["🧠 Ollama (AI Model)"]

    %% Define Relationships
    Client ---|REST API Calls| Controllers
    Controllers ---|DTOs| Services
    Services ---|CRUD| DB
    Services ---|Caching| Redis
    Services ---|Publish Job| Kafka
    
    Kafka ---|Consume Events| Consumers
    Consumers ---|AI Inference| Ollama
    Consumers ---|Update Results| DB

    %% Styling
    style Backend_App fill:#ffffff,stroke:#333,stroke-width:2px,color:#333
    style Client fill:#d1e9ff,stroke:#005cb2,color:#000
    style DB fill:#d0f0d0,stroke:#2b7a2b,color:#000
    style Redis fill:#ffe0b2,stroke:#e65100,color:#000
    style Kafka fill:#ebccff,stroke:#6a1b9a,color:#000
    style Ollama fill:#ffccdc,stroke:#ad1457,color:#000
    
    %% Internal nodes visibility
    style Controllers fill:#fff,stroke:#333,color:#000
    style Services fill:#fff,stroke:#333,color:#000
    style Consumers fill:#fff,stroke:#333,color:#000
```

## 2. High-Level Class Diagram

```mermaid
classDiagram
    class User {
        +Long id
        +String email
        +String password
        +int analysisCount
        +int generationCount
        +int usageLimit
        +Role role
    }

    class AnalysisResultEntity {
        +UUID id
        +String resumeText
        +String jdText
        +LocalDateTime createdAt
    }

    class AnalysisJob {
        +UUID id
        +Long userId
        +UUID resultId
        +JobStatus status
    }

    class UpgradeRequest {
        +Long id
        +User user
        +String planType
        +RequestStatus status
    }

    class ResumeExtractionResource {
        +analyzeResume(file, jdText)
        +trackGeneration()
    }

    class AtsServiceImpl {
        +analyzeResume(file, jdText)
        +extractResumeData(file)
        +trackSkillsGeneration()
    }

    class KafkaProducerService {
        +publishJobForResumeAnalysis(jobId)
    }

    User "1" -- "*" AnalysisJob : initiates
    AnalysisJob "1" -- "1" AnalysisResultEntity : processes
    User "1" -- "*" UpgradeRequest : requests
    ResumeExtractionResource --> AtsServiceImpl : uses
    AtsServiceImpl --> KafkaProducerService : uses
    AtsServiceImpl --> User : tracks usage
```

## 3. Low-Level Design (LLD) - Detailed Class Diagram (Analysis Module)

```mermaid
classDiagram
    namespace Controller {
        class ResumeExtractionResource {
            -AtsService atsService
            +analyseResume(MultipartFile file, String jdText) ResponseEntity
            +getAnalysisResult(UUID resultId) ResponseEntity
        }
    }

    namespace Service {
        class AtsService {
            <<interface>>
            +analyzeResume(MultipartFile, String) AIResponse
            +extractResumeData(MultipartFile) String
        }

        class AtsServiceImpl {
            -ResumeProcessingService resumeProcessingService
            -UserRepository userRepository
            -AnalysisResultRepository repository
            -AnalysisJobService analysisJobService
            -KafkaProducerService kafkaProducerService
            +analyzeResume(MultipartFile, String) AIResponse
        }

        class ResumeProcessingService {
            <<interface>>
            +extractResumeData(MultipartFile) String
        }

        class AnalysisJobService {
            +createJob(Long userId, UUID resultId) AnalysisJob
        }
    }

    namespace Repository {
        class AnalysisResultRepository {
            +save(AnalysisResultEntity)
            +findById(UUID)
        }
        class UserRepository {
            +findById(Long)
            +save(User)
        }
    }

    ResumeExtractionResource ..> AtsService
    AtsServiceImpl ..|> AtsService
    AtsServiceImpl --> ResumeProcessingService
    AtsServiceImpl --> AnalysisJobService
    AtsServiceImpl --> KafkaProducerService
    AtsServiceImpl --> UserRepository
    AtsServiceImpl --> AnalysisResultRepository
```

## 4. Sequence Diagram (Resume Analysis Flow)

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Controller as ResumeExtractionResource
    participant Service as AtsServiceImpl
    participant UserRepo as UserRepository
    participant ResumeService as ResumeProcessingService
    participant DB as Repository (Postgres)
    participant Kafka as KafkaProducerService
    
    User->>Controller: POST /api/analyze (File, JD)
    Controller->>Service: analyzeResume(file, jdText)
    
    Service->>UserRepo: findById(userId)
    UserRepo-->>Service: User Entity
    
    Service->>Service: Check Usage Limit
    alt Limit Reached
        Service-->>Controller: Throw BadRequestException
        Controller-->>User: 400 Bad Request
    end
    
    Service->>ResumeService: extractResumeData(file)
    ResumeService-->>Service: extractedText
    
    Service->>DB: Save AnalysisResultEntity (resumeText, jdText)
    DB-->>Service: Entity (ID: resultId)
    
    Service->>DB: Create AnalysisJob (userId, resultId)
    DB-->>Service: Job (ID: jobId)
    
    Service->>Kafka: publishJobForResumeAnalysis(jobId)
    Kafka--)Service: Acknowledge (Async)
    
    Service->>UserRepo: Increment Analysis Count
    Service-->>Controller: AIResponse (jobId)
    Controller-->>User: 200 OK (Job ID)
    
    note right of User: Client now polls for result using Job ID
```
