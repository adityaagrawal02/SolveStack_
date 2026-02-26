
# 🚀 SolveStack – Open Innovation Collaboration Platform (Academic Version)

## 📌 Project Overview

**SolveStack** is a Java-based Open Innovation Collaboration Platform designed to connect companies with developers and researchers through structured problem-solving workflows.

In this academic prototype, companies can post challenges, developers can submit solutions, and evaluators can review and score submissions through a defined evaluation process.

This project simulates a **Corporate R&D Marketplace** in an academic environment using Object-Oriented Programming principles.

---

## 🎯 Problem Statement

There is no structured academic platform that:

* Allows companies to post real-world challenges
* Enables students/developers to submit solutions
* Provides a transparent evaluation workflow
* Tracks submissions and challenge lifecycle

SolveStack addresses this gap by providing a modular, role-based innovation system.

---

## 👥 Target Users

* 🏢 Companies – Post challenges
* 👩‍💻 Developers / Researchers – Submit solutions
* 🧑‍⚖️ Evaluators – Review and score submissions
* 🛠 Admin – Manage platform operations

---

## ✨ Key Features (Phase 1)

* Role-based user system
* Challenge posting and management
* Submission tracking system
* Evaluation workflow
* Status monitoring
* Console-based prototype implementation

---

## 🧱 System Architecture (OOP Based)

The system is designed using Object-Oriented Programming principles:

### 🔹 Encapsulation

Private fields with controlled access using getters and setters.

### 🔹 Inheritance

Base class:

```
User
```

Derived classes:

```
Company
Developer
Evaluator
Admin
```

### 🔹 Polymorphism

Method overriding for:

* Dashboard views
* Role-specific operations

### 🔹 Abstraction

Abstract methods in base class to enforce implementation in derived classes.

### 🔹 Interface

`Evaluable` interface implemented by Evaluator for submission scoring.

---

## 🏗 Project Structure (Sample)

```
SolveStack/ 
│ 
├── models/ 
│   │ 
│   ├── User.java 
│   │      ├── login() 
│   │      ├── logout() 
│   │      ├── viewDashboard() 
│   │      └── updateProfile() 
│   │ 
│   ├── Company.java 
│   │      ├── createChallenge() 
│   │      ├── editChallenge() 
│   │      ├── closeChallenge() 
│   │      └── viewSubmissions() 
│   │ 
│   ├── Developer.java 
│   │      ├── browseChallenges() 
│   │      ├── submitSolution() 
│   │      ├── trackSubmission() 
│   │      └── withdrawSubmission() 
│   │ 
│   ├── Evaluator.java 
│   │      ├── evaluateSubmission() 
│   │      ├── giveFeedback() 
│   │      └── assignScore() 
│   │ 
│   ├── Admin.java 
│   │      ├── verifyUser() 
│   │      ├── banUser() 
│   │      ├── approveChallenge() 
│   │      └── viewReports() 
│   │ 
│   ├── Challenge.java 
│   │      ├── openChallenge() 
│   │      ├── updateDeadline() 
│   │      ├── addSubmission() 
│   │      └── closeChallenge() 
│   │ 
│   └── Submission.java 
│          
├── uploadDocument() 
│          
│          
│          
│ 
├── attachGithubLink() 
├── updateStatus() 
└── calculateScore() 
├── services/ 
│   │ 
│   ├── ChallengeService.java 
│   │      ├── createChallenge() 
│   │      ├── deleteChallenge() 
│   │      ├── getAllChallenges() 
│   │      ├── searchChallenges() 
│   │      └── assignEvaluator() 
│   │ 
│   ├── SubmissionService.java 
│   │      ├── submitSolution() 
│   │      ├── validateSubmission() 
│   │      ├── fetchSubmissionsByChallenge() 
│   │      └── updateSubmissionStatus() 
│   │ 
│   └── EvaluationService.java 
│          
├── evaluateSubmission() 
│          
│          
│          
├── calculateFinalScore() 
├── generateLeaderboard() 
└── publishResults() 
── exceptions/ 
│ ├── SolveStackException.java 
│ ├── UserNotFoundException.java 
│ ├── ChallengeNotFoundException.java 
│ ├── SubmissionDeadlineException.java 
│ ├── UnauthorizedAccessException.java 
│ └── GlobalExceptionHandler.java 
│ 
├── main/ 
│   │ 
│   └── SolveStackApp.java 
│ 
└── README.md
```

---

## 🛠 Technologies Used

* Java (Core OOP Concepts)
* IntelliJ IDEA / Eclipse
* Git & GitHub (Version Control)
* UML (System Design)

---

## 🔄 Development Methodology

* Requirement Analysis
* UML Class Design
* Modular Implementation
* Incremental Development Approach

---

## 📈 Future Scope (Phase 2+)

* Database integration (MySQL)
* GUI or Web Interface
* Spring Boot Backend
* REST APIs
* Authentication & Authorization
* Cloud deployment (AWS / Azure)
* AI-assisted evaluation
* Global innovation scaling

---

## 🎓 Academic Objective

This project demonstrates:

* Effective use of OOP concepts
* Real-world system modeling
* Modular architecture
* Scalable system design

---

## 📂 Repository Guidelines

* All group members added as contributors
* Regular commits with meaningful messages
* Proper package structure maintained
* Clean and documented code

