# Meeting Room Booking API

REST API for booking meeting rooms built with Spring Boot 4.

## Requirements

- Java 17+
- Maven 3.6+

## Quick Start

```bash
# Clone and run
mvn spring-boot:run
```

The application starts at `http://localhost:8080`

## API Endpoints

### Create Booking
```bash
POST /api/bookings
Content-Type: application/json

{
  "roomId": 1,
  "startTime": "2026-01-25T10:00:00",
  "endTime": "2026-01-25T11:00:00"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "roomId": 1,
  "roomName": "Conference Room A",
  "startTime": "2026-01-25T10:00:00",
  "endTime": "2026-01-25T11:00:00",
  "status": "BOOKED"
}
```

### Cancel Booking
```bash
DELETE /api/bookings/{id}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "roomId": 1,
  "roomName": "Conference Room A",
  "startTime": "2026-01-25T10:00:00",
  "endTime": "2026-01-25T11:00:00",
  "status": "CANCELED"
}
```

### List Bookings by Room
```bash
GET /api/rooms/{roomId}/bookings
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "roomId": 1,
    "roomName": "Conference Room A",
    "startTime": "2026-01-25T10:00:00",
    "endTime": "2026-01-25T11:00:00",
    "status": "BOOKED"
  }
]
```

## Available Rooms

The following rooms are created on startup:

| ID | Name |
|----|------|
| 1 | Conference Room A |
| 2 | Conference Room B |
| 3 | Meeting Room 1 |
| 4 | Meeting Room 2 |
| 5 | Board Room |

## Business Rules

- Bookings cannot overlap for the same room
- Bookings cannot be in the past
- Start time must be before end time

## Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid input or validation error |
| 404 | Room or booking not found |
| 409 | Booking overlaps with existing booking |

## H2 Console

Access the database console at `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:bookingdb`
- Username: `sa`
- Password: *(empty)*

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/main/java/com/tomato/rising_star_2026/
├── config/           # DataInitializer
├── controller/       # REST controllers
├── dto/              # Request/Response DTOs
├── exception/        # Custom exceptions & handler
├── model/            # JPA entities
├── repository/       # Spring Data repositories
└── service/          # Business logic
```

## Tech Stack

- Spring Boot 4.0.1
- Spring Data JPA
- H2 Database
- Lombok
- JUnit 5 + Mockito
