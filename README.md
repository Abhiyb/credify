# 💳 Credit Card Management Portal

A full-stack fintech web application that enables users to manage credit cards, simulate transactions (regular & BNPL), track installments, and securely manage profile information.

Designed using clean architecture principles and built to simulate real-world banking workflows with scalability, modularity, and security in mind.

---

# 🚀 Project Overview

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

---

# 🏗 System Architecture

## 🔹 Frontend
- Vue.js 3 (Composition API)
- Tailwind CSS
- Vite

## 🔹 Backend
- Spring Boot (Java 17)
- REST APIs
- Spring Data JPA
- Maven

## 🔹 Database
- MySQL 8.x

## 🔹 Testing
- JUnit & Mockito (Backend)
- Vitest (Frontend)
- Cypress (End-to-End)

## 🔹 Containerization
- Docker
- Docker Compose

---

# ✨ Features

## 🔐 Authentication & Profile
- User registration
- Secure login
- Profile update
- Financial detail management

## 💳 Card Management
- Apply for credit cards (VISA, MASTER, RUPAY, AMEX)
- Activate or block cards
- View masked card details
- Track available credit

## 📈 Credit Limit Management
- Update card credit limit
- Automatic recalculation of available balance

## 💰 Transaction Management
- Create regular transactions
- Create BNPL transactions (THREE, SIX, NINE month plans)
- View transaction history
- Update or delete transactions

## 📆 BNPL Installments
- Automatic installment creation
- Installment payment
- Track pending installments
- Identify overdue installments
- Automated late fee calculation

---

# 🗄 Database Design

## Entities

### 1️⃣ Users
- id (Primary Key)
- name
- email (Unique)
- phone_number
- address
- is_eligible_for_bnpl

### 2️⃣ Cards
- id (Primary Key)
- card_number (Unique)
- card_type
- status
- credit_limit
- available_limit
- expiry_date
- user_id (Foreign Key → users.id)

### 3️⃣ Card Applications
- id (Primary Key)
- user_id (Foreign Key)
- card_type
- requested_limit
- application_date
- status
- reviewed_by
- review_date

### 4️⃣ Transactions
- id (Primary Key)
- card_id (Foreign Key → cards.id)
- merchant_name
- amount
- transaction_date
- category
- is_bnpl

### 5️⃣ User Profiles
- id (Primary Key)
- user_id (Foreign Key)
- full_name
- email
- phone
- address
- annual_income
- created_at
- updated_at

### 6️⃣ BNPL Installments
- id (Primary Key)
- transaction_id (Foreign Key → transactions.id)
- installment_number
- amount
- due_date
- is_paid
- late_fee

---

## Relationships

- One User → Many Cards  
- One User → Many Card Applications  
- One Card → Many Transactions  
- One Transaction → Many BNPL Installments  

---

# 📡 API Endpoints

## 🔐 Authentication
- POST `/api/profile`
- POST `/api/profile/login`

## 💳 Card Operations
- POST `/api/cards/apply`
- GET `/api/cards/applications/{userId}`
- GET `/api/cards/user/{userId}`
- PUT `/api/cards/{cardId}/status`
- PUT `/cards/{cardId}/limit`

## 💰 Transactions
- POST `/transactions`
- POST `/transactions/bnpl`
- GET `/transactions`
- GET `/transactions/{id}`
- GET `/transactions/card/{cardId}`
- PUT `/transactions/{id}`
- DELETE `/transactions/{id}`

## 📆 BNPL Installments
- POST `/bnpl/installments`
- POST `/bnpl/installments/{id}/pay`
- GET `/bnpl/installments`
- GET `/bnpl/installments/{id}`
- GET `/bnpl/installments/transaction/{id}`
- GET `/bnpl/installments/card/{cardId}/overdue`
- PUT `/bnpl/installments/{id}`
- DELETE `/bnpl/installments/{id}`

## 💸 Late Fees
- GET `/latefees/{cardId}`
- GET `/latefees/installment/{id}`

---

# 📁 Project Structure
backend/
├── controller/
├── service/
├── repository/
├── entity/
├── config/

frontend/
├── components/
├── views/
├── services/

📊 ER Diagram
(Add ER diagram image here)

🚀 Future Improvements

Role-based access control
API rate limiting
CI/CD pipeline integration
Cloud deployment
Caching layer (Redis)
Microservices-based scaling
