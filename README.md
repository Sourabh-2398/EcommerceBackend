# E-commerce Microservices - Order & Inventory Management

A comprehensive Spring Boot microservices solution for managing product orders and inventory with batch tracking and expiry date management. This project demonstrates best practices in microservice architecture, including factory design patterns, RESTful APIs, and comprehensive testing.

## 📋 Project Overview

This system consists of two main microservices:

### 1. **Inventory Service**
- Maintains product inventory with batch-level tracking
- Tracks expiry dates for each batch
- Provides endpoints to retrieve inventory sorted by expiry date
- Supports inventory updates after orders are placed
- Implements Factory Pattern for extensible inventory strategies

### 2. **Order Service**
- Accepts and processes customer orders
- Communicates with Inventory Service to check availability
- Reserves inventory from appropriate batches
- Updates inventory upon successful order placement
- Provides order tracking and management

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Database**: H2 (in-memory)
- **Migrations**: Liquibase
- **Testing**: JUnit 5, Mockito
- **API Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven
- **ORM**: Spring Data JPA (Hibernate)
- **REST Communication**: RestTemplate, WebFlux

### Project Structure

```
EcommerceBackend/
├── src/
│   ├── main/
│   │   ├── java/com/example/EcommerceBackend/
│   │   │   ├── config/                    # Configuration classes
│   │   │   │   └── RestTemplateConfig.java
│   │   │   ├── inventory/
│   │   │   │   ├── controller/            # REST endpoints
│   │   │   │   ├── service/               # Business logic
│   │   │   │   ├── repository/            # Data access layer
│   │   │   │   ├── entity/                # JPA entities
│   │   │   │   ├── dto/                   # Data transfer objects
│   │   │   │   └── factory/               # Factory pattern implementation
│   │   │   ├── order/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   └── dto/
│   │   │   └── EcommerceBackend.java
│   │   └── resources/
│   │       ├── application.properties      # Application config
│   │       ├── db/
│   │       │   ├── changelog/             # Liquibase migrations
│   │       │   │   ├── db.changelog-master.xml
│   │       │   │   ├── 001-create-inventory-batch-table.xml
│   │       │   │   ├── 002-create-order-table.xml
│   │       │   │   ├── 003-load-inventory-data.xml
│   │       │   │   └── 004-load-order-data.xml
│   │       │   └── data/                  # CSV seed data
│   │       │       ├── inventory_batch.csv
│   │       │       └── orders.csv
│   └── test/
│       └── java/com/example/EcommerceBackend/
│           ├── inventory/
│           │   ├── service/
│           │   │   └── InventoryServiceTest.java    # Unit tests
│           │   └── controller/
│           │       └── InventoryControllerIntegrationTest.java
│           └── order/
│               ├── service/
│               │   └── OrderServiceTest.java
│               └── controller/
│                   └── OrderControllerIntegrationTest.java
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Installation & Setup

1. **Clone the Repository**
   ```bash
   cd EcommerceBackend
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`

4. **Verify Database Initialization**
   - Access H2 Console: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:ecommerce`
   - Username: `sa`
   - Password: (empty)

## 📖 API Documentation

### Swagger UI
Access the interactive API documentation at: **http://localhost:8080/swagger-ui.html**

### Inventory Service Endpoints

#### 1. Get Inventory by Product ID
```
GET /inventory/{productId}
```

**Example Request:**
```bash
curl -X GET "http://localhost:8080/inventory/1001" \
  -H "Content-Type: application/json"
```

**Example Response:**
```json
{
  "productId": 1001,
  "productName": "Laptop",
  "batches": [
    {
      "batchId": 1,
      "quantity": 68,
      "expiryDate": "2026-06-25"
    }
  ],
  "totalQuantity": 68
}
```

**Response Fields:**
- `productId`: Unique product identifier
- `productName`: Name of the product
- `batches`: List of inventory batches sorted by expiry date (earliest first)
  - `batchId`: Unique batch identifier
  - `quantity`: Available quantity in this batch
  - `expiryDate`: Batch expiry date (YYYY-MM-DD)
- `totalQuantity`: Total available quantity across all batches

#### 2. Update Inventory
```
POST /inventory/update
```

**Example Request:**
```bash
curl -X POST "http://localhost:8080/inventory/update" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1001,
    "quantityToReduce": 10,
    "batchIds": "1,2"
  }'
```

**Request Body:**
```json
{
  "productId": 1001,
  "quantityToReduce": 10,
  "batchIds": "1,2"
}
```

**Response:**
```
Inventory updated successfully
```

#### 3. Check Inventory Availability (Internal Endpoint)
```
GET /inventory/check/{productId}/{quantity}
```

**Example Request:**
```bash
curl -X GET "http://localhost:8080/inventory/check/1001/50"
```

**Response:**
```
true
```

### Order Service Endpoints

#### 1. Place Order
```
POST /order
```

**Example Request:**
```bash
curl -X POST "http://localhost:8080/order" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1002,
    "quantity": 3
  }'
```

**Request Body:**
```json
{
  "productId": 1002,
  "quantity": 3
}
```

**Example Response (201 Created):**
```json
{
  "orderId": 5012,
  "productId": 1002,
  "productName": "Smartphone",
  "quantity": 3,
  "status": "PLACED",
  "reservedFromBatchIds": [9, 10],
  "message": "Order placed. Inventory reserved."
}
```

**Response Fields:**
- `orderId`: Unique order identifier (auto-generated)
- `productId`: Product being ordered
- `productName`: Name of the product
- `quantity`: Quantity ordered
- `status`: Order status (PLACED, SHIPPED, DELIVERED)
- `reservedFromBatchIds`: List of batch IDs from which inventory was reserved
- `message`: Confirmation message

**Error Responses:**
- `400 Bad Request`: Insufficient inventory or invalid product
- `500 Internal Server Error`: Service communication failure

## 🏭 Factory Design Pattern Implementation

The project uses the Factory Pattern for inventory handling, allowing extensible strategies:

### Strategy Interface
```java
public interface InventoryStrategy {
    List<InventoryBatch> getAvailableInventory(List<InventoryBatch> batches);
    Integer calculateTotalQuantity(List<InventoryBatch> batches);
}
```

### Implementations

1. **DefaultInventoryStrategy**
   - Returns all non-expired batches
   - Sorted by expiry date (earliest first)
   - Basic FIFO approach

2. **ExpiryPriorityInventoryStrategy**
   - Prioritizes batches expiring within 30 days
   - Useful for FEFO (First Expiry First Out) approach
   - Reduces waste of expiring products

### Factory Usage
```java
InventoryStrategy strategy = strategyFactory.getStrategy("DEFAULT");
List<InventoryBatch> available = strategy.getAvailableInventory(batches);
```

### Extending with New Strategies
1. Create a new class implementing `InventoryStrategy`
2. Register it in `InventoryStrategyFactory`
3. Use the factory to instantiate the strategy

## 📊 Sample Data

### Inventory Batches
The system is initialized with 10 inventory batches across 5 products:

| Batch ID | Product ID | Product Name | Quantity | Expiry Date |
|----------|-----------|--------------|----------|------------|
| 1 | 1001 | Laptop | 68 | 2026-06-25 |
| 2 | 1005 | Smartwatch | 52 | 2026-05-30 |
| 3 | 1004 | Headphones | 20 | 2026-08-12 |
| 4 | 1003 | Tablet | 35 | 2026-09-03 |
| 5 | 1005 | Smartwatch | 39 | 2026-03-31 |
| 6 | 1004 | Headphones | 56 | 2026-06-06 |
| 7 | 1005 | Smartwatch | 40 | 2026-04-24 |
| 8 | 1003 | Tablet | 21 | 2026-09-09 |
| 9 | 1002 | Smartphone | 29 | 2026-05-31 |
| 10 | 1002 | Smartphone | 83 | 2026-11-15 |

### Orders
The system is pre-populated with 10 orders for demonstration.

## 🧪 Testing

### Running Tests

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=InventoryServiceTest
```

#### Run with Coverage
```bash
mvn clean test jacoco:report
```

### Test Coverage

#### Unit Tests
- **InventoryServiceTest.java**: Tests for inventory business logic
  - Test inventory retrieval and filtering
  - Test inventory updates
  - Test availability checking
  - Test batch reservation

- **OrderServiceTest.java**: Tests for order processing
  - Test successful order placement
  - Test insufficient inventory handling
  - Test service communication failures

#### Integration Tests
- **InventoryControllerIntegrationTest.java**: End-to-end inventory API tests
  - Test GET endpoints
  - Test batch sorting by expiry date
  - Test inventory updates via REST
  - Test availability checking

- **OrderControllerIntegrationTest.java**: End-to-end order processing tests
  - Test successful order placement
  - Test inventory reservation
  - Test multiple orders
  - Test error scenarios

### Test Database
- Tests use H2 in-memory database
- Liquibase migrations run automatically
- Sample data is loaded before each test
- Transactions are rolled back after tests

## 🔄 Inter-Service Communication

### Request Flow: Order Placement

```
Client
  ↓
Order Service (POST /order)
  ↓
[Check Inventory] → Inventory Service (GET /inventory/{productId})
  ↓
[Reserve Batches] → Inventory Service (Internal logic)
  ↓
[Create Order] → Order Database
  ↓
[Update Inventory] → Inventory Service (POST /inventory/update)
  ↓
[Return Response] → Client
```

### Technologies Used
- **RestTemplate**: Synchronous HTTP client for service-to-service communication
- **WebFlux**: Asynchronous web framework (configured but optional for reactive endpoints)
- **Error Handling**: Graceful degradation with fallback mechanisms

## 📝 Configuration

### Application Properties
Located in `src/main/resources/application.properties`

```properties
# Server Configuration
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:ecommerce
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

### Customization
To change the port or database, modify the `application.properties` file before running the application.

## 🗄️ Database Schema

### INVENTORY_BATCH Table
```sql
CREATE TABLE inventory_batch (
    batch_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ORDERS Table
```sql
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PLACED',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reserved_batch_ids VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🛠️ Development

### IDE Setup
- **IntelliJ IDEA**: Open project root as Maven project
- **Eclipse**: Import as "Existing Maven Projects"
- **VS Code**: Install "Extension Pack for Java"

### Code Style
- Follow Google Java Style Guide
- Use Lombok annotations to reduce boilerplate
- Document public methods and classes with JavaDoc

### Best Practices
- Layer separation: Controller → Service → Repository
- Dependency injection via constructor
- Immutable DTOs where possible
- Transaction management at service layer
- Comprehensive logging throughout

## 🐛 Troubleshooting

### Issue: Application fails to start
**Solution**: Ensure Java 17+ is installed and Maven dependencies are downloaded
```bash
mvn clean install
```

### Issue: H2 Console not accessible
**Solution**: Check that `spring.h2.console.enabled=true` in application.properties

### Issue: Liquibase migrations fail
**Solution**: Check CSV file paths and ensure data files exist in correct location

### Issue: Tests fail with database errors
**Solution**: Clear Maven cache and rebuild
```bash
rm -rf ~/.m2/repository/com/example
mvn clean test
```

## 📚 Reference Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Liquibase Documentation](https://www.liquibase.org/get-started/best-practices)
- [Swagger/OpenAPI](https://swagger.io/specification/)
- [Factory Design Pattern](https://refactoring.guru/design-patterns/factory-method)

## 📄 License

This project is provided as-is for educational purposes.

## 👤 Author

Developed as a comprehensive microservices assignment demonstrating best practices in Spring Boot development.

## ✅ Checklist - Assignment Requirements

- ✅ Two Spring Boot microservices (Order & Inventory)
- ✅ REST API communication between services
- ✅ Factory Design Pattern implementation
- ✅ Spring Data JPA with H2 database
- ✅ Liquibase database migrations
- ✅ CSV data loading
- ✅ Unit tests (JUnit 5, Mockito)
- ✅ Integration tests (@SpringBootTest, H2)
- ✅ Swagger/OpenAPI documentation
- ✅ Lombok for reduced boilerplate
- ✅ Proper layering (Controller, Service, Repository)
- ✅ Extensible architecture
- ✅ Comprehensive README with setup & API docs
- ✅ Maven build configuration
- ✅ Java 17 compatibility

---

**Last Updated**: February 2026  
**Status**: Complete and Ready for Production Testing

