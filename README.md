# Credit Card Management Portal

A full-stack fintech web application that enables users to manage credit cards, simulate transactions (regular & BNPL), track installments, and securely manage profile information.  
Designed using clean architecture principles and built to simulate real-world banking workflows with scalability, modularity, and security in mind.

## Project Overview

The Credit Card Management Portal provides end-to-end credit card lifecycle management including:
- User registration and authentication
- Credit card application and approval tracking
- Credit limit management
- Regular and BNPL transaction simulation
- Installment generation and payment tracking
- Automated late fee calculation
- Profile management

This project demonstrates:
- RESTful API development
- Layered backend architecture (Controller → Service → Repository)
- Relational database modeling
- Transactional data consistency
- Docker-based containerized deployment
- Full-stack integration

## Architecture

- **Frontend**: Vue.js 3 (Composition API) + Tailwind CSS + Vite  
- **Backend**: Spring Boot (Java 17) + Spring Security + JWT  
- **Database**: MySQL 8.x (relational, ACID-compliant)  
- **Authentication**: JWT (stateless)  
- **Containerization**: Docker + Docker Compose  

High-level flow:
1. User registers → logs in → receives JWT token  
2. Applies for card → tracks status  
3. Card issued → performs regular or BNPL transactions  
4. BNPL → installments generated automatically  
5. User pays installments → late fees calculated if overdue  

## Tech Stack

| Layer            | Technology                          | Purpose                              |
|------------------|-------------------------------------|--------------------------------------|
| Backend          | Spring Boot 3, Java 17              | REST APIs, Business Logic            |
| Security         | Spring Security + JWT               | Authentication & Authorization       |
| ORM              | Spring Data JPA / Hibernate         | Database access                      |
| Build Tool       | Maven                               | Dependency management                |
| Frontend         | Vue.js 3 (Composition API)          | Reactive UI                          |
| Build Tool       | Vite                                | Fast frontend development            |
| Styling          | Tailwind CSS                        | Responsive design                    |
| HTTP Client      | Axios                               | API calls from frontend              |
| Database         | MySQL 8.x                           | Persistent storage                   |
| Containerization | Docker, Docker Compose              | Easy deployment                      |
| Testing          | JUnit + Mockito, Vitest, Cypress    | Unit, integration, E2E testing       |

## Features

### Authentication & Profile
- User registration
- Secure login with JWT
- Profile update
- Financial detail management

### Card Management
- Apply for credit cards (VISA, MASTER, RUPAY, AMEX)
- Activate or block cards
- View masked card details
- Track available credit

### Credit Limit Management
- Update card credit limit
- Automatic recalculation of available balance

### Transaction Management
- Create regular transactions
- Create BNPL transactions (THREE, SIX, NINE month plans)
- View transaction history
- Update or delete transactions

### BNPL Installments
- Automatic installment creation
- Installment payment
- Track pending installments
- Identify overdue installments
- Automated late fee calculation

## Database Design

Database: MySQL 8.x

### Entities

1. **user_profiles**
   - id (PK)
   - full_name
   - email (unique)
   - phone
   - address
   - annual_income
   - is_eligible_for_bnpl
   - password (hashed)

2. **cards**
   - id (PK)
   - card_number (unique)
   - card_type
   - status
   - credit_limit
   - available_limit
   - expiry_date
   - user_id (FK → user_profiles)

3. **card_applications**
   - id (PK)
   - user_id (FK)
   - card_type
   - requested_limit
   - application_date
   - status
   - reviewed_by
   - review_date

4. **transactions**
   - id (PK)
   - card_id (FK → cards)
   - merchant_name
   - amount
   - transaction_date
   - category
   - is_bnpl

5. **bnpl_installments**
   - id (PK)
   - transaction_id (FK → transactions)
   - installment_number
   - amount
   - due_date
   - is_paid
   - late_fee

### Relationships
- One User → Many Cards
- One User → Many Card Applications
- One Card → Many Transactions
- One Transaction → Many BNPL Installments

## ER Diagram
(Add ER diagram image here)  
<img width="851" alt="ER Diagram" src="https://github.com/user-attachments/assets/b66e322c-3461-4a7d-b41e-646792f6c1e0" />

## API Endpoints

All endpoints are under `http://localhost:8080`.  
Protected endpoints require `Authorization: Bearer <token>` header.

### Authentication & Profile
- `POST /api/profile` — Register user
- `POST /api/profile/login` — Login & get JWT
- `GET /api/profile/{userId}` — Get profile
- `PUT /api/profile/{userId}` — Update profile
- `PUT /api/profile/{userId}/password` — Change password

### Card Operations
- `POST /api/cards/apply` — Apply for card
- `GET /api/cards/applications/{userId}` — List applications
- `GET /api/cards/{userId}` — List cards
- `PUT /api/cards/{cardId}/status` — Update card status
- `PUT /api/cards/{cardId}/limit` — Update credit limit

### Transactions
- `POST /api/transactions` — Create regular transaction
- `POST /api/transactions/bnpl?plan=THREE` — Create BNPL transaction
- `GET /api/transactions/card/{cardId}` — Transaction history
- `GET /api/transactions` — All transactions
- `GET /api/transactions/{id}` — Single transaction
- `PUT /api/transactions/{id}` — Update transaction
- `DELETE /api/transactions/{id}` — Delete transaction

### BNPL Installments
- `POST /api/bnpl/installments` — Create installment (manual)
- `POST /api/bnpl/installments/{id}/pay` — Pay installment
- `GET /api/bnpl/installments` — All installments
- `GET /api/bnpl/installments/{id}` — Single installment
- `GET /api/bnpl/installments/transaction/{transactionId}` — By transaction
- `GET /api/bnpl/installments/transaction/{transactionId}/pending` — Pending
- `GET /api/bnpl/installments/card/{cardId}/overdue` — Overdue
- `PUT /api/bnpl/installments/{id}` — Update installment
- `DELETE /api/bnpl/installments/{id}` — Delete installment

### Late Fees
- `GET /api/latefees/{cardId}` — Total late fee for card
- `GET /api/latefees/installment/{installmentId}` — Late fee for one installment

## Project Structure

## Future Improvements
- Role-based access control (Admin/User)
- API rate limiting & throttling
- Refresh token implementation
- Email/SMS notifications for due payments
- CI/CD pipeline (GitHub Actions)
- Cloud deployment (AWS / Azure)
- Redis caching for frequent queries
- Microservices migration for high scalability

## Demo Video

[Watch Full Application Demo](https://youtu.be/YOUR_VIDEO_LINK_HERE)  
(Upload your screen recording and replace this link)

## Team
- Abhishekh (Team Lead): Core Backend, BNPL, Security
- Akuthota Sravani: Card Application Module
- Aravind P: Credit Limit & Transaction Logic
- Pothuri Tejaswi: Profile & User Management
- Ashritha Sadu: Frontend UI/UX & Integration

## License
MIT License — see the LICENSE file for details.

## Repository
https://github.com/Abhishekhzeta/credit-card-management-portal

## Notes
- Frontend: http://localhost:5173
- Backend APIs: http://localhost:8080
- Use Postman collections (in repo) for API testing
- Default testing: Register a user → Login → Apply card → Simulate BNPL transaction
