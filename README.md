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

<p align="center">
  <a href="#demo">📹 Live Demo Video</a> •
  <a href="#features">✨ Features</a> •
  <a href="#technologies">🛠️ Tech Stack</a> •
  <a href="#installation">🚀 Quick Start</a> •
  <a href="#api">📡 API Endpoints</a>
</p>

## 📹 Demo Video

Watch the full application in action:  
[![Demo Video](https://img.shields.io/badge/Watch%20Demo%20Video-FF0000?style=for-the-badge&logo=youtube&logoColor=white)](https://youtu.be/YOUR_VIDEO_ID_HERE)

> Replace the link above with your actual demo video URL (YouTube, Loom, etc.) after uploading.

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
