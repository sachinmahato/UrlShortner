# Agentic Software Engineering System - URL Shortener (Java/Spring Boot)

This repository contains a production-grade prototype for an agentic SDLC orchestration system built to fulfill the AI Engineering Assessment[cite: 1, 2, 3]. It translates high-level engineering requirements into validated, reviewable software outcomes through controlled agent autonomy, explicit dependency graphs, and human-in-the-loop governance[cite: 1, 2, 3].

## Architecture Overview
- **Agent Orchestrator (`AgentOrchestrator.java`)**: Manages non-linear, stateful workflow execution, dependency graph resolution, bounded retries, fallbacks, rollbacks, and audit telemetry.
- **URL Shortener Service (`UrlShortenerService.java`)**: Core Spring Boot domain implementation providing robust API creation and redirection logic[cite: 1].
- **Governance**: Enforces human approval checkpoints for high-impact lifecycle actions[cite: 2, 3].

## Quick Start & Setup Instructions
1. Ensure **Java 17+** and **Maven** are installed.
2. Build the project:
   ```bash
   mvn clean install