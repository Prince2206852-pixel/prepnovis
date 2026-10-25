# PrepNovis — AI-Powered Interview Preparation

PrepNovis is a full-stack interview preparation platform designed to help developers practise technical questions, run mock interviews, and improve their answers with AI-powered feedback.

## Features

- **Question Management:** Save and organise interview questions for focused preparation.
- **Practice Sessions:** Create practice sessions and track answers.
- **AI Answer Evaluation:** Receive structured feedback, scores, strengths, and areas for improvement.
- **AI Mock Interviews:** Generate interview questions for practice.
- **Authentication:** JWT-based authentication and Google sign-in.
- **Email Verification:** Account verification and password recovery workflows.
- **Performance:** Redis caching and request rate limiting.
- **Monitoring:** Application metrics and monitoring integrations.
- **API Documentation:** OpenAPI/Swagger documentation.
- **Error Handling and Testing:** Centralised exception handling and automated tests.

## Technology Stack

**Backend:** Java 21, Spring Boot, Spring Security, Spring Data JPA

**Database and caching:** PostgreSQL, Redis

**AI integration:** Gemini API

**Frontend:** Next.js, React, TypeScript

**Infrastructure and tooling:** Docker, Kafka, Prometheus, Grafana, GitHub Actions

> Technology list reflects the intended project stack; verify each item against the current implementation before publishing.

## Architecture

The application separates frontend, REST API, business logic, persistence, and AI integration into dedicated components. Authentication and rate limiting protect API access, while caching helps reduce repeated database work.

## Running Locally

1. Clone the repository.
2. Configure the required environment variables using the example configuration.
3. Start the required database and supporting services.
4. Run the Spring Boot backend and frontend using the instructions in their respective directories.

Refer to the backend and frontend directories for detailed setup instructions.

## Project Links

- **Source code:** https://github.com/Prince2206852-pixel/prepnovis
- **Portfolio:** https://my-name-is-prince.netlify.app/

## Security

Never commit real API keys, passwords, JWT secrets, OAuth credentials, or production environment files. Use environment variables for sensitive configuration.

## Author

Prince Kumar — Java Backend Developer

This project demonstrates full-stack development, REST API design, authentication, caching, AI integration, and application monitoring.
