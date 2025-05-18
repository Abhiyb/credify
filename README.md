
# Credit Card Management Portal

This project is a web application for managing credit cards, including application, transaction simulation, card blocking/activation, and profile management.

## 🧑‍💻 Team Members and Module Ownership

| Member Name         | Module Responsibility                                       | Backend Component                            | Frontend Component                     |
|---------------------|--------------------------------------------------------------|-----------------------------------------------|----------------------------------------|
| **Abhishekh** (Team Lead)     | Module 1: View Credit Cards + Activate/Block               | GET /cards/{userId}, PUT /cards/{cardId}/status | CardList.vue                          |
| **Akuthota Sravani**          | Module 2: Apply for Credit Card + View Requests           | POST /cards/apply, GET /cards/applications/{userId} | ApplyCard.vue, RequestStatus.vue     |
| **Aravind P**                 | Module 3: Manage Card Limit                                | PUT /cards/{cardId}/limit                      | ManageLimit.vue                       |
| **Pothuri Tejaswi**           | Module 4: Simulate & View Transactions                     | POST /transactions, GET /transactions/{cardId} | NewTransaction.vue, TransactionHistory.vue |
| **Ashritha Sadu**            | Module 5: Customer Profile Management                      | GET/PUT /api/profile/{userId}                  | UserProfile.vue                       |

## 📦 Tech Stack

- **Backend**: Spring Boot, Java, MySQL
- **Frontend**: Vue.js
- **Containerization**: Docker
- **Version Control**: Git + GitHub
- **Task Management**: Jira (Kanban)

## 🗂️ Repository Structure

