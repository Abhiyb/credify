# 💳 Credit Card Management API — Usage Guide

## ✅ 1. User Registration

**Endpoint:**  
`POST http://localhost:8080/api/profile`

**Request Body:**
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

---

## 🔐 2. User Login

**Endpoint:**  
`POST http://localhost:8080/api/profile/login`

**Request Body:**
```json
{
  "email": "ananay@example.com",
  "password": "Anan@1234"
}
```

---

## 📝 3. Apply for a Credit Card

**Endpoint:**  
`POST http://localhost:8080/cards/apply`

**Request Body:**
```json
{
  "user": {
    "userId": 1
  },
  "cardType": "VISA",
  "requestedLimit": 50000.0
}
```

> 🔁 Repeat the same for multiple card types (e.g., `MASTER`, `RUPAY`)

---

## 📄 4. Get All Card Applications for a User

**Endpoint:**  
`GET http://localhost:8080/cards/applications/1`

**Sample Response:**
```json
[
  {
    "cardType": "VISA",
    "status": "APPROVED",
    "requestedLimit": 50000.0,
    "applicationDate": "2025-05-21"
  },
  {
    "cardType": "MASTER",
    "status": "APPROVED",
    "requestedLimit": 50000.0,
    "applicationDate": "2025-05-21"
  }
]
```

---

## 💳 5. Get Credit Card Details by User ID

**Endpoint:**  
`GET http://localhost:8080/api/cards/user/1`

> Ensure your controller path is `/user/{userId}` to avoid conflict with PUT mapping.

---

## 🔄 6. Update Card Status

**Endpoint:**  
`PUT http://localhost:8080/api/cards/1/status?status=ACTIVE`

- Replace `1` with the actual card ID.
- Status options: `ACTIVE`, `BLOCKED`

---

## 💰 7. Update Credit Limit of a Card

**Endpoint:**  
`PUT http://localhost:8080/cards/1/limit?newLimit=60000`

- Replace `1` with the actual card ID.
- `newLimit` is the new credit limit to be assigned.
