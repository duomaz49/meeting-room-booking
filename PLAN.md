# Meeting Room Booking API – Plan

## 1. Objective
Implement a simple API for booking meeting rooms with the following features:
- **Create a booking**: Reserve a room for a specific time interval.
- **Cancel a booking**: Remove (soft-delete) a reservation.
- **List bookings**: Retrieve all active bookings for a room.

---

## 2. Business Rules
- Bookings **cannot overlap** for the same room.
- Bookings **cannot be in the past**.
- **Start time must be before end time**.

---

## 3. Technology & Setup
- **Language**: Java
- **Framework**: Spring Boot
- **Persistence**: H2 in-memory database
- **Validation**: `spring-boot-starter-validation`
- **Boilerplate reduction**: Lombok
- **Global exceptions**: Centralized handling for business and validation errors
- **Comprehensive testing**: Unit and integration tests for all endpoints and business rules
- **No authentication/security**: Can be implemented later

### Why H2 Database? 
This project uses H2 in-memory database as part of an **MVP (Minimum Viable Product)** approach:

1. **Fast development**: No external database setup required - start coding immediately
2. **Easy demonstration**: Customer can review a working product without infrastructure dependencies
3. **Zero configuration**: Works out of the box with Spring Boot auto-configuration
4. **Seamless migration path**: Spring Data JPA abstracts the database layer - switching to PostgreSQL, MySQL, or other production databases requires only:
   - Change `application.properties` connection settings
   - Add the appropriate database driver dependency
   - No code changes needed

**Workflow**: MVP approval → Production database migration → Continue development

---

## 4. Domain Model

### Room
- `id` (PK)
- `name`

### Booking
- `id` (PK)
- `room_id` (FK → Room)
- `start_time`
- `end_time`
- `status` (BOOKED / CANCELED)

**Relationship:** Room 1 → * Booking

---

## 5. RESTful Architecture Principles

This API follows **flat RESTful resource design** for consistency and scalability:

### Design Principles
- **Resources are nouns**: `/api/bookings`, `/api/rooms`
- **HTTP methods define actions**: GET (read), POST (create), PUT (update), DELETE (remove)
- **Query parameters for filtering**: `?roomId=1`
- **Consistent base paths**: Each resource has its own base endpoint
- **Stateless**: Each request contains all necessary information

### Resource Structure
```
/api/bookings          → Booking resource (CRUD)
/api/rooms             → Room resource (CRUD - future)
```

---

## 6. API Endpoints

### Current Endpoints (Bookings)

| Action | Method | Endpoint | Notes |
|--------|--------|----------|------|
| Create booking | POST | `/api/bookings` | Validate overlap, start < end, not in past |
| List bookings | GET | `/api/bookings?roomId={id}` | Filter by room, only active (BOOKED) |
| Cancel booking | DELETE | `/api/bookings/{id}` | Soft delete (status = CANCELED) |

### Future Endpoints (Rooms - when needed)

| Action | Method | Endpoint | Notes |
|--------|--------|----------|------|
| List all rooms | GET | `/api/rooms` | Return all rooms |
| Get room | GET | `/api/rooms/{id}` | Return single room |
| Create room | POST | `/api/rooms` | Create new room |
| Update room | PUT | `/api/rooms/{id}` | Update room details |
| Delete room | DELETE | `/api/rooms/{id}` | Remove room |

---

## 7. Validation Layers
- **DTO / Controller**: Null checks, start < end, future times
- **Service**: Database-dependent rules (overlaps, room existence, cancellation rules)

---

## 8. Assumptions
1. Rooms are pre-existing; creation is out of scope. A set of rooms are created when running the application.
2. Cancellation is soft-delete (status = CANCELED).
3. Time zone is server local time.
4. Only active bookings are checked for overlaps.
5. Authentication/authorization is out of scope.

---

## 9. Error Handling
- **Global exceptions** handle all business and validation errors
- 400 Bad Request → invalid input, past dates, start ≥ end
- 404 Not Found → room or booking not found
- 409 Conflict → overlapping booking

---

## 10. Testing
- **Unit tests**: Service layer and validation logic
- **Integration tests**: Endpoints with in-memory H2 DB
- **Coverage**: All business rules, overlap checks, cancellations, listing bookings  
