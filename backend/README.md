# Online Judge - Backend

This is the backend for an online judge platform, built with **Spring Boot**. It provides RESTful APIs for managing users, problems, submissions, and authentication.

## Features

* **User Authentication:** Secure user registration and login using JWT (JSON Web Tokens).
* **Problem Management:** CRUD operations for programming problems, including test cases and tags.
* **Code Submissions:** Submit code for a specific problem and receive a verdict.
* **Role-Based Access Control:** Differentiates between regular users and admins, with specific permissions for certain actions.

## Technologies Used

* **Java 21**
* **Spring Boot**
* **Spring Security** for authentication and authorization.
* **Spring Data JPA** for database interactions.
* **PostgreSQL** as the relational database.
* **Maven** for project management.
* **JUnit 5**, **Mockito**, and **Testcontainers** for testing.
* **Spotless** for code formatting.
* **JaCoCo** for code coverage analysis.
