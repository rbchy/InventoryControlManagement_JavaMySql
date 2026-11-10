-- =====================================================
-- ফার্মাসিউটিক্যাল প্যাকেজিং ইনভেন্টরি ম্যানেজমেন্ট
-- MySQL ডাটাবেস স্কিমা
-- =====================================================

-- ডাটাবেস তৈরি করুন
CREATE DATABASE IF NOT EXISTS pharma_inventory;
USE pharma_inventory;

-- =====================================================
-- RESET (makes this script safely re-runnable)
-- WARNING: this drops and recreates every table below,
-- wiping any existing data in pharma_inventory. Comment
-- this block out if you need to preserve existing rows.
-- =====================================================
SET FOREIGN_KEY_CHECKS = 0;
DROP VIEW IF EXISTS low_stock_items;
DROP VIEW IF EXISTS recent_transactions;
DROP VIEW IF EXISTS inventory_summary;
DROP TABLE IF EXISTS mo_materials;
DROP TABLE IF EXISTS production_orders;
DROP TABLE IF EXISTS inventory_audit_log;
DROP TABLE IF EXISTS stock_transactions;
DROP TABLE IF EXISTS po_items;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS material_categories;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- ১. সাপ্লায়ার টেবিল (Supplier Table)
-- =====================================================
CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(200),
    city VARCHAR(50),
    country VARCHAR(50),
    payment_terms VARCHAR(50),
    rating INT DEFAULT 5,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- ২. ম্যাটেরিয়াল ক্যাটাগরি টেবিল (Material Category)
-- =====================================================
CREATE TABLE material_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- ३. ম্যাটেরিয়াল টেবিল (Material Master Table)
-- =====================================================
CREATE TABLE materials (
    material_id INT PRIMARY KEY AUTO_INCREMENT,
    material_code VARCHAR(20) UNIQUE NOT NULL,
    material_name VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    unit_of_measurement VARCHAR(20),
    current_price DECIMAL(10, 2),
    supplier_id INT NOT NULL,
    min_stock_level INT NOT NULL,
    max_stock_level INT NOT NULL,
    reorder_point INT NOT NULL,
    lead_time_days INT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES material_categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- =====================================================
-- ४. ইনভেন্টরি টেবিল (Inventory Table)
-- =====================================================
CREATE TABLE inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    material_id INT NOT NULL UNIQUE,
    current_quantity INT NOT NULL DEFAULT 0,
    reserved_quantity INT DEFAULT 0,
    available_quantity INT GENERATED ALWAYS AS (current_quantity - reserved_quantity) STORED,
    warehouse_location VARCHAR(50),
    last_received_date DATETIME,
    last_issued_date DATETIME,
    last_count_date DATETIME,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials(material_id)
);

-- =====================================================
-- ५. ক্রয় অর্ডার টেবিল (Purchase Order Table)
-- =====================================================
CREATE TABLE purchase_orders (
    po_id INT PRIMARY KEY AUTO_INCREMENT,
    po_number VARCHAR(20) UNIQUE NOT NULL,
    supplier_id INT NOT NULL,
    po_date DATE NOT NULL,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    total_amount DECIMAL(12, 2),
    status VARCHAR(30) DEFAULT 'PENDING',
    notes VARCHAR(500),
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- =====================================================
-- ६. ক্রয় অর্ডার আইটেম (PO Items Table)
-- =====================================================
CREATE TABLE po_items (
    po_item_id INT PRIMARY KEY AUTO_INCREMENT,
    po_id INT NOT NULL,
    material_id INT NOT NULL,
    ordered_quantity INT NOT NULL,
    received_quantity INT DEFAULT 0,
    unit_price DECIMAL(10, 2),
    line_total DECIMAL(12, 2),
    FOREIGN KEY (po_id) REFERENCES purchase_orders(po_id),
    FOREIGN KEY (material_id) REFERENCES materials(material_id)
);

-- =====================================================
-- ७. স্টক লেনদেন টেবিল (Stock Transaction Table)
-- =====================================================
CREATE TABLE stock_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    material_id INT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    transaction_date DATETIME NOT NULL,
    reference_number VARCHAR(50),
    reference_type VARCHAR(30),
    description VARCHAR(200),
    created_by VARCHAR(50),
    warehouse_from VARCHAR(50),
    warehouse_to VARCHAR(50),
    FOREIGN KEY (material_id) REFERENCES materials(material_id),
    INDEX idx_material_date (material_id, transaction_date)
);

-- =====================================================
-- ८. ইনভেন্টরি অডিট টেবিল (Inventory Audit Log)
-- =====================================================
CREATE TABLE inventory_audit_log (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    material_id INT NOT NULL,
    system_quantity INT,
    physical_quantity INT,
    difference INT,
    variance_percentage DECIMAL(5, 2),
    audit_date DATE NOT NULL,
    audited_by VARCHAR(50),
    remarks VARCHAR(500),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials(material_id)
);

-- =====================================================
-- ९. প্রোডাকশন অর্ডার (Production Order)
-- =====================================================
CREATE TABLE production_orders (
    mo_id INT PRIMARY KEY AUTO_INCREMENT,
    mo_number VARCHAR(20) UNIQUE NOT NULL,
    product_name VARCHAR(100),
    quantity_to_produce INT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(30) DEFAULT 'PENDING',
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- १०. প্রোডাকশন অর্ডার উপকরণ (Production Order Materials)
-- =====================================================
CREATE TABLE mo_materials (
    mo_material_id INT PRIMARY KEY AUTO_INCREMENT,
    mo_id INT NOT NULL,
    material_id INT NOT NULL,
    required_quantity INT NOT NULL,
    issued_quantity INT DEFAULT 0,
    FOREIGN KEY (mo_id) REFERENCES production_orders(mo_id),
    FOREIGN KEY (material_id) REFERENCES materials(material_id)
);

-- =====================================================
-- ११. ব্যবহারকারী টেবিল (User Table)
-- =====================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    role VARCHAR(50),
    department VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME
);

-- =====================================================
-- নমুনা ডেটা ঢোকানো (Sample Data)
-- =====================================================

-- ১. সাপ্লায়ার
INSERT INTO suppliers (supplier_name, contact_person, email, phone, city, country, payment_terms) VALUES
('ABC Packaging Ltd', 'Rafi Ahmed', 'rafi@abcpack.com', '8801234567890', 'Dhaka', 'Bangladesh', 'Net 30'),
('XYZ Industries', 'Mr. Khan', 'khan@xyzind.com', '8801987654321', 'Chittagong', 'Bangladesh', 'Net 15'),
('Pack Solutions', 'Ms. Fatima', 'fatima@packsol.com', '8801112223333', 'Dhaka', 'Bangladesh', 'Net 45');

-- ২. ক্যাটাগরি
INSERT INTO material_categories (category_name, description) VALUES
('Blister Packs', 'PVC এবং Aluminium ব্লিস্টার প্যাক'),
('Bottles & Caps', 'ওষুধের বোতল এবং ক্যাপ'),
('Cartons', 'কার্টন বক্স এবং শিপিং উপকরণ'),
('Labels', 'প্রিন্টেড লেবেল এবং স্টিকার'),
('Others', 'অন্যান্য প্যাকেজিং উপকরণ');

-- ३. ম্যাটেরিয়াল
INSERT INTO materials (material_code, material_name, category_id, unit_of_measurement, current_price, supplier_id, min_stock_level, max_stock_level, reorder_point, lead_time_days, status) VALUES
('BLS-001', 'Blister Pack PVC 10 cavity', 1, 'pieces', 2.50, 1, 1000, 10000, 3000, 10, 'ACTIVE'),
('BOT-001', 'Bottle 20ml HDPE', 2, 'pieces', 0.80, 2, 800, 6000, 2000, 7, 'ACTIVE'),
('CAR-001', 'Carton Box 100x100mm', 3, 'pieces', 1.20, 3, 500, 5000, 1500, 5, 'ACTIVE'),
('LBL-001', 'Label Printed 50x30mm', 4, 'pieces', 0.50, 2, 2000, 15000, 5000, 3, 'ACTIVE'),
('ALU-001', 'Aluminum Foil Strip 500m', 5, 'roll', 3.00, 1, 50, 200, 100, 14, 'ACTIVE');

-- ४. ইনভেন্টরি
INSERT INTO inventory (material_id, current_quantity, reserved_quantity, warehouse_location) VALUES
(1, 5000, 500, 'A1-01'),
(2, 3500, 200, 'A1-02'),
(3, 2000, 100, 'B1-01'),
(4, 8000, 1000, 'B2-01'),
(5, 150, 20, 'C1-01');

-- ५. ব্যবহারকারী
-- Passwords are stored as "salt:sha256(salt+password)" (see PasswordUtil.java), never plaintext.
-- Demo login credentials (plaintext, for you to type into the Login screen):
--   admin          / admin123
--   inventory_mgr  / pass123
--   store_keeper   / store123
INSERT INTO users (username, password, full_name, email, role, department, is_active) VALUES
('admin', 'a1b2c3d4e5f60718:9879a6c081970a685052bdaf0cba84347009c2f6f758c7a05257f5dcd6485ca8', 'Admin User', 'admin@pharma.com', 'ADMIN', 'IT', TRUE),
('inventory_mgr', '1122334455667788:590ec48c23c1b62655d6038b71ef151baf71426ae0a472050e59c844690ec84e', 'Inventory Manager', 'inv@pharma.com', 'MANAGER', 'Inventory', TRUE),
('store_keeper', '99aabbccddeeff00:0c5622c2828e0f2179df78e423dcf2a6c3080f1824f8904cd6d9c92dc2ec1fce', 'Store Keeper', 'store@pharma.com', 'OPERATOR', 'Store', TRUE);

-- =====================================================
-- দরকারী Views তৈরি করুন
-- =====================================================

-- ১. কম স্টক আইটেম ভিউ
CREATE OR REPLACE VIEW low_stock_items AS
SELECT 
    m.material_id,
    m.material_code,
    m.material_name,
    i.current_quantity,
    m.min_stock_level,
    m.max_stock_level,
    (m.max_stock_level - i.current_quantity) as quantity_to_order,
    s.supplier_name,
    m.reorder_point
FROM materials m
JOIN inventory i ON m.material_id = i.material_id
JOIN suppliers s ON m.supplier_id = s.supplier_id
WHERE i.current_quantity <= m.reorder_point
  AND m.status = 'ACTIVE';   -- a deactivated (soft-deleted) material must not keep raising alerts

-- २. সাম্প্রতিক লেনদেন ভিউ
CREATE OR REPLACE VIEW recent_transactions AS
SELECT 
    st.transaction_id,
    m.material_code,
    m.material_name,
    st.transaction_type,
    st.quantity,
    st.transaction_date,
    st.reference_number,
    st.created_by
FROM stock_transactions st
JOIN materials m ON st.material_id = m.material_id
ORDER BY st.transaction_date DESC
LIMIT 100;

-- ३. ইনভেন্টরি সারসংক্ষেপ ভিউ
CREATE OR REPLACE VIEW inventory_summary AS
SELECT 
    m.material_id,
    m.material_code,
    m.material_name,
    mc.category_name,
    i.current_quantity,
    i.reserved_quantity,
    i.available_quantity,
    m.min_stock_level,
    m.max_stock_level,
    (i.current_quantity * m.current_price) as stock_value,
    CASE 
        WHEN i.current_quantity <= m.min_stock_level THEN 'CRITICAL'
        WHEN i.current_quantity <= m.reorder_point THEN 'WARNING'
        WHEN i.current_quantity >= m.max_stock_level THEN 'OVERSTOCK'
        ELSE 'NORMAL'
    END as stock_status
FROM materials m
JOIN inventory i ON m.material_id = i.material_id
JOIN material_categories mc ON m.category_id = mc.category_id
WHERE m.status = 'ACTIVE'    -- "Stock Update" report = active materials only (see ReportDAO)
ORDER BY stock_status DESC;
