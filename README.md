# 💳 Credit Card Management Portal

The Credit Card Management Portal is a web application developed for a bank as part of the Zeta Software Development Foundation Program. It empowers customers to manage credit cards and Buy Now Pay Later (BNPL) plans through a secure, responsive, and intuitive interface. Key features include card management, transaction simulation (regular and BNPL), credit limit adjustments, installment tracking, and profile management. The application is built with a modern tech stack, ensuring scalability, performance, and compliance with banking standards.

## 📑 Table of Contents
- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
  - [Database Setup](#database-setup)
  - [Docker Setup](#docker-setup)
- [API Usage](#api-usage)
  - [User Registration](#1-user-registration)
  - [User Login](#2-user-login)
  - [Apply for a Credit Card](#3-apply-for-a-credit-card)
  - [Get All Card Applications](#4-get-all-card-applications-for-a-user)
  - [Get Credit Card Details](#5-get-credit-card-details-by-user-id)
  - [Update Card Status](#6-update-card-status)
  - [Update Credit Limit](#7-update-credit-limit)
  - [Create a Regular Transaction](#8-create-a-regular-transaction)
  - [Create a BNPL Transaction](#9-create-a-bnpl-transaction)
  - [Get Transaction History](#10-get-transaction-history-for-a-card)
  - [Get All Transactions](#11-get-all-transactions)
  - [Get Transaction by ID](#12-get-transaction-by-id)
  - [Update a Transaction](#13-update-a-transaction)
  - [Delete a Transaction](#14-delete-a-transaction)
  - [Create a BNPL Installment](#15-create-a-bnpl-installment)
  - [Pay a BNPL Installment](#16-pay-a-bnpl-installment)
  - [Get All BNPL Installments](#17-get-all-bnpl-installments)
  - [Get BNPL Installment by ID](#18-get-bnpl-installment-by-id)
  - [Get BNPL Installments for a Transaction](#19-get-bnpl-installments-for-a-transaction)
  - [Get Pending BNPL Installments](#20-get-pending-bnpl-installments)
  - [Get Overdue BNPL Installments](#21-get-overdue-bnpl-installments)
  - [Update a BNPL Installment](#22-update-a-bnpl-installment)
  - [Delete a BNPL Installment](#23-delete-a-bnpl-installment)
  - [Get Total Late Fee for a Card](#24-get-total-late-fee-for-a-card)
  - [Get Late Fee for an Installment](#25-get-late-fee-for-an-installment)
- [Testing](#testing)
- [Contributing](#contributing)
- [Team](#team)
- [License](#license)
- [Repository](#repository)

## ✨ Features
- **Card Management**: View, activate, block, and manage credit card details (e.g., card number, status, limit, expiry).
- **Card Applications**: Apply for VISA, Mastercard, RUPAY, or Amex cards and track application status.
- **Credit Limit Adjustment**: Modify card limits within bank-approved ranges.
- **Transaction Simulation**: Simulate regular and BNPL transactions with THREE, SIX, or NINE-month installment plans.
- **Transaction History**: Generate detailed transaction reports with filtering options.
- **BNPL Management**: Create, pay, and track BNPL installments, with automated late fee calculations.
- **Profile Management**: Update personal and financial details securely.

## 🛠️ Technologies
- **Backend**: Spring Boot (Java 17), REST APIs, Maven
- **Frontend**: Vue.js 3 (Composition API), Tailwind CSS, Vite, JavaScript
- **Database**: MySQL 8.x
- **Testing**: JUnit (backend), Vitest (frontend), Cypress (end-to-end)
- **Containerization**: Docker, Docker Compose
- **Version Control**: Git

## 📥 Installation

### Prerequisites
- **Java JDK 17** or later
- **Node.js v18.x** or later
- **npm** (Node Package Manager)
- **MySQL 8.x**
- **Docker** and **Docker Compose**
- **Git**

### Backend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Abhishekhzeta/credit-card-management-portal.git
   ```
2. Navigate to the backend directory:
   ```bash
   cd backend
   ```
3. Install dependencies:
   ```bash
   mvn install
   ```
4. Configure MySQL connection in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/credit_card_portal
       username: user
       password: password
     jpa:
       hibernate:
         ddl-auto: update
   ```
5. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Create a `.env` file and set the backend API URL:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```
4. Run the development server:
   ```bash
   npm run dev
   ```

### Database Setup
1. Start MySQL and create the database:
   ```sql
   CREATE DATABASE credit_card_portal;
   ```
2. The schema is automatically applied via Spring Boot’s JPA (`ddl-auto: update`). Alternatively, run the schema script manually from `backend/src/main/resources/db/migration/schema.sql`:
   ```sql
   CREATE TABLE users (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       name VARCHAR(255) NOT NULL,
       email VARCHAR(255) UNIQUE NOT NULL,
       phone_number VARCHAR(20),
       address TEXT,
       is_eligible_for_bnpl BOOLEAN DEFAULT FALSE
   );

   CREATE TABLE cards (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       card_number VARCHAR(16) UNIQUE NOT NULL,
       card_type VARCHAR(50) NOT NULL,
       status VARCHAR(20) NOT NULL,
       credit_limit DOUBLE NOT NULL,
       available_limit DOUBLE NOT NULL,
       expiry_date DATE NOT NULL,
       user_id BIGINT,
       FOREIGN KEY (user_id) REFERENCES users(id)
   );

   CREATE TABLE card_applications (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       user_id BIGINT,
       card_type VARCHAR(50) NOT NULL,
       requested_limit DOUBLE NOT NULL,
       application_date DATE NOT NULL,
       status VARCHAR(20) NOT NULL,
       reviewed_by VARCHAR(255),
       review_date DATE,
       FOREIGN KEY (user_id) REFERENCES users(id)
   );

   CREATE TABLE transactions (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       card_id BIGINT,
       merchant_name VARCHAR(255) NOT NULL,
       amount DOUBLE NOT NULL,
       transaction_date DATE NOT NULL,
       category VARCHAR(50),
       is_bnpl BOOLEAN DEFAULT FALSE,
       FOREIGN KEY (card_id) REFERENCES cards(id)
   );

   CREATE TABLE user_profiles (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       user_id BIGINT,
       full_name VARCHAR(255) NOT NULL,
       email VARCHAR(255) NOT NULL,
       phone VARCHAR(20),
       address TEXT,
       annual_income DOUBLE,
       created_at TIMESTAMP NOT NULL,
       updated_at TIMESTAMP,
       FOREIGN KEY (user_id) REFERENCES users(id)
   );

   CREATE TABLE bnpl_installments (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       transaction_id BIGINT,
       installment_number INTEGER NOT NULL,
       amount DOUBLE NOT NULL,
       due_date DATE NOT NULL,
       is_paid BOOLEAN DEFAULT FALSE,
       late_fee DOUBLE DEFAULT 0.0,
       FOREIGN KEY (transaction_id) REFERENCES transactions(id)
   );
   ```

### Docker Setup
1. Ensure Docker and Docker Compose are installed.
2. From the project root, create a `docker-compose.yml`:
   ```yaml
   version: '3.8'
   services:
     backend:
       build: ./backend
       ports:
         - "8080:8080"
       depends_on:
         - db
       environment:
         - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/credit_card_portal
         - SPRING_DATASOURCE_USERNAME=user
         - SPRING_DATASOURCE_PASSWORD=password
     frontend:
       build: ./frontend
       ports:
         - "5173:5173"
     db:
       image: mysql:8
       environment:
         MYSQL_DATABASE: credit_card_portal
         MYSQL_USER: user
         MYSQL_PASSWORD: password
         MYSQL_ROOT_PASSWORD: rootpassword
       volumes:
         - db_data:/var/lib/mysql
   volumes:
     db_data:
   ```
3. Run the application:
   ```bash
   docker-compose up -d --build
   ```
4. Access the frontend at `http://localhost:5173` and backend APIs at `http://localhost:8080`.

## 🚀 API Usage

### Overview
The API provides endpoints for user management, card operations, transactions, and BNPL installment management. All endpoints are accessible at `http://localhost:8080`. Use Postman to test requests, and include authentication tokens (e.g., from login) in headers if required. Postman collections (`BNPL_API_Collection.json`, `CapstoneApi.postman_collection.json`) are available in the repository.

### 1. User Registration

**Endpoint:** `POST /api/profile`

**Request:**
```json
{
  "fullName": "Ananaya",
  "email": "ananay@example.com",
  "phone": "9876546756",
  "address": "123 Zeta Lane, Bangalore",
  "annualIncome": 500000,
  "password": "Anan@1234"
}
```

**Response:**
```json
{
  "userId": 1,
  "fullName": "Ananaya",
  "email": "ananay@example.com",
  "message": "User registered successfully"
}
```

### 2. User Login

**Endpoint:** `POST /api/profile/login`

**Request:**
```json
{
  "email": "ananay@example.com",
  "password": "Anan@1234"
}
```

**Response:**
```json
{
  "userId": 1,
  "fullName": "Ananaya",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Apply for a Credit Card

**Endpoint:** `POST /api/cards/apply`

**Request:**
```json
{
  "user": {
    "userId": 1
  },
  "cardType": "VISA",
  "requestedLimit": 50000.0
}
```

**Response:**
```json
{
  "applicationId": 1,
  "cardType": "VISA",
  "requestedLimit": 50000.0,
  "status": "PENDING",
  "applicationDate": "2025-05-26"
}
```

> **Note:** Repeat for card types like `MASTER`, `RUPAY`, `AMEX`.

### 4. Get All Card Applications for a User

**Endpoint:** `GET /api/cards/applications/1`

**Response:**
```json
[
  {
    "applicationId": 1,
    "cardType": "VISA",
    "status": "APPROVED",
    "requestedLimit": 50000.0,
    "applicationDate": "2025-05-21"
  },
  {
    "applicationId": 2,
    "cardType": "MASTER",
    "status": "APPROVED",
    "requestedLimit": 50000.0,
    "applicationDate": "2025-05-21"
  }
]
```

### 5. Get Credit Card Details by User ID

**Endpoint:** `GET /api/cards/user/1`

**Response:**
```json
[
  {
    "cardId": 1,
    "cardNumber": "****-****-****-1234",
    "cardType": "VISA",
    "status": "ACTIVE",
    "creditLimit": 50000.0,
    "availableLimit": 45000.0,
    "expiryDate": "2028-05-31"
  },
  {
    "cardId": 2,
    "cardNumber": "****-****-****-5678",
    "cardType": "MASTER",
    "status": "ACTIVE",
    "creditLimit": 50000.0,
    "availableLimit": 50000.0,
    "expiryDate": "2028-05-31"
  }
]
```

> **Note:** Uses `/user/{userId}` to avoid conflicts with other mappings.

### 6. Update Card Status

**Endpoint:** `PUT /api/cards/1/status?status=ACTIVE`

- Replace `1` with the card ID.
- Status: `ACTIVE`, `BLOCKED`

**Response:**
```json
{
  "cardId": 1,
  "cardNumber": "****-****-****-1234",
  "status": "ACTIVE",
  "message": "Card status updated successfully"
}
```

### 7. Update Credit Limit

**Endpoint:** `PUT /cards/1/limit?newLimit=60000`

- Replace `1` with the card ID.

**Response:**
```json
{
  "cardId": 1,
  "cardNumber": "****-****-****-1234",
  "creditLimit": 60000.0,
  "availableLimit": 55000.0,
  "message": "Credit limit updated successfully"
}
```

### 8. Create a Regular Transaction

**Endpoint:** `POST /transactions`

**Request:**
```json
{
  "cardId": 1,
  "amount": 5000.0,
  "merchantName": "Amazon India",
  "category": "Shopping"
}
```

**Response:**
```json
{
  "id": 1,
  "cardId": 1,
  "amount": 5000.0,
  "merchantName": "Amazon India",
  "category": "Shopping",
  "transactionDate": "2025-05-26",
  "isBNPL": false,
  "status": "Completed"
}
```

### 9. Create a BNPL Transaction

**Endpoint:** `POST /transactions/bnpl?plan=THREE`

- `plan`: `THREE`, `SIX`, `NINE`

**Request:**
```json
{
  "cardId": 1,
  "amount": 12000.0,
  "merchantName": "Home Depot",
  "category": "Furniture"
}
```

**Response:**
```json
{
  "id": 2,
  "cardId": 1,
  "amount": 12000.0,
  "merchantName": "Home Depot",
  "category": "Furniture",
  "transactionDate": "2025-05-26",
  "isBNPL": true,
  "status": "Pending"
}
```

### 10. Get Transaction History for a Card

**Endpoint:** `GET /transactions/card/1`

**Response:**
```json
[
  {
    "id": 1,
    "cardId": 1,
    "amount": 5000.0,
    "merchantName": "Amazon India",
    "category": "Shopping",
    "transactionDate": "2025-05-26",
    "isBNPL": false
  },
  {
    "id": 2,
    "cardId": 1,
    "amount": 12000.0,
    "merchantName": "Home Depot",
    "category": "Furniture",
    "transactionDate": "2025-05-26",
    "isBNPL": true
  }
]
```

### 11. Get All Transactions

**Endpoint:** `GET /transactions`

**Response:**
```json
[
  {
    "id": 1,
    "cardId": 1,
    "amount": 5000.0,
    "merchantName": "Amazon India",
    "category": "Shopping",
    "transactionDate": "2025-05-26",
    "isBNPL": false
  },
  {
    "id": 2,
    "cardId": 1,
    "amount": 12000.0,
    "merchantName": "Home Depot",
    "category": "Furniture",
    "transactionDate": "2025-05-26",
    "isBNPL": true
  }
]
```

### 12. Get Transaction by ID

**Endpoint:** `GET /transactions/1`

**Response:**
```json
{
  "id": 1,
  "cardId": 1,
  "amount": 5000.0,
  "merchantName": "Amazon India",
  "category": "Shopping",
  "transactionDate": "2025-05-26",
  "isBNPL": false
}
```

### 13. Update a Transaction

**Endpoint:** `PUT /transactions/1`

**Request:**
```json
{
  "merchantName": "Amazon India Updated",
  "category": "Electronics",
  "amount": 4500.0
}
```

**Response:**
```json
{
  "id": 1,
  "cardId": 1,
  "amount": 4500.0,
  "merchantName": "Amazon India Updated",
  "category": "Electronics",
  "transactionDate": "2025-05-26",
  "isBNPL": false,
  "message": "Transaction updated successfully"
}
```

### 14. Delete a Transaction

**Endpoint:** `DELETE /transactions/1`

**Response:**
```json
{
  "message": "Transaction deleted successfully"
}
```

### 15. Create a BNPL Installment

**Endpoint:** `POST /bnpl/installments`

**Request:**
```json
{
  "transactionId": 2,
  "installmentNumber": 1,
  "amount": 4000.0,
  "dueDate": "2025-06-25",
  "isPaid": false
}
```

**Response:**
```json
{
  "id": 1,
  "transactionId": 2,
  "installmentNumber": 1,
  "amount": 4000.0,
  "dueDate": "2025-06-25",
  "isPaid": false,
  "lateFee": 0.0
}
```

### 16. Pay a BNPL Installment

**Endpoint:** `POST /bnpl/installments/1/pay?amount=4000`

**Response:**
```json
{
  "id": 1,
  "transactionId": 2,
  "installmentNumber": 1,
  "amount": 4000.0,
  "dueDate": "2025-06-25",
  "isPaid": true,
  "lateFee": 0.0,
  "message": "Installment paid successfully"
}
```

### 17. Get All BNPL Installments

**Endpoint:** `GET /bnpl/installments`

**Response:**
```json
[
  {
    "id": 1,
    "transactionId": 2,
    "installmentNumber": 1,
    "amount": 4000.0,
    "dueDate": "2025-06-25",
    "isPaid": true,
    "lateFee": 0.0
  },
  {
    "id": 2,
    "transactionId": 2,
    "installmentNumber": 2,
    "amount": 4000.0,
    "dueDate": "2025-07-25",
    "isPaid": false,
    "lateFee": 0.0
  }
]
```

### 18. Get BNPL Installment by ID

**Endpoint:** `GET /bnpl/installments/1`

**Response:**
```json
{
  "id": 1,
  "transactionId": 2,
  "installmentNumber": 1,
  "amount": 4000.0,
  "dueDate": "2025-06-25",
  "isPaid": true,
  "lateFee": 0.0
}
```

### 19. Get BNPL Installments for a Transaction

**Endpoint:** `GET /bnpl/installments/transaction/2`

**Response:**
```json
[
  {
    "id": 1,
    "transactionId": 2,
    "installmentNumber": 1,
    "amount": 4000.0,
    "dueDate": "2025-06-25",
    "isPaid": true,
    "lateFee": 0.0
  },
  {
    "id": 2,
    "transactionId": 2,
    "installmentNumber": 2,
    "amount": 4000.0,
    "dueDate": "2025-07-25",
    "isPaid": false,
    "lateFee": 0.0
  }
]
```

### 20. Get Pending BNPL Installments

**Endpoint:** `GET /bnpl/installments/transaction/2/pending`

**Response:**
```json
[
  {
    "id": 2,
    "transactionId": 2,
    "installmentNumber": 2,
    "amount": 4000.0,
    "dueDate": "2025-07-25",
    "isPaid": false,
    "lateFee": 0.0
  }
]
```

### 21. Get Overdue BNPL Installments

**Endpoint:** `GET /bnpl/installments/card/1/overdue`

**Response:**
```json
[
  {
    "id": 3,
    "transactionId": 2,
    "installmentNumber": 3,
    "amount": 4000.0,
    "dueDate": "2025-05-25",
    "isPaid": false,
    "lateFee": 200.0
  }
]
```

### 22. Update a BNPL Installment

**Endpoint:** `PUT /bnpl/installments/1`

**Request:**
```json
{
  "amount": 4100.0,
  "dueDate": "2025-06-30",
  "isPaid": false
}
```

**Response:**
```json
{
  "id": 1,
  "transactionId": 2,
  "installmentNumber": 1,
  "amount": 4100.0,
  "dueDate": "2025-06-30",
  "isPaid": false,
  "lateFee": 0.0,
  "message": "Installment updated successfully"
}
```

### 23. Delete a BNPL Installment

**Endpoint:** `DELETE /bnpl/installments/1`

**Response:**
```json
{
  "message": "Installment deleted successfully"
}
```

### 24. Get Total Late Fee for a Card

**Endpoint:** `GET /latefees/1`

**Response:**
```json
200.0
```

### 25. Get Late Fee for an Installment

**Endpoint:** `GET /latefees/installment/3`

**Response:**
```json
200.0
```

## 🧪 Testing
- **Backend**: JUnit and Mockito for service/repository tests.
  ```bash
  cd backend
  mvn test
  ```
- **Frontend**: Vitest for component tests.
  ```bash
  cd frontend
  npm run test
  ```
- **End-to-End**: Cypress for user workflows.
  ```bash
  cd frontend
  npm run cypress:open
  ```

## 🤝 Contributing
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Commit changes: `git commit -m "Add your feature"`.
4. Push to the branch: `git push origin feature/your-feature`.
5. Open a pull request.

Follow coding standards and include tests for new features.

## ER diagram
<img width="851" alt="Screenshot 2025-05-26 at 4 34 48 PM" src="https://github.com/user-attachments/assets/b66e322c-3461-4a7d-b41e-646792f6c1e0" />


## 👥 Team
- **Abhishekh** (Team Lead): Card Management, BNPL Feature
- **Akuthota Sravani**: Card Applications
- **Aravind P**: Credit Limit Management
- **Pothuri Tejaswi**: Transactions
- **Ashritha Sadu**: Profile Management

## 📄 License
MIT License. See `LICENSE` file for details.

## 🌐 Repository
[https://github.com/Abhishekhzeta/credit-card-management-portal](https://github.com/Abhishekhzeta/credit-card-management-portal)

## 📝 Notes
- The application is accessible at `http://localhost:5173` (frontend) and `http://localhost:8080` (backend APIs).
