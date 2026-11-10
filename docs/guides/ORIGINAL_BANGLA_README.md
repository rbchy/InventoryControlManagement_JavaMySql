╔════════════════════════════════════════════════════════════════════════════════╗
║                                                                                ║
║         ফার্মাসিউটিক্যাল প্যাকেজিং ইনভেন্টরি ম্যানেজমেন্ট সিস্টেম                 ║
║                                                                                ║
║           Pharmaceutical Packaging Inventory Management System                ║
║                                                                                ║
║              Java + MySQL + DAO Pattern Implementation                        ║
║                                                                                ║
╚════════════════════════════════════════════════════════════════════════════════╝

════════════════════════════════════════════════════════════════════════════════
📋 বিষয়বস্তু (Table of Contents)
════════════════════════════════════════════════════════════════════════════════

1. সিস্টেম ওভারভিউ (System Overview)
2. প্রযুক্তিগত স্ট্যাক (Technology Stack)
3. ডাটাবেস ডিজাইন (Database Design)
4. আর্কিটেকচার (Architecture)
5. কীভাবে কাজ করে (How It Works)
6. প্রোগ্রাম ফিচার (Features)
7. ফাইল ডেসক্রিপশন (File Descriptions)
8. ইনস্টলেশন ধাপ (Installation Steps)

════════════════════════════════════════════════════════════════════════════════
1. সিস্টেম ওভারভিউ (System Overview)
════════════════════════════════════════════════════════════════════════════════

এটি একটি সম্পূর্ণ Java-based ইনভেন্টরি ম্যানেজমেন্ট সিস্টেম যা ফার্মাসিউটিক্যাল 
প্যাকেজিং ইন্ডাস্ট্রির জন্য তৈরি।

প্রধান উদ্দেশ্য:
  ✓ স্টক ম্যানেজমেন্ট (Stock Management)
  ✓ ক্রয় অর্ডার ট্র্যাকিং (Purchase Order Tracking)
  ✓ প্রোডাকশন মেটেরিয়াল ইস্যু (Production Material Issuance)
  ✓ লেনদেন ইতিহাস (Transaction History)
  ✓ রিপোর্টিং এবং বিশ্লেষণ (Reporting & Analysis)

════════════════════════════════════════════════════════════════════════════════
2. প্রযুক্তিগত স্ট্যাক (Technology Stack)
════════════════════════════════════════════════════════════════════════════════

প্রোগ্রামিং ভাষা:     Java (JDK 11+)
ডাটাবেস:              MySQL (8.0+)
ডাটাবেস ড্রাইভার:     MySQL Connector/J 8.0.33+
ডিজাইন প্যাটার্ন:      DAO (Data Access Object) + Service Pattern
IDE:                   Eclipse, IntelliJ IDEA, বা NetBeans
OS সাপোর্ট:          Windows, Linux, macOS

════════════════════════════════════════════════════════════════════════════════
3. ডাটাবেস ডিজাইন (Database Design)
════════════════════════════════════════════════════════════════════════════════

মূল টেবিলগুলি:

📦 SUPPLIERS (সাপ্লায়ার)
   ├─ supplier_id (PRIMARY KEY)
   ├─ supplier_name
   ├─ contact_person
   ├─ email
   ├─ phone
   ├─ city
   └─ rating

📦 MATERIAL_CATEGORIES (ক্যাটাগরি)
   ├─ category_id (PRIMARY KEY)
   ├─ category_name
   └─ description

📦 MATERIALS (উপাদান/ম্যাটেরিয়াল)
   ├─ material_id (PRIMARY KEY)
   ├─ material_code (UNIQUE)
   ├─ material_name
   ├─ category_id (FOREIGN KEY)
   ├─ supplier_id (FOREIGN KEY)
   ├─ min_stock_level
   ├─ max_stock_level
   ├─ reorder_point
   ├─ current_price
   └─ status

📦 INVENTORY (ইনভেন্টরি/স্টক)
   ├─ inventory_id (PRIMARY KEY)
   ├─ material_id (FOREIGN KEY)
   ├─ current_quantity
   ├─ reserved_quantity
   ├─ available_quantity (GENERATED COLUMN)
   ├─ warehouse_location
   └─ last_received_date

📦 STOCK_TRANSACTIONS (লেনদেন)
   ├─ transaction_id (PRIMARY KEY)
   ├─ material_id (FOREIGN KEY)
   ├─ transaction_type (IN/OUT)
   ├─ quantity
   ├─ transaction_date
   ├─ reference_number (PO-001, MO-045)
   └─ created_by

📦 PURCHASE_ORDERS (ক্রয় অর্ডার)
   ├─ po_id (PRIMARY KEY)
   ├─ po_number (UNIQUE)
   ├─ supplier_id (FOREIGN KEY)
   ├─ po_date
   ├─ expected_delivery_date
   ├─ total_amount
   └─ status (PENDING, RECEIVED)

📦 PRODUCTION_ORDERS (প্রোডাকশন অর্ডার)
   ├─ mo_id (PRIMARY KEY)
   ├─ mo_number (UNIQUE)
   ├─ product_name
   ├─ quantity_to_produce
   └─ status

════════════════════════════════════════════════════════════════════════════════
4. আর্কিটেকচার (Architecture)
════════════════════════════════════════════════════════════════════════════════

স্তরীয় আর্কিটেকচার (Layered Architecture):

┌─────────────────────────────────────────────────────┐
│           ইউজার ইন্টারফেস লেয়ার                      │
│   (User Interface Layer)                             │
│   - InventoryManagementApp.java (Console UI)        │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│          সেবা/ব্যবসায়িক যুক্তি লেয়ার                 │
│   (Service/Business Logic Layer)                    │
│   - InventoryService.java                          │
│   - PurchaseService.java                           │
│   - ReportService.java                             │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│         ডেটা অ্যাক্সেস অবজেক্ট লেয়ার                  │
│   (Data Access Object Layer - DAO)                 │
│   - MaterialDAO.java                               │
│   - InventoryDAO.java                              │
│   - SupplierDAO.java                               │
│   - PurchaseOrderDAO.java                          │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│          মডেল/সত্তা লেয়ার                           │
│   (Model/Entity Layer)                             │
│   - Material.java                                  │
│   - Inventory.java                                 │
│   - Supplier.java                                  │
│   - StockTransaction.java                          │
│   - PurchaseOrder.java                             │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│          ডাটাবেস সংযোগ লেয়ার                        │
│   (Database Connection Layer)                      │
│   - DatabaseConfig.java                            │
│   - JDBC Driver (MySQL Connector)                  │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│           MySQL ডাটাবেস                            │
│   (pharma_inventory database)                      │
└───────────────────────────────────────────────────┘

════════════════════════════════════════════════════════════════════════════════
5. কীভাবে কাজ করে (How It Works - Data Flow)
════════════════════════════════════════════════════════════════════════════════

উদাহরণ: ক্রয় অর্ডার গ্রহণ করা

ধাপ ১: ব্যবহারকারী ইনপুট
   ─────────────────────
   ব্যবহারকারী মেনু থেকে "ক্রয় অর্ডার গ্রহণ" নির্বাচন করেন
   → প্রয়োজনীয় তথ্য দেন (Material ID, Quantity, PO Number)

ধাপ २: ইউজার ইন্টারফেস লেয়ার
   ──────────────────────────
   InventoryManagementApp.java এর receivePurchaseOrder() মেথড কল হয়

ধাপ ३: সেবা লেয়ার
   ────────────────
   InventoryService.java এর receivePurchaseOrder() মেথড
   → ডেটা ভ্যালিডেশন করে
   → DAO মেথড কল করে

ধাপ ४: ডেটা অ্যাক্সেস লেয়ার
   ─────────────────────────
   InventoryDAO.java এর receiveStock() মেথড
   → SQL UPDATE কোয়েরি তৈরি করে: 
      UPDATE inventory 
      SET current_quantity = current_quantity + ?
      WHERE material_id = ?
   → নীচের লেয়ারে পাঠায়

ধাপ ५: ডাটাবেস সংযোগ লেয়ার
   ───────────────────────
   DatabaseConfig.java এর getConnection() মেথড
   → MySQL এর সাথে সংযোগ করে
   → PreparedStatement তৈরি করে
   → কোয়েরি এক্সিকিউট করে

ধাপ ६: ডাটাবেস
   ────────────
   MySQL inventory টেবিল আপডেট হয়
   → current_quantity বৃদ্ধি পায়

ধাপ ७: লেনদেন রেকর্ডিং
   ──────────────────
   stock_transactions টেবিলে নতুন এন্ট্রি যুক্ত হয়
   → transaction_type: "IN"
   → reference_number: "PO-001"

ধাপ ८: রিটার্ন ভ্যালু
   ────────────────
   সফলতার বার্তা ব্যবহারকারীকে দেখানো হয়
   → "✓ PO সফলভাবে গ্রহণ করা হয়েছে!"

════════════════════════════════════════════════════════════════════════════════
6. প্রোগ্রাম ফিচার (Features)
════════════════════════════════════════════════════════════════════════════════

✓ স্টক ম্যানেজমেন্ট
  - রিয়েল-টাইম স্টক ট্র্যাকিং
  - মিনিমাম এবং ম্যাক্সিমাম স্তর সেট করা
  - পুনরায় অর্ডার পয়েন্ট গণনা

✓ ক্রয় অর্ডার ম্যানেজমেন্ট
  - PO তৈরি এবং ট্র্যাক করা
  - সরবরাহকারী ব্যবস্থাপনা
  - ডেলিভারি স্ট্যাটাস ট্র্যাকিং

✓ প্রোডাকশন সাপোর্ট
  - মেটেরিয়াল ইস্যু করা
  - উৎপাদন অর্ডার সংযোগ
  - বাকি স্টক ট্র্যাকিং

✓ লেনদেন ইতিহাস
  - সকল ইন/আউট লেনদেন রেকর্ড
  - রেফারেন্স নম্বর সহ
  - তারিখ এবং সময় লগিং

✓ রিপোর্টিং
  - স্টক স্ট্যাটাস রিপোর্ট
  - কম স্টক সতর্কতা
  - সাপ্লায়ার পারফরম্যান্স
  - ইনভেন্টরি মূল্য বিশ্লেষণ

✓ ডেটা ইন্টিগ্রিটি
  - তথ্য ভ্যালিডেশন
  - সাবধানে রেকর্ড রাখা
  - লেনদেন লগিং

════════════════════════════════════════════════════════════════════════════════
7. ফাইল ডেসক্রিপশন (File Descriptions)
════════════════════════════════════════════════════════════════════════════════

📁 প্রদত্ত ফাইলগুলি:

1️⃣  01_database_schema.sql
    ├─ উদ্দেশ্য: MySQL ডাটাবেস স্কিমা তৈরি করা
    ├─ অন্তর্ভুক্ত: সকল টেবিল, ভিউ এবং নমুনা ডেটা
    └─ ব্যবহার: MySQL Command Line এ চালান

२️⃣  DatabaseConfig.java
    ├─ উদ্দেশ্য: ডাটাবেস সংযোগ পরিচালনা করা
    ├─ মূল মেথড: getConnection()
    ├─ প্যাকেজ: com.pharma.inventory.config
    └─ ব্যবহার: সকল DAO ক্লাসে ব্যবহৃত

३️⃣  ModelClasses.java
    ├─ উদ্দেশ্য: ডেটা মডেল সংজ্ঞা দেওয়া
    ├─ অন্তর্ভুক্ত: 
    │   ├─ Material.java (উপাদান)
    │   ├─ Inventory.java (ইনভেন্টরি)
    │   ├─ Supplier.java (সাপ্লায়ার)
    │   ├─ StockTransaction.java (লেনদেন)
    │   └─ PurchaseOrder.java (ক্রয় অর্ডার)
    ├─ প্যাকেজ: com.pharma.inventory.model
    └─ ব্যবহার: ডেটা সংরক্ষণ এবং পরিবহনের জন্য

४️⃣  DAOClasses.java
    ├─ উদ্দেশ্য: ডাটাবেস অপারেশন সম্পাদন করা
    ├─ অন্তর্ভুক্ত:
    │   ├─ MaterialDAO (উপাদান অপারেশন)
    │   ├─ InventoryDAO (ইনভেন্টরি অপারেশন)
    │   └─ SupplierDAO (সাপ্লায়ার অপারেশন)
    ├─ প্যাকেজ: com.pharma.inventory.dao
    ├─ মূল মেথড: 
    │   ├─ addMaterial()
    │   ├─ receiveStock()
    │   ├─ issueStock()
    │   └─ getAllTransactions()
    └─ ব্যবহার: ডেটা CRUD অপারেশনের জন্য

५️⃣  ServiceAndMainApp.java
    ├─ উদ্দেশ্য: ব্যবসায়িক যুক্তি এবং ইউজার ইন্টারফেস
    ├─ অন্তর্ভুক্ত:
    │   ├─ InventoryService (সেবা/বিজনেস লজিক)
    │   └─ InventoryManagementApp (মেইন অ্যাপ্লিকেশন)
    ├─ প্যাকেজ: 
    │   ├─ com.pharma.inventory.service
    │   └─ com.pharma.inventory.app
    ├─ মূল মেথড:
    │   ├─ displayStockStatus()
    │   ├─ receivePurchaseOrder()
    │   └─ issueForProduction()
    └─ ব্যবহার: অ্যাপ্লিকেশন চালু এবং ব্যবহারকারী ইন্টারঅ্যাকশন

६️⃣  STEP_BY_STEP_BANGLA_GUIDE.txt
    ├─ উদ্দেশ্য: সম্পূর্ণ সেটআপ এবং ব্যবহারের গাইড
    ├─ বিষয়বস্তু: ১০টি বিস্তারিত ধাপ
    └─ ব্যবহার: ইনস্টলেশন এবং ট্রাবলশুটিংয়ের জন্য

৭️⃣  README.md (এই ফাইল)
    ├─ উদ্দেশ্য: প্রজেক্ট বর্ণনা এবং ওভারভিউ
    └─ ব্যবহার: দ্রুত তথ্য জন্য

════════════════════════════════════════════════════════════════════════════════
8. ইনস্টলেশন ধাপ (Installation Steps)
════════════════════════════════════════════════════════════════════════════════

সংক্ষিপ্ত সংস্করণ (5 মিনিটে সেটআপ):

Step 1: প্রয়োজনীয় সফটওয়্যার ইনস্টল করুন
        ├─ Java JDK 11+ ডাউনলোড ও ইনস্টল করুন
        ├─ MySQL 8.0 ইনস্টল করুন
        └─ IDE (Eclipse/IntelliJ) ইনস্টল করুন

Step 2: MySQL ডাটাবেস সেটআপ করুন
        ├─ MySQL Command Line খুলুন
        ├─ 01_database_schema.sql চালান
        └─ সফলতা যাচাই করুন: SHOW TABLES;

Step 3: Java প্রজেক্ট তৈরি করুন
        ├─ নতুন Java প্রজেক্ট তৈরি করুন
        ├─ MySQL Connector JAR যোগ করুন
        └─ প্যাকেজ কাঠামো তৈরি করুন

Step 4: Java ফাইলগুলি যোগ করুন
        ├─ সকল .java ফাইল সঠিক প্যাকেজে রাখুন
        ├─ DatabaseConfig.java এ সংযোগ তথ্য আপডেট করুন
        └─ প্রজেক্ট বিল্ড করুন

Step 5: প্রোগ্রাম চালান
        ├─ InventoryManagementApp.java চালান
        ├─ মেনু প্রদর্শিত হবে
        └─ বিভিন্ন ফাংশন পরীক্ষা করুন

বিস্তারিত গাইডের জন্য: STEP_BY_STEP_BANGLA_GUIDE.txt দেখুন

════════════════════════════════════════════════════════════════════════════════
প্রয়োজনীয় লাইব্রেরি এবং ড্রাইভার
════════════════════════════════════════════════════════════════════════════════

Java Libraries:
  ✓ java.sql (Built-in JDBC)
  ✓ java.time (Built-in)
  ✓ java.util (Built-in)

External Libraries:
  ✓ mysql-connector-java-8.0.33.jar
    DownLoad: https://dev.mysql.com/downloads/connector/j/

Setup Guide:
  1. JAR ফাইল ডাউনলোড করুন
  2. প্রজেক্টের lib/ ফোল্ডারে রাখুন
  3. IDE তে Build Path এ যোগ করুন

════════════════════════════════════════════════════════════════════════════════
উপসংহার (Conclusion)
════════════════════════════════════════════════════════════════════════════════

এই সিস্টেম একটি সম্পূর্ণ, উৎপাদন-প্রস্তুত ইনভেন্টরি ম্যানেজমেন্ট সমাধান প্রদান করে।

সহজ ব্যবহার:
  ✓ কনসোল-ভিত্তিক ইউজার ইন্টারফেস
  ✓ বাংলা মেনু এবং বার্তা
  ✓ স্পষ্ট ত্রুটি হ্যান্ডলিং

প্রযুক্তিগত শ্রেষ্ঠত্ব:
  ✓ DAO ডিজাইন প্যাটার্ন
  ✓ স্তরীয় আর্কিটেকচার
  ✓ SQL Injection প্রতিরোধ (Prepared Statements)

ভবিষ্যত উন্নয়ন:
  ✓ Web ইন্টারফেস (JSP/Spring)
  ✓ REST API
  ✓ রিয়েল-টাইম ড্যাশবোর্ড
  ✓ মোবাইল অ্যাপ্লিকেশন

================================================================================

প্রশ্ন বা সমস্যার জন্য, অনুগ্রহ করে উপরের গাইড নথিগুলি পরামর্শ করুন।

ধন্যবাদ এবং সুখী কোডিং! 🚀

================================================================================
