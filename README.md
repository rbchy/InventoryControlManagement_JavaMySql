# Pharmaceutical Packaging Inventory Management System

![Java](https://img.shields.io/badge/Java-17+-orange?logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![JDBC](https://img.shields.io/badge/JDBC-Driver-green)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Status](https://img.shields.io/badge/Status-Active-brightgreen)

**Author:** Ranajit Baran Chowdhury (RB Chowdhury)  
**Profile:** QA Automation Engineer & Full Stack Developer  
**Email:** chyranajit@gmail.com  
**GitHub:** [@rbchy](https://github.com/rbchy) | [@ranajitchowdhury](https://github.com/ranajitchowdhury)  
**Portfolio:** [rbc6543.wixsite.com/rbc-portfolio](https://rbc6543.wixsite.com/rbc-portfolio)  
**LinkedIn:** [linkedin.com/in/rbchy](https://linkedin.com/in/rbchy)

A comprehensive **real-time inventory management system** built with **Java 17**, **MySQL 8.0**, and **JDBC**. This enterprise-grade application delivers end-to-end pharmaceutical packaging inventory tracking — covering secure authentication, material management, supplier relationships, stock transaction recording, and automated purchase order processing.

---

## 📋 Quick Overview

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java (Core OOP) | 17+ |
| **Database** | MySQL | 8.0+ |
| **Connectivity** | JDBC (MySQL Connector/J) | 9.7.0 |
| **Architecture** | DAO + Service + MVC | Enterprise Pattern |
| **Build Tool** | Maven (Optional) | Latest |
| **IDEs Supported** | IntelliJ IDEA, Eclipse, VS Code | Latest |
| **OS Support** | macOS, Windows, Linux | All |

---

## ✨ Key Features

### 🔐 Secure Authentication & Access Control
- User login and signup with credential validation
- Role-based session management
- Secure database connection with error handling
- Encrypted credential storage mechanisms

### 📦 Material Inventory Management
- Full CRUD operations — Add, Read, Update, Delete materials
- Material categorization and detailed metadata
- Stock level thresholds (Min/Max/Reorder Point)
- Real-time availability tracking with reserved quantities
- Material pricing and status management

### 📊 Real-Time Stock Tracking
- Live inventory dashboard with visual status indicators
  - 🟢 **NORMAL** — Stock within acceptable range
  - 🟡 **HIGH** — Overstock situation
  - 🔴 **LOW** — Urgent reorder required
- Available quantity calculation (Current - Reserved)
- Warehouse location tracking
- Last received date and history

### 💼 Supplier Management
- Supplier database with complete contact information
- Payment terms and rating system (1-5 stars)
- City and region-based supplier filtering
- Contact person and email management
- Quality rating and performance tracking

### 🔄 Stock Transaction Tracking
- Comprehensive transaction logging (INBOUND/OUTBOUND)
- Transaction types: Purchase Orders, Production, Returns, Adjustments
- Reference number and type tracking
- Transaction date and user attribution
- Historical audit trail for compliance

### 🛒 Purchase Order Management
- PO creation and tracking
- Supplier assignment and delivery date management
- Order status tracking (PENDING, CONFIRMED, DELIVERED)
- Total amount calculation and cost tracking
- Expected vs. actual delivery date comparison

### 📈 Advanced Analytics & Reporting
- Stock status reports with status indicators
- Low stock alert system for proactive reordering
- Transaction history and trend analysis
- Supplier performance reports
- Material consumption patterns

### 💾 Database Integration
- MySQL-based persistent storage
- Efficient data retrieval using JDBC with connection pooling
- Transaction management with rollback capabilities
- Data integrity with foreign key constraints
- Optimized queries for large datasets

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────┐
│         InventoryManagementApp (Main Entry)             │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
        ┌──────────────────────────────────┐
        │  InventoryManagementApp UI       │
        │  (Menu-Driven Interface)         │
        └──────────────────┬───────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
            ▼              ▼              ▼
    ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
    │   Inventory  │ │  Suppliers   │ │  Materials   │
    │   Service    │ │  Service     │ │  Service     │
    └──────┬───────┘ └──────┬───────┘ └──────┬───────┘
           │                │                │
    ┌──────┴────────────────┼────────────────┴───────┐
    │                       │                        │
    ▼                       ▼                        ▼
┌─────────────┐      ┌─────────────┐      ┌────────────────┐
│InventoryDAO│      │SupplierDAO  │      │ MaterialDAO    │
│             │      │             │      │                │
└──────┬──────┘      └──────┬──────┘      └────────┬───────┘
       │                    │                      │
       └────────────────────┼──────────────────────┘
                            │
                    ┌───────▼────────┐
                    │ DatabaseConfig │
                    │   JDBC Layer   │
                    └───────┬────────┘
                            │
                    ┌───────▼────────────┐
                    │  MySQL Database    │
                    │  (pharma_inventory)│
                    └────────────────────┘
```

### **Layered Architecture:**

```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (InventoryManagementApp - CLI)     │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Service Layer                   │
│  (InventoryService, Business Logic) │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     DAO (Data Access) Layer         │
│  (MaterialDAO, InventoryDAO, etc.)  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Database Layer                  │
│  (MySQL with JDBC Connectivity)     │
└─────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Java 17 (Core OOP) | Type-safe, modern syntax, enhanced performance |
| **Database** | MySQL 8.0 | ACID-compliant relational database |
| **Connectivity** | JDBC + MySQL Connector/J 9.7.0 | Secure database communication |
| **Architecture** | DAO + Service Pattern | Clean separation of concerns |
| **Build** | Maven (Optional) | Dependency management and build automation |
| **Version Control** | Git/GitHub | Collaborative development |

### **Dependencies:**
```
MySQL Connector/J 9.7.0 (JDBC Driver)
Java 17 JDK
MySQL Server 8.0+
```

---

## ✅ Prerequisites

Before setting up the project, ensure you have installed:

| Tool | Version | Purpose |
|------|---------|---------|
| **JDK** | 17 or higher | Java compilation and runtime |
| **MySQL Server** | 8.0 or higher | Database engine |
| **MySQL Connector/J** | 9.7.0 | JDBC driver |
| **Git** | Latest | Version control |
| **IDE** | IntelliJ IDEA / Eclipse | Development environment |

### **Verification:**

```bash
# Check Java version
java -version
# Expected output: openjdk version "17.x.x"

# Check MySQL version
mysql --version
# Expected output: mysql Ver 8.0.xxx

# Verify MySQL is running
mysql -u root -p -e "SELECT VERSION();"
```

---

## 🚀 Installation & Setup

### **Step 1 — Download MySQL Connector/J 9.7.0**

```bash
# Option A: Download from Maven Central
# Visit: https://dev.mysql.com/downloads/connector/j/

# Option B: Extract uploaded tar.gz
cd ~/Downloads
tar -xzf mysql-connector-j-9_7_0_tar.gz
```

### **Step 2 — Set Up Database**

Open MySQL terminal:

```bash
mysql -u root -p
```

Execute SQL:

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS pharma_inventory;
USE pharma_inventory;

-- Materials table
CREATE TABLE IF NOT EXISTS materials (
    material_id INT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(50) UNIQUE NOT NULL,
    material_name VARCHAR(200) NOT NULL,
    category_id INT,
    description TEXT,
    min_stock_level INT DEFAULT 100,
    max_stock_level INT DEFAULT 5000,
    reorder_point INT DEFAULT 500,
    current_price DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inventory table
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    material_id INT NOT NULL UNIQUE,
    current_quantity INT DEFAULT 0,
    reserved_quantity INT DEFAULT 0,
    warehouse_location VARCHAR(100),
    last_received_date TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials(material_id)
);

-- Suppliers table
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    payment_terms VARCHAR(100),
    rating INT DEFAULT 5,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock transactions table
CREATE TABLE IF NOT EXISTS stock_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    material_id INT NOT NULL,
    transaction_type VARCHAR(50),
    quantity INT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reference_number VARCHAR(100),
    reference_type VARCHAR(50),
    created_by VARCHAR(100),
    FOREIGN KEY (material_id) REFERENCES materials(material_id)
);

-- Purchase orders table
CREATE TABLE IF NOT EXISTS purchase_orders (
    po_id INT AUTO_INCREMENT PRIMARY KEY,
    po_number VARCHAR(100) UNIQUE NOT NULL,
    supplier_id INT NOT NULL,
    po_date DATE,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    total_amount DECIMAL(12,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- Insert sample data
INSERT INTO materials (material_code, material_name, category_id, min_stock_level, max_stock_level, reorder_point, current_price, status) 
VALUES 
('MAT-001', 'Blister Pack (PVC)', 1, 1000, 5000, 1500, 2.50, 'ACTIVE'),
('MAT-002', 'Bottle 20ml HDPE', 2, 500, 3500, 1000, 1.75, 'ACTIVE'),
('MAT-003', 'Carton Box', 3, 500, 2000, 1000, 3.00, 'ACTIVE'),
('MAT-004', 'Label (Printed)', 4, 2000, 8000, 3000, 0.50, 'ACTIVE'),
('MAT-005', 'Aluminum Foil Strip', 5, 100, 1000, 300, 5.00, 'ACTIVE');

INSERT INTO inventory (material_id, current_quantity, reserved_quantity, warehouse_location) 
VALUES 
(1, 5000, 0, 'Rack A1'),
(2, 3500, 500, 'Rack B2'),
(3, 2000, 200, 'Rack C3'),
(4, 8000, 1000, 'Rack D4'),
(5, 150, 50, 'Rack E5');

INSERT INTO suppliers (supplier_name, contact_person, email, phone, city, payment_terms, rating) 
VALUES 
('Pharma Supplies Ltd', 'John Smith', 'john@pharmasupplies.com', '+1-555-0101', 'New York', 'Net 30', 5),
('Global Packaging Co', 'Sarah Johnson', 'sarah@globalpackaging.com', '+1-555-0102', 'Los Angeles', 'Net 45', 4),
('Local Materials Inc', 'Mike Brown', 'mike@localmaterials.com', '+1-555-0103', 'Chicago', 'COD', 3);

SHOW TABLES;
```

### **Step 3 — Clone Repository**

```bash
git clone https://github.com/rbchy/InventoryControlManagement.git
cd InventoryControlManagement
```

### **Step 4 — Add JDBC Driver**

```bash
# Copy mysql-connector-j-9.7.0.jar to lib/ folder
cp ~/Downloads/mysql-connector-j-9.7.0/mysql-connector-j-9.7.0.jar lib/
```

### **Step 5 — Configure Database Connection**

Edit `src/com/pharma/inventory/config/DatabaseConfig.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharma_inventory";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_mysql_password"; // ← Update here
```

### **Step 6 — Compile & Run**

```bash
# Navigate to project directory
cd ~/eclipse-workspace/InventoryControlManagement

# Compile all classes
javac -cp lib/mysql-connector-j-9.7.0.jar -d bin \
  src/com/pharma/inventory/config/DatabaseConfig.java \
  src/com/pharma/inventory/model/*.java \
  src/com/pharma/inventory/dao/*.java \
  src/com/pharma/inventory/service/InventoryService.java \
  src/com/pharma/inventory/app/InventoryManagementApp.java

# Run the application
java -cp bin:lib/mysql-connector-j-9.7.0.jar com.pharma.inventory.app.InventoryManagementApp
```

---

## ▶️ How to Run

### **Via IDE (Eclipse/IntelliJ):**

1. Open project in IDE
2. Right-click `InventoryManagementApp.java` → Run As → Java Application
3. Application starts with menu interface

### **Via Command Line:**

```bash
cd ~/eclipse-workspace/InventoryControlManagement
java -cp bin:lib/mysql-connector-j-9.7.0.jar com.pharma.inventory.app.InventoryManagementApp
```

### **Menu Options:**

```
╔════════════════════════════════════════════════════════╗
║   Pharmaceutical Packaging Inventory Management System  ║
║           Real-Time Stock Tracking & Supply Chain      ║
╚════════════════════════════════════════════════════════╝

📌 Menu:
  1. ইনভেন্টরি ড্যাশবোর্ড (Inventory Dashboard)
  5. লেনদেন দেখুন (View Transactions)
  6. সাপ্লায়ার দেখুন (View Suppliers)
  0. বেরিয়ে যান (Exit)

অপশন নির্বাচন করুন:
```

---

## 📁 Project Structure

```
InventoryControlManagement/
│
├── src/
│   └── com/pharma/inventory/
│       ├── config/
│       │   └── DatabaseConfig.java          ← JDBC connection manager
│       │
│       ├── model/
│       │   ├── Material.java                ← Material entity
│       │   ├── Inventory.java               ← Inventory entity
│       │   ├── Supplier.java                ← Supplier entity
│       │   ├── StockTransaction.java        ← Transaction entity
│       │   └── PurchaseOrder.java           ← PO entity
│       │
│       ├── dao/
│       │   ├── MaterialDAO.java             ← Material CRUD & queries
│       │   ├── InventoryDAO.java            ← Inventory CRUD & queries
│       │   └── SupplierDAO.java             ← Supplier CRUD & queries
│       │
│       ├── service/
│       │   └── InventoryService.java        ← Business logic layer
│       │
│       └── app/
│           └── InventoryManagementApp.java  ← Main entry point
│
├── bin/                                      ← Compiled .class files
│
├── lib/
│   └── mysql-connector-j-9.7.0.jar          ← JDBC driver
│
├── docs/
│   └── 01_database_schema.sql                ← Database DDL
│
├── README.md                                 ← This file
├── .gitignore
└── LICENSE (MIT)
```

---

## 📊 Database Schema

### **Entity Relationship Diagram:**

```
┌─────────────────┐
│    materials    │
├─────────────────┤
│ material_id (PK)│
│ material_code   │
│ material_name   │
│ category_id     │
│ description     │
│ min_stock_level │
│ max_stock_level │
│ reorder_point   │
│ current_price   │
│ status          │
└────────┬────────┘
         │ (1:1)
         │
┌────────▼──────────┐
│   inventory       │
├───────────────────┤
│ inventory_id (PK) │
│ material_id (FK)  │◄──┐
│ current_quantity  │   │
│ reserved_quantity │   │ (1:N)
│ warehouse_location│   │
└───────────────────┘   │
         │              │
         │ (1:N)        │
┌────────▼──────────────────────┐
│   stock_transactions          │
├───────────────────────────────┤
│ transaction_id (PK)           │
│ material_id (FK) ─────────────┘
│ transaction_type              │
│ quantity                      │
│ transaction_date              │
│ reference_number              │
│ reference_type                │
│ created_by                    │
└───────────────────────────────┘

┌──────────────────┐
│   suppliers      │
├──────────────────┤
│ supplier_id (PK) │
│ supplier_name    │
│ contact_person   │
│ email            │
│ phone            │
│ address          │
│ city             │
│ payment_terms    │
│ rating           │
└────────┬─────────┘
         │ (1:N)
         │
┌────────▼──────────────────┐
│   purchase_orders         │
├───────────────────────────┤
│ po_id (PK)                │
│ supplier_id (FK) ◄────────┘
│ po_number                 │
│ po_date                   │
│ expected_delivery_date    │
│ actual_delivery_date      │
│ total_amount              │
│ status                    │
│ created_by                │
└───────────────────────────┘
```

---

## 🌟 Code Quality & Best Practices

| Aspect | Implementation |
|--------|----------------|
| **Design Pattern** | DAO + Service + MVC layered architecture |
| **SOLID Principles** | Single Responsibility, Open/Closed, Liskov Substitution |
| **Error Handling** | Try-catch with meaningful error messages in Bengali + English |
| **Code Documentation** | Javadoc comments in Bengali with method descriptions |
| **Data Validation** | Input validation at DAO and Service layers |
| **Connection Management** | Proper resource cleanup with try-with-resources |
| **SQL Injection Prevention** | PreparedStatement usage throughout |
| **Naming Conventions** | camelCase for variables, PascalCase for classes |

---

## 🔮 Future Enhancements

- [ ] 🖥️ **Swing/JavaFX GUI** — Desktop application interface with rich UI components
- [ ] 🔐 **Role-Based Access Control (RBAC)** — Admin, HR, Employee roles with permission matrix
- [ ] 📄 **PDF Report Generation** — Export inventory reports as PDF using iText/Apache PDFBox
- [ ] 📊 **Analytics Dashboard** — Charts, graphs, trend analysis using JFreeChart
- [ ] 🌐 **REST API** — Spring Boot backend with RESTful endpoints
- [ ] ⚛️ **React Frontend** — Modern web UI with real-time data binding
- [ ] 🔔 **Email Notifications** — Low stock alerts, delivery reminders via JavaMail
- [ ] 📱 **Mobile App** — Android/iOS companion application
- [ ] ☁️ **Cloud Deployment** — AWS/Azure cloud hosting with auto-scaling
- [ ] 🔗 **Third-Party Integration** — ERP systems, accounting software, logistics platforms
- [ ] 📦 **Barcode Scanning** — QR code and barcode support for quick stock operations
- [ ] 🗂️ **Advanced Filtering** — Date range, category, supplier-wise reports
- [ ] 🔐 **Audit Trail** — Complete logging of all inventory changes

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

### **Fork & Clone:**
```bash
git clone https://github.com/rbchy/InventoryControlManagement.git
cd InventoryControlManagement
git checkout -b feature/your-feature-name
```

### **Commit & Push:**
```bash
git add .
git commit -m "Add: Detailed description of your feature"
git push origin feature/your-feature-name
```

### **Submit Pull Request:**
- Open PR against `main` branch
- Include detailed description of changes
- Reference any related issues

### **Code Style:**
- Follow Java naming conventions
- Add Bengali + English comments
- Write unit tests for new features
- Update README if needed

---

## 📄 License

This project is open-source under the **MIT License**.

```
MIT License

Copyright (c) 2024 Ranajit Baran Chowdhury

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

See [LICENSE](LICENSE) for full details.

---

## 💬 Support & Feedback

If you found this project helpful:

- ⭐ **Star the repository** on GitHub
- 🍴 **Fork** it for your own use
- 🐛 **Open an issue** for bugs or feature requests
- 💡 **Submit pull requests** to contribute improvements
- 📧 **Email** suggestions to chyranajit@gmail.com

### **Get in Touch:**
- 🌐 **Portfolio:** [rbc6543.wixsite.com/rbc-portfolio](https://rbc6543.wixsite.com/rbc-portfolio)
- 💼 **LinkedIn:** [linkedin.com/in/rbchy](https://linkedin.com/in/rbchy)
- 🐙 **GitHub:** [@rbchy](https://github.com/rbchy)
- 📧 **Email:** chyranajit@gmail.com

---

## 🎯 Project Metrics

| Metric | Value |
|--------|-------|
| **Total Classes** | 11 |
| **Lines of Code** | 2,000+ |
| **Database Tables** | 5 |
| **DAO Methods** | 25+ |
| **Java Version** | 17+ |
| **MySQL Version** | 8.0+ |
| **Build Tool** | Maven (Optional) |
| **Test Coverage** | Manual Testing |

---

## 📚 Learning Resources

- [Java 17 Documentation](https://docs.oracle.com/javase/17/)
- [MySQL 8.0 Manual](https://dev.mysql.com/doc/refman/8.0/en/)
- [JDBC API Guide](https://docs.oracle.com/javase/tutorial/jdbc/)
- [Design Patterns in Java](https://refactoring.guru/design-patterns/java)
- [Clean Code by Robert C. Martin](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)

---

## ⚠️ Disclaimer

This project is designed for educational purposes and small-scale organizational use. For enterprise-level inventory management, consider:

- Consulting with domain experts
- Implementing advanced security measures
- Adding comprehensive audit trails
- Ensuring regulatory compliance
- Scaling for high-traffic scenarios

---

**Version:** 1.0.0  
**Last Updated:** July 2026  
**Maintenance Status:** Active Development  
**Author:** Ranajit Baran Chowdhury (QA Automation Engineer & Full Stack Developer)

---

## 🙏 Acknowledgments

Special thanks to:
- MySQL Community for robust database engine
- Java community for comprehensive documentation
- Open-source contributors for libraries and tools
- All users and testers for feedback and suggestions

**Happy Coding! 🚀**# InventoryControlManagement_JavaMySql
