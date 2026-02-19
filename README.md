# 💳 Credit Card Management Portal

**A secure, full-stack banking application** built for the Zeta Software Development Foundation Program.  
Empowers users to manage credit cards, apply for new cards, simulate real-world transactions (regular + BNPL), track installments, manage credit limits, and handle late fees — all with JWT authentication and a modern responsive UI.

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Vue.js-3-%234FC08D?style=for-the-badge&logo=vue.js" alt="Vue 3" />
  <img src="https://img.shields.io/badge/Tailwind%20CSS-3.4-38B2AC?style=for-the-badge&logo=tailwind-css" alt="Tailwind" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql" alt="MySQL" />
  <img src="https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker" alt="Docker" />
</p>



## 📹 Demo Video



## ✨ Key Features

- 🏦 **User Authentication** — Secure registration & login with JWT
- 💳 **Credit Card Management** — View, block/unblock, update limits
- 📝 **Card Applications** — Apply for VISA, Mastercard, RuPay, Amex
- 💸 **Transaction Simulation** — Regular & BNPL (3/6/9 months plans)
- 📊 **BNPL Installment Tracking** — Pay, view pending/overdue, late fee calculation
- 🔒 **Profile Management** — Update personal & financial details
- 📈 **Transaction History** — Filter by date, type, merchant
- 🖥️ **Responsive UI** — Works beautifully on mobile, tablet, desktop
- 🐳 **Dockerized** — One-command full-stack deployment

## 🛠️ Tech Stack

| Layer          | Technology                          | Purpose                              |
|----------------|-------------------------------------|--------------------------------------|
| Backend        | Spring Boot 3, Java 17, Maven       | REST APIs, Business Logic, Security  |
| Authentication | Spring Security + JWT               | Stateless, secure token-based auth   |
| Frontend       | Vue 3 (Composition API), Vite       | Modern, reactive UI                  |
| Styling        | Tailwind CSS                        | Fast, responsive design              |
| Database       | MySQL 8                             | Persistent storage                   |
| Container      | Docker + Docker Compose             | Easy local & production deployment   |

## 🚀 Quick Start (Local Development)

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Docker (optional but recommended)

### 1. Clone the Repository
```bash
git clone https://github.com/Abhishekhzeta/credit-card-management-portal.git
cd credit-card-management-portal




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

