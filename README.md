# Group Project Cloud Management System

A web application for coordinating student team projects, developed for a Software Engineering course at Tongji University.

## Features

- Account registration, password and verification-code login, and profile management.
- Group creation, membership applications, member management, and leadership transfer.
- Task assignment, subtasks, deadlines, progress tracking, and Gantt charts.
- Group message boards with replies and read tracking.
- Shared file uploads, downloads, and resource management.

## Tech Stack

- **Frontend:** Vue 3, Vue Router, Vuex, Element Plus, Axios, DHTMLX Gantt.
- **Backend:** Java 17, Spring Boot 3.3, MyBatis, Maven.
- **Database:** MySQL 8.
- **Notifications:** SMTP email and Aliyun SMS.

## Project Structure

```text
FrontEnd/             Vue application
BackEnd/              Spring Boot REST API
cloud_management.sql  Database schema
```

## Local Setup

1. Create a MySQL database named `cloud_management` and import `cloud_management.sql`.
2. Set `DB_USERNAME` and `DB_PASSWORD`; optionally override `DB_URL` in the backend environment.
3. For email, set `MAIL_USERNAME` and `MAIL_PASSWORD`. For SMS, set `ALIYUN_ACCESS_KEY_ID`, `ALIYUN_ACCESS_KEY_SECRET`, `ALIYUN_SMS_SIGN_NAME`, and `ALIYUN_SMS_TEMPLATE_CODE`.
4. Start the backend with Java 17 and Maven:

   ```sh
   cd BackEnd
   mvn spring-boot:run
   ```

5. In another terminal, start the frontend:

   ```sh
   cd FrontEnd
   npm install
   npm run serve
   ```

The frontend API defaults to `http://localhost:8080`; set `VUE_APP_API_BASE_URL` to use another backend. This repository preserves a coursework implementation; external services require your own credentials.
