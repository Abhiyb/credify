# Credify — Credit Card & BNPL Simulation Platform

Credify is a secure, full-stack fintech simulation platform built using Spring Boot and Vue 3.  
It models real-world credit card operations including card lifecycle management, transaction processing, BNPL installment workflows, and automated late fee handling.

This project demonstrates production-style backend architecture, secure authentication, and clean financial workflow design.

---

## Overview

Credify simulates how modern credit systems operate internally — from issuing a card to processing transactions and managing installment-based repayments.

It is designed with scalability, modular architecture, and financial data integrity in mind.

---

## Core Features

### Authentication & Security
- Secure user registration and login
- JWT-based stateless authentication
- Protected API routes

### Credit Card Management
- Apply for VISA, Mastercard, RuPay, or Amex
- Activate, block, and manage cards
- Update credit limits
- Card lifecycle tracking

### Transaction Engine
- Regular transaction simulation
- Credit limit validation before processing
- Transaction history with filtering

### BNPL Engine
- 3 / 6 / 9 month installment plans
- Automatic installment generation
- Installment tracking (Paid / Pending / Overdue)
- Late fee calculation for missed payments

### Profile Management
- Update personal and financial details
- Eligibility flagging for BNPL

---

## System Architecture

The application follows a layered backend architecture to ensure maintainability and scalability.

### Backend
- Spring Boot 3
- Java 17
- Controller → Service → Repository pattern
- DTO mapping to prevent entity exposure
- Global exception handling
- ACID-compliant transactional operations
- RESTful API design
- Spring Security with JWT authentication

### Frontend
- Vue 3 (Composition API)
- Vite
- Axios-based API integration
- Responsive UI design

### Database
- MySQL 8
- Relational schema with foreign key constraints
- Transactional data integrity

### Deployment
- Dockerized multi-container setup
- Environment-based configuration
- Isolated backend and database services

---

## Tech Stack

| Layer          | Technology                          | Purpose                              |
|----------------|-------------------------------------|--------------------------------------|
| Backend        | Spring Boot 3, Java 17, Maven       | REST APIs, Business Logic, Security  |
| Authentication | Spring Security + JWT               | Stateless, secure token-based auth   |
| Frontend       | Vue 3 (Composition API), Vite       | Modern, reactive UI                  |
| Styling        | Tailwind CSS                        | Fast, responsive design              |
| Database       | MySQL 8                             | Persistent storage                   |
| Container      | Docker + Docker Compose             | Local & production deployment        |

---

## Key Workflows

### Credit Card Transaction
1. User initiates transaction  
2. System validates available credit  
3. Transaction is persisted  
4. Credit utilization is updated  

### BNPL Transaction
1. User selects installment tenure  
2. System validates eligibility  
3. Installment schedule is generated  
4. Monthly dues are tracked  
5. Late fee applied if overdue  

---

## Demo

Add demo video or GIF link here.

---

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8+
- Docker (optional)

### Clone Repository

```bash
git clone https://github.com/Abhishekhzeta/credit-card-management-portal.git
cd credit-card-management-portal
