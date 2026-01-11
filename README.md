# Secure-Vault
🔐 Secure Vault – Secure File Storage & Versioning System

Secure Vault is a secure, scalable file storage backend system built with Spring Boot, featuring JWT authentication, role-based access, file versioning, soft delete & restore, credit-based storage upgrades, and AWS S3 integration.

Designed with clean architecture, production-ready security, and real-world SaaS patterns.

⸻

🚀 Features

🔑 Authentication & Security
	•	JWT-based authentication
	•	Refresh token mechanism
	•	Secure logout & token revocation
	•	Password reset (forgot/reset flow)
	•	Spring Security + custom filters

👤 User Management
	•	User registration & profile management
	•	Credit system
	•	Storage quota enforcement
	•	Profile update support

📁 File Management
	•	File upload with validation
	•	File versioning (automatic)
	•	Soft delete & restore
	•	Download support
	•	Per-user file isolation
	•	Latest version tracking

🗄 Storage System
	•	Pluggable storage architecture
	•	Local filesystem (dev)
	•	AWS S3 (production)
	•	Strategy pattern (FileStorageService)
	•	Profile-based switching (local / cloud)

💳 Credit & Storage Upgrade System
	•	Users start with default credits
	•	Spend credits to increase storage
	•	Enforced storage limits

📚 API Documentation
	•	OpenAPI 3 / Swagger UI integrated
	•	JWT security scheme configured

🛡 Global Error Handling
	•	Centralized exception handling
	•	Structured error responses
	•	Meaningful HTTP status codes

## 🛠 Tech Stack

| Layer | Technology |
|------|------------|
| Backend | Spring Boot 3.x |
| Security | Spring Security, JWT (jjwt) |
| Database | MongoDB |
| Storage | Local FS, AWS S3 |
| Authentication | JWT + Refresh Tokens |
| Build Tool | Maven |
| API Docs | Swagger / OpenAPI |
| Cloud | AWS S3 |
| Frontend | Next.js (Planned), React (Planned) |

## 📁 Project Structure

```text
secure-vault/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/vault/secure_vault/
│   │   │       ├── config/              # Security, Swagger, AWS, App configs
│   │   │       ├── controller/          # REST controllers (Auth, User, File)
│   │   │       ├── service/             # Business logic layer
│   │   │       ├── repository/          # MongoDB repositories
│   │   │       ├── model/               # MongoDB entities
│   │   │       ├── dto/                 # Request & Response DTOs
│   │   │       ├── security/            # JWT filter, UserDetailsService
│   │   │       ├── storage/
│   │   │       │   ├── local/            # Local file storage implementation
│   │   │       │   └── cloud/            # AWS S3 storage implementation
│   │   │       ├── exceptions/           # Custom exceptions & handlers
│   │   │       └── util/                 # Utility classes & constants
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-local.properties
│   │       └── application-cloud.properties
│   └── test/
│       └── java/
│           └── com/vault/secure_vault/   # Unit & integration tests
├── pom.xml
├── README.md
└── .gitignore




## 🔐 Authentication Flow

Secure Vault uses **JWT + Refresh Token** based authentication.

### Login Flow
1. User sends email & password to `/api/auth/login`
2. Spring Security authenticates credentials
3. System generates:
   - **Access Token (JWT)**
   - **Refresh Token (stored in DB)**
4. Tokens are returned to client

### Accessing Protected APIs
1. Client sends `Authorization: Bearer <accessToken>`
2. `JwtAuthenticationFilter` validates token
3. User is loaded from database
4. Request proceeds to controller

### Refresh Token Flow
1. Client sends refresh token to `/api/auth/refresh`
2. System validates refresh token from DB
3. New access token is issued

### Logout Flow
1. Refresh token is revoked in DB
2. Access token becomes useless after expiry

### Forgot / Reset Password Flow
1. User requests password reset → token generated & stored
2. Token expires in 15 minutes
3. User resets password using token
4. Token is marked as used




## 📂 File Upload & Versioning Flow

Secure Vault implements **automatic file versioning**.

### Upload Flow
1. User uploads file
2. System checks:
   - File size limit
   - User storage quota
   - Allowed file type
3. If file with same name exists:
   - Old version marked as `isLatest = false`
   - New version created with `version + 1`
4. File stored in:
   - Local FS or AWS S3 (based on config)
5. Metadata saved in MongoDB

### Versioning Rules
- Only one file is marked `isLatest = true`
- Older versions are preserved
- User can list all versions

### Delete Flow (Soft Delete)
1. File is marked `deleted = true`
2. `isLatest` is updated
3. Previous version becomes latest
4. Physical file is NOT removed (safe design)

### Restore Flow
1. Deleted file is restored
2. All other versions are marked `isLatest = false`
3. Restored file becomes active version




## ☁ Storage Strategy

Secure Vault supports **pluggable storage providers** using Strategy Pattern.

### Supported Providers
- **Local File System**
- **AWS S3**

### How It Works
`FileStorageService` interface defines:
- upload
- download
- delete

Implementations:
- `LocalFileStorageService`
- `S3StorageService`

### Switching Storage
Controlled via property:

```properties
storage.provider=local
# OR
storage.provider=s3




## ⚙ Environment Configuration

The application supports multiple environments using Spring Profiles.

### Local
```properties
spring.profiles.active=local
storage.provider=local
spring.profiles.active=cloud
storage.provider=s3
