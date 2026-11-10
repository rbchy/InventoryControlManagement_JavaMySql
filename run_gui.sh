#!/usr/bin/env bash
# Compiles and runs the Pharma Inventory Swing GUI.
# Usage: ./run_gui.sh
set -e
cd "$(dirname "$0")"

if [ -z "$DB_USER" ]; then
  read -p "MySQL username [pharma_app]: " DB_USER
  DB_USER=${DB_USER:-pharma_app}
fi
if [ -z "$DB_PASSWORD" ]; then
  read -s -p "MySQL password: " DB_PASSWORD
  echo
fi
export DB_USER
export DB_PASSWORD
export DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/pharma_inventory?useSSL=false&allowPublicKeyRetrieval=true}"

echo "Compiling..."
javac -d bin -cp "lib/mysql-connector-j-9.7.0.jar" $(find src/com -name "*.java")

echo "Starting Pharma Inventory (GUI)..."
java -cp "bin:lib/mysql-connector-j-9.7.0.jar" com.pharma.inventory.gui.PharmaInventoryGUI
