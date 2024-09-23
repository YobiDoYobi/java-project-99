### Hexlet tests and linter status:
[![Actions Status](https://github.com/YobiDoYobi/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/YobiDoYobi/java-project-99/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/9c80f6133a726436a268/maintainability)](https://codeclimate.com/github/YobiDoYobi/java-project-99/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/9c80f6133a726436a268/test_coverage)](https://codeclimate.com/github/YobiDoYobi/java-project-99/test_coverage)

Task Manager is a task management system. It allows you to set tasks, assign performers and change their statuses. To work with the system, registration and authentication are required.

Example application
https://java-project-99-un5j.onrender.com

Default login login: `hexlet@example.com`
password: `qwerty`

API description: localhost:port/swagger-ui.html


## Requirements

* JDK 22
* Gradle 8.10
* GNU Make

## Stack
* Spring Boot
* Spring Security
* Spring Data JPA
* H2 & PostgreSQL
* Sentry
* Swagger

## Setup and run

```bash
make build
```
## Run checkstyle
```bash
make lint
```
## Run tests
```bash
make test
```