# 📚 Complete Project Index & Navigation Guide

## Welcome to Körber E-Commerce Microservices

This document provides a complete navigation guide for the project implementation.

---

## 📂 DOCUMENTATION FILES

### Quick Start
| Document | Purpose | Read Time |
|----------|---------|-----------|
| **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** | Fast setup & API reference | 5 min |
| **[README.md](README.md)** | Complete documentation | 15 min |
| **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** | Technical details | 10 min |
| **[DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md)** | Deployment guide | 10 min |

### Which document should I read?

- **Just want to run the app?** → [QUICK_REFERENCE.md](QUICK_REFERENCE.md)
- **Need complete setup & API docs?** → [README.md](README.md)
- **Want to understand implementation?** → [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)
- **Planning to deploy?** → [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md)
- **Everything?** → Read in order below

---

## 🚀 GETTING STARTED (5 minutes)

### 1. Prerequisites Check
```bash
java -version          # Should be 17 or higher
mvn -version           # Should be 3.6 or higher
```

### 2. Build
```bash
cd /Users/sourabh23k17/IdeaProjects/EcommerceBackend/
mvn clean install
```

### 3. Run
```bash
mvn spring-boot:run
```

### 4. Access
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Database**: http://localhost:8080/h2-console
- **Inventory**: http://localhost:8080/inventory/1001

---

## 📖 DOCUMENTATION STRUCTURE

### 📄 README.md (Primary Documentation)
**Content:**
- Architecture overview
- Feature descriptions
- Complete API documentation with examples
- Database schema
- Testing instructions
- Configuration guide
- Troubleshooting

**Use when:** You need comprehensive project information

### 📋 QUICK_REFERENCE.md (Quick Lookup)
**Content:**
- Project structure tree
- Quick start commands
- API endpoint quick reference
- Sample cURL commands
- Configuration properties
- Troubleshooting quick fixes

**Use when:** You need fast access to specific information

### 🔧 IMPLEMENTATION_SUMMARY.md (Technical Details)
**Content:**
- Project completion status
- Detailed deliverables list
- File-by-file breakdown
- Design pattern explanation
- Technical stack details
- Compliance checklist

**Use when:** You want to understand implementation details

### 📝 DEPLOYMENT_CHECKLIST.md (Operations)
**Content:**
- Pre-deployment verification
- Step-by-step deployment
- Production considerations
- Troubleshooting guide
- Rollback procedures

**Use when:** Preparing for deployment

---

## 🏗️ PROJECT ARCHITECTURE

### Microservices

```
┌─────────────────────────────────────────┐
│     Order Service                       │
│  (Processes & places orders)            │
│                                         │
│  POST /order                            │
│  └─ Calls Inventory Service             │
└──────────────┬──────────────────────────┘
               │ (REST via RestTemplate)
               │
┌──────────────▼──────────────────────────┐
│  Inventory Service                      │
│  (Manages stock & batches)              │
│                                         │
│  GET /inventory/{id}                    │
│  POST /inventory/update                 │
│  GET /inventory/check/{id}/{qty}        │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│  H2 In-Memory Database                  │
│  (Liquibase migrations)                 │
│  - INVENTORY_BATCH table                │
│  - ORDERS table                         │
└─────────────────────────────────────────┘
```

### Design Patterns

**Factory Pattern** (Inventory Service)
```
InventoryStrategyFactory
├── DefaultInventoryStrategy
│   └── Returns non-expired batches sorted by expiry
└── ExpiryPriorityInventoryStrategy
    └── Prioritizes batches expiring within 30 days (FEFO)
```

### Layered Architecture

```
┌──────────────────────────┐
│   Controller             │  REST Endpoints
│   (REST APIs)            │
└──────────────┬───────────┘
               │
┌──────────────▼───────────┐
│   Service                │  Business Logic
│   (InventoryService,     │  Transactions
│    OrderService)         │  Validation
└──────────────┬───────────┘
               │
┌──────────────▼───────────┐
│   Repository             │  Database Access
│   (Spring Data JPA)      │  Queries
└──────────────┬───────────┘
               │
┌──────────────▼───────────┐
│   Entity                 │  Data Models
│   (JPA Entities)         │
└──────────────────────────┘
```

---

## 📂 DIRECTORY STRUCTURE

### Source Code
```
src/main/java/com/example/EcommerceBackend/
├── config/                     # Configuration beans
├── inventory/                  # Inventory microservice
│   ├── controller/            # REST endpoints
│   ├── service/               # Business logic
│   ├── repository/            # Data access
│   ├── entity/                # Database entities
│   ├── dto/                   # Data transfer objects
│   └── factory/               # Factory pattern
└── order/                      # Order microservice
    ├── controller/
    ├── service/
    ├── repository/
    ├── entity/
    └── dto/
```

### Resources
```
src/main/resources/
├── application.properties      # Configuration
└── db/
    ├── changelog/             # Liquibase migrations
    │   ├── db.changelog-master.xml
    │   ├── 001-create-inventory-batch-table.xml
    │   ├── 002-create-order-table.xml
    │   ├── 003-load-inventory-data.xml
    │   └── 004-load-order-data.xml
    └── data/                  # CSV seed data
        ├── inventory_batch.csv
        └── orders.csv
```

### Tests
```
src/test/java/com/example/EcommerceBackend/
├── inventory/
│   ├── service/InventoryServiceTest.java
│   └── controller/InventoryControllerIntegrationTest.java
└── order/
    ├── service/OrderServiceTest.java
    └── controller/OrderControllerIntegrationTest.java
```

---

## 🔑 KEY COMPONENTS

### Entities
| Entity | Purpose | Location |
|--------|---------|----------|
| `InventoryBatch` | Stock tracking with expiry dates | `inventory/entity/` |
| `Order` | Order records | `order/entity/` |

### Services
| Service | Responsibility | Location |
|---------|-----------------|----------|
| `InventoryService` | Stock management, availability checks | `inventory/service/` |
| `OrderService` | Order processing, inter-service calls | `order/service/` |

### Controllers
| Endpoint | Method | Purpose | Location |
|----------|--------|---------|----------|
| `/inventory/{id}` | GET | Get inventory | `inventory/controller/` |
| `/inventory/update` | POST | Update stock | `inventory/controller/` |
| `/order` | POST | Place order | `order/controller/` |

---

## 🧪 TESTING

### Unit Tests
- Test service logic in isolation
- Use Mockito for mocking dependencies
- Fast execution

Files:
- `InventoryServiceTest.java`
- `OrderServiceTest.java`

### Integration Tests
- Test API endpoints
- Use real H2 database
- Test inter-service communication

Files:
- `InventoryControllerIntegrationTest.java`
- `OrderControllerIntegrationTest.java`

### Run Tests
```bash
mvn test                           # All tests
mvn test -Dtest=InventoryServiceTest   # Specific class
```

---

## 📊 API REFERENCE

### Inventory Service

```
GET /inventory/{productId}
  ├─ Description: Get inventory with batches sorted by expiry
  ├─ Response: InventoryResponseDTO
  └─ Example: GET /inventory/1001

POST /inventory/update
  ├─ Description: Update inventory after order
  ├─ Request: InventoryUpdateDTO
  ├─ Response: "Inventory updated successfully"
  └─ Example: POST /inventory/update

GET /inventory/check/{productId}/{quantity}
  ├─ Description: Check if quantity is available
  ├─ Response: boolean (true/false)
  └─ Example: GET /inventory/check/1001/50
```

### Order Service

```
POST /order
  ├─ Description: Place a new order
  ├─ Request: OrderRequestDTO (productId, quantity)
  ├─ Response: OrderResponseDTO
  └─ Example: POST /order
       Body: {"productId": 1002, "quantity": 3}
```

See [README.md](README.md) for complete examples with curl commands.

---

## 🔒 Database

### Tables

**INVENTORY_BATCH**
- batchId (PK)
- productId
- productName
- quantity
- expiryDate
- createdAt, updatedAt

**ORDERS**
- orderId (PK)
- productId
- productName
- quantity
- status
- orderDate
- reservedBatchIds
- createdAt, updatedAt

### Sample Data
- 10 inventory batches (5 products)
- 10 orders (various statuses)
- Auto-loaded via Liquibase on startup

---

## ⚙️ CONFIGURATION

### Server
```properties
server.port=8080
```

### Database
```properties
spring.datasource.url=jdbc:h2:mem:ecommerce
spring.datasource.username=sa
spring.datasource.password=
```

### Logging
```properties
logging.level.root=INFO
logging.level.com.example=DEBUG
```

See [README.md](README.md) for all configuration options.

---

## ✅ IMPLEMENTATION CHECKLIST

- [x] Two microservices (Inventory + Order)
- [x] REST APIs with proper HTTP methods
- [x] Factory Pattern implementation
- [x] Spring Data JPA with H2
- [x] Liquibase migrations
- [x] CSV data loading
- [x] Unit tests (JUnit 5 + Mockito)
- [x] Integration tests (@SpringBootTest)
- [x] Swagger/OpenAPI docs
- [x] Lombok for boilerplate
- [x] Proper layering (C→S→R)
- [x] Extensible architecture
- [x] Maven build
- [x] Java 17 support
- [x] Complete documentation

---

## 🆘 HELP & TROUBLESHOOTING

### Application Won't Start
```bash
# Check Java version
java -version        # Should be 17+

# Check Maven
mvn -version         # Should be 3.6+

# Clean and rebuild
mvn clean install
```

### Tests Failing
```bash
# Clear cache
rm -rf ~/.m2/repository/com/example

# Rebuild and test
mvn clean test
```

### Can't Access APIs
```bash
# Check if application is running
curl http://localhost:8080/inventory/1001

# Check logs
# Look in console output for errors
```

See [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for more troubleshooting tips.

---

## 📞 GETTING HELP

1. **Quick questions**: Check [QUICK_REFERENCE.md](QUICK_REFERENCE.md)
2. **Setup issues**: See [README.md](README.md) - Troubleshooting section
3. **Technical questions**: Check [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)
4. **Deployment**: See [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md)
5. **API details**: Access Swagger at http://localhost:8080/swagger-ui.html

---

## 🎯 COMMON TASKS

### Run the application
```bash
mvn spring-boot:run
```

### Build without tests
```bash
mvn clean package -DskipTests
```

### Run specific test
```bash
mvn test -Dtest=InventoryServiceTest
```

### View API documentation
Open: http://localhost:8080/swagger-ui.html

### Access database
Open: http://localhost:8080/h2-console

### Check application logs
```bash
# Logs appear in console when running mvn spring-boot:run
```

---

## 📋 FILES AT A GLANCE

| File | Purpose | Size |
|------|---------|------|
| pom.xml | Maven configuration | ~100 lines |
| README.md | Complete documentation | ~400 lines |
| QUICK_REFERENCE.md | Quick reference | ~200 lines |
| IMPLEMENTATION_SUMMARY.md | Implementation details | ~300 lines |
| DEPLOYMENT_CHECKLIST.md | Deployment guide | ~200 lines |
| application.properties | App config | ~20 lines |

### Java Files Summary
- **Main Sources**: 21 files
- **Test Sources**: 4 files
- **Configuration Files**: 1 file
- **Resource Files**: 7 files (migrations + data)

---

## 🎓 LEARNING PATH

**For Beginners:**
1. Read [QUICK_REFERENCE.md](QUICK_REFERENCE.md)
2. Run the application
3. Try API calls in Swagger UI
4. Read [README.md](README.md)

**For Developers:**
1. Read [README.md](README.md)
2. Review [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)
3. Examine source code in `src/main/java/`
4. Run and debug tests

**For DevOps:**
1. Read [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md)
2. Review [README.md](README.md) - Configuration section
3. Set up monitoring
4. Plan deployment strategy

---

## 🏆 PROJECT STATUS

**Status**: ✅ COMPLETE & PRODUCTION READY

**Deliverables**: All requirements fulfilled  
**Testing**: Comprehensive unit & integration tests  
**Documentation**: Complete with examples  
**Code Quality**: Production-ready  

---

## 📅 VERSION INFORMATION

- **Version**: 0.0.1-SNAPSHOT
- **Spring Boot**: 4.0.3
- **Java**: 17
- **Created**: February 2026
- **Last Updated**: February 24, 2026

---

**Happy coding! 🚀**

For questions or issues, refer to the appropriate documentation above.

