# 💰 Expense Microservices

A robust, distributed **Expense Tracking Application** built with **Java Spring Boot** and **Spring Cloud**. 
This project demonstrates a modern microservices architecture with service discovery, centralized gateway routing, and a thymeleaf-based frontend.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.1-green)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![Render](https://img.shields.io/badge/Deployment-Render-purple)

---

## 🏗️ Architecture

The system consists of **4 independent microservices**:

1.  **Eureka Server** (`:8761`): Service Registry & Discovery.
2.  **API Gateway** (`:8080`): Central entry point that routes traffic to specific services.
3.  **User Service** (`:8082`): Handles User Registration, Authentication, and Session Management.
4.  **Expense Service** (`:8081`): Manages Expenses, Budget Categories, and Dashboard Analytics.

*(Communication uses **Spring Cloud OpenFeign** for inter-service calls)*

---

## 🚀 Key Features

- **Microservices Pattern**: Decoupled services for scalability.
- **Service Discovery**: Automatic detection of services via Eureka.
- **Budget Tracking**: Visual progress bars showing spending vs. category limits.
- **Secure Deletion**: Logic to prevent unauthorized deletion of expenses.
- **Dynamic Dashboard**: Real-time updates of remaining salary and category breakdown.
- **Dockerized**: specific `Dockerfile` for each service + `render.yaml` for 1-click cloud deployment.

---

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3, Spring Cloud (Gateway, Eureka, OpenFeign).
- **Frontend**: Thymeleaf, Bootstrap 5, Glassmorphism CSS.
- **Database**: H2 In-Memory Database (for development simplicity).
- **Deployment**: Docker, Render.

---

## 🏃‍♂️ How to Run Locally

### Prerequisites
- Java 17+
- Maven

### Steps
1.  **Start Eureka Server**:
    ```bash
    cd eureka-server
    mvn spring-boot:run
    ```
2.  **Start User Service**:
    ```bash
    cd user-service
    mvn spring-boot:run
    ```
3.  **Start Expense Service**:
    ```bash
    cd expense-service
    mvn spring-boot:run
    ```
4.  **Start API Gateway**:
    ```bash
    cd api-gateway
    mvn spring-boot:run
    ```
5.  **Access the App**:
    Open [http://localhost:8080](http://localhost:8080) (Gateway) or [http://localhost:8082/login](http://localhost:8082/login)

---

## 🐳 How to Deploy on Render

This project is configured for **1-click deployment** on Render.

1.  Push this repository to **GitHub**.
2.  Login to **Render**.
3.  Click **New +** -> **Blueprint**.
4.  Connect your repository.
5.  Render will auto-detect `render.yaml` and deploy all services!

---

## 🔒 Security

- **Safe Deletion**: Users can only delete their own expenses.
- **Redirection**: Deleting the last item in a category smartly redirects to the dashboard.
- **Auth**: Simple Session-based authentication managed by User Service.

---

Made with ❤️ by Karanvir Singh using Spring Boot.
