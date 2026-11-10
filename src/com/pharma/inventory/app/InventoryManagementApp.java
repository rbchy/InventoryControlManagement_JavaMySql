package com.pharma.inventory.app;
import com.pharma.inventory.service.InventoryService;
import java.util.Scanner;
public class InventoryManagementApp {
    private static InventoryService inventoryService;
    private static Scanner scanner;
    public static void main(String[] args) {
        inventoryService = new InventoryService();
        scanner = new Scanner(System.in);
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   ফার্মাসিউটিক্যাল প্যাকেজিং ইনভেন্টরি ম্যানেজমেন্ট সিস্টেম    ║");
        System.out.println("║           রিয়েল-টাইম স্টক ট্র্যাকিং & সাপ্লাই চেইন            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        boolean running = true;
        while (running) {
            displayMainMenu();
            System.out.print("\nঅপশন নির্বাচন করুন: ");
            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("✗ অবৈধ ইনপুট।");
                scanner.nextLine();
                continue;
            }
            switch (choice) {
                case 1: inventoryService.displayStockStatus(); break;
                case 5: inventoryService.displayTransactions(); break;
                case 6: inventoryService.displaySuppliers(); break;
                case 0: System.out.println("\n✓ ধন্যবাদ!"); running = false; break;
                default: System.out.println("✗ অবৈধ অপশন।");
            }
        }
        scanner.close();
    }
    private static void displayMainMenu() {
        System.out.println("\n📌 মেনু:");
        System.out.println("  1. ইনভেন্টরি ড্যাশবোর্ড");
        System.out.println("  5. লেনদেন দেখুন");
        System.out.println("  6. সাপ্লায়ার দেখুন");
        System.out.println("  0. বেরিয়ে যান");
    }
}
