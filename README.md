# Credify — Credit Card Management Portal

A full-stack fintech web application that enables users to securely manage credit cards, apply for new cards, simulate regular and BNPL transactions, track installments, manage credit limits, and handle profile information — all with JWT-based authentication.

## Demo
<img src="./resolution2.gif" width="800">

## 🚀 Project Overview

The Credit Card Management Portal provides end-to-end credit card lifecycle management with the following core capabilities:

- Secure user registration, login, and profile management
- Credit card application submission and status tracking
- Credit card activation, blocking, and limit adjustment
- Simulation of regular and BNPL (Buy Now Pay Later) transactions
- Automatic installment generation for BNPL (3, 6, 9 months plans)
- Installment payment tracking with overdue detection
- Automated late fee calculation
- Transaction history with filtering options

This project demonstrates modern full-stack development practices, clean architecture, RESTful design, JWT security, and containerized deployment.

## 🏗 System Architecture

- **Frontend**: Vue 3 (Composition API) + Tailwind CSS + Vite
- **Backend**: Spring Boot 3 (Java 17) + Spring Security + JWT
- **Database**: MySQL 8.x (relational, ACID-compliant)
- **Authentication**: Stateless JWT tokens (no session)
- **Containerization**: Docker + Docker Compose
- **Communication**: REST APIs over HTTP

High-level flow:
1. User registers → logs in → receives JWT token
2. Applies for card → tracks status
3. Card issued → performs regular or BNPL transactions
4. BNPL → installments generated automatically
5. User pays installments → late fees calculated if overdue

## 🛠 Tech Stack

| Layer              | Technology                        | Purpose                                   |
|--------------------|-----------------------------------|-------------------------------------------|
| Backend            | Spring Boot 3, Java 17            | REST APIs, Business Logic                 |
| Security           | Spring Security + JWT             | Authentication & Authorization            |
| ORM                | Spring Data JPA / Hibernate       | Database access                           |
| Build Tool         | Maven                             | Dependency & build management             |
| Frontend           | Vue.js 3 (Composition API)        | Reactive single-page application          |
| Build Tool         | Vite                              | Fast frontend development & bundling      |
| Styling            | Tailwind CSS                      | Responsive & utility-first design         |
| HTTP Client        | Axios                             | API communication from frontend           |
| Database           | MySQL 8.x                         | Persistent relational storage             |
| Containerization   | Docker, Docker Compose            | Containerized local & production setup    |
| Testing            | JUnit + Mockito, Vitest, Cypress  | Unit, integration, E2E testing            |

## ✨ Key Features

- **Authentication & Profile**
    - Secure registration & login (JWT)
    - Profile update (name, phone, address, income)
    - BNPL eligibility check

- **Card Management**
    - Apply for VISA, Mastercard, RuPay, Amex
    - View masked card details
    - Activate / block cards
    - Track available credit

- **Credit Limit Management**
    - Update credit limit
    - Automatic available balance recalculation

- **Transaction Management**
    - Simulate regular transactions
    - Simulate BNPL transactions (3/6/9 months)
    - View transaction history
    - Update or delete transactions

- **BNPL Installments**
    - Automatic installment creation
    - Pay installments
    - View pending / paid / overdue
    - Automated late fee calculation

## 🗄 Database Design

**Database**: MySQL 8.x (chosen for ACID compliance and relational integrity required for financial data)

### Main Entities & Relationships

1. **user_profiles**
    - id (PK)
    - full_name
    - email (unique)
    - phone
    - address
    - annual_income
    - is_eligible_for_bnpl
    - password (BCrypt hashed)

2. **cards**
    - id (PK)
    - card_number (unique, masked)
    - card_type
    - status (ACTIVE / BLOCKED)
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

**Relationships**:
- One User → Many Cards
- One User → Many Card Applications
- One Card → Many Transactions
- One Transaction → Many BNPL Installments

## ER Diagram

<img width="851" alt="ER Diagram" src="https://github.com/user-attachments/assets/b66e322c-3461-4a7d-b41e-646792f6c1e0" />

(Replace with your actual high-quality image path if needed)

## 📡 API Endpoints

All endpoints are available at `http://localhost:8080`.  
Protected endpoints require `Authorization: Bearer <token>` header.

| Category               | Method | Endpoint                                          | Description                                 | Auth Required |
|-----------------------|--------|---------------------------------------------------|---------------------------------------------|---------------|
| Authentication         | POST   | `/api/profile`                                    | Register new user                           | No            |
| Authentication         | POST   | `/api/profile/login`                              | Login & receive JWT                         | No            |
| Profile                | GET    | `/api/profile/{userId}`                           | Get user profile                            | Yes           |
| Profile                | PUT    | `/api/profile/{userId}`                           | Update profile                              | Yes           |
| Profile                | PUT    | `/api/profile/{userId}/password`                  | Change password                             | Yes           |
| Card Application       | POST   | `/api/cards/apply`                                | Apply for a credit card                     | Yes           |
| Card Application       | GET    | `/api/cards/applications/{userId}`                | Get all applications for user               | Yes           |
| Cards                  | GET    | `/api/cards/{userId}`                             | Get user's credit cards                     | Yes           |
| Cards                  | PUT    | `/api/cards/{cardId}/status`                      | Update card status (ACTIVE/BLOCKED)         | Yes           |
| Cards                  | PUT    | `/api/cards/{cardId}/limit`                       | Update credit limit                         | Yes           |
| Transactions           | POST   | `/api/transactions`                               | Create regular transaction                  | Yes           |
| Transactions           | POST   | `/api/transactions/bnpl?plan=THREE`               | Create BNPL transaction                     | Yes           |
| Transactions           | GET    | `/api/transactions/card/{cardId}`                 | Get transaction history for card            | Yes           |
| Transactions           | GET    | `/api/transactions`                               | Get all transactions                        | Yes           |
| Transactions           | GET    | `/api/transactions/{id}`                          | Get single transaction                      | Yes           |
| Transactions           | PUT    | `/api/transactions/{id}`                          | Update transaction                          | Yes           |
| Transactions           | DELETE | `/api/transactions/{id}`                          | Delete transaction                          | Yes           |
| BNPL Installments      | POST   | `/api/bnpl/installments`                          | Create BNPL installment (manual)            | Yes           |
| BNPL Installments      | POST   | `/api/bnpl/installments/{id}/pay`                 | Pay installment                             | Yes           |
| BNPL Installments      | GET    | `/api/bnpl/installments`                          | Get all installments                        | Yes           |
| BNPL Installments      | GET    | `/api/bnpl/installments/{id}`                     | Get single installment                      | Yes           |
| BNPL Installments      | GET    | `/api/bnpl/installments/transaction/{id}`         | Get installments by transaction             | Yes           |
| BNPL Installments      | GET    | `/api/bnpl/installments/transaction/{id}/pending` | Get pending installments                    | Yes           |
| BNPL Installments      | GET    | `/api/bnpl/installments/card/{cardId}/overdue`    | Get overdue installments                    | Yes           |
| BNPL Installments      | PUT    | `/api/bnpl/installments/{id}`                     | Update installment                          | Yes           |
| BNPL Installments      | DELETE | `/api/bnpl/installments/{id}`                     | Delete installment                          | Yes           |
| Late Fees              | GET    | `/api/latefees/{cardId}`                          | Get total late fee for card                 | Yes           |
| Late Fees              | GET    | `/api/latefees/installment/{installmentId}`       | Get late fee for single installment         | Yes           |

## Setup Instructions

1. Clone the repository
   ```bash
   git clone https://github.com/Abhishekhzeta/credit-card-management-portal.git

   Start MySQL & create database credit_card_portal
Backend:Bashcd backend
mvn clean install
mvn spring-boot:run
Frontend:Bashcd ../frontend
npm install
npm run dev
Access: http://localhost:5173

## Future Improvements
API rate limiting & throttling

Refresh token implementation

CI/CD pipeline (GitHub Actions)

Cloud deployment (AWS / Azure)

Redis caching for frequent queries

Microservices migration for high scalability
