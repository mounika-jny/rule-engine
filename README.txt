# Simple Rule Engine — React + Spring Boot (H2)

This bundle contains a fully runnable starter kit.

## Structure
- backend/ : Spring Boot app (Java 17, H2 DB, JPA/Web/Validation)
- frontend/ : React (Vite) app

## Prereqs
- Java 17+
- Maven 3.9+
- Node 18+ / npm

## Run backend
```bash
cd backend
mvn spring-boot:run
```
H2 console: http://localhost:8080/h2  
APIs base: http://localhost:8080

## Run frontend
```bash
cd frontend
npm install
npm run dev
```
App available at http://localhost:5173

## Try it
In the UI:
- App: App1
- Entities: Employee, Department
- Query: `Department ids in (:deptSet) and employeeIds in (:empSet)`
Click **Create**, then **Run Evaluation**.
