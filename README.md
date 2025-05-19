
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


##  How to Clone and Start Working on the Project

1. **Clone the repository:**
2. **Create your own branch from `main`:**
3. **Make changes only in your assigned module and follow the file naming conventions .**
4. **Commit your changes and push your branch:** i)git add . ii)git commit -m "Describe your changes" iii)git push origin feature/<your-module-name>

6. **Create a Pull Request (PR) from your branch to `main` branch on GitHub.**

7. **Wait for code review and approval before merging.**

---

## 💻 Running the Backend

- After cloning the repo, to run the backend server:
- Open **only** the `backend` folder in your IDE (like IntelliJ IDEA or VSCode).
- Run the Spring Boot application from there.
- This is necessary because the backend is a separate Spring Boot project inside the repo.

---

Please follow these steps carefully to avoid conflicts and ensure smooth collaboration!

