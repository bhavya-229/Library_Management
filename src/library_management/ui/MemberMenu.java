package library_management.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import library_management.model.Member;
import library_management.service.MemberService;
import library_management.util.InputUtil;

/**
 * MemberMenu - Console UI for Member Management
 * 
 * Handles user interactions for registering, searching, updating,
 * and deleting library members.
 */
public class MemberMenu {

    private final MemberService memberService;
    private static final String TABLE_SEPARATOR = "=========================================================================================";

    public MemberMenu() {
        this.memberService = new MemberService();
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n==========================================");
            System.out.println("         MEMBER MANAGEMENT MENU           ");
            System.out.println("==========================================");
            System.out.println("1. View All Members");
            System.out.println("2. Search Members (Name / Email / Phone)");
            System.out.println("3. Search Member by ID");
            System.out.println("4. Register New Member");
            System.out.println("5. Update Member Details");
            System.out.println("6. Delete Member");
            System.out.println("7. Back to Main Menu");
            System.out.println("==========================================");

            int choice = InputUtil.readIntInRange("Enter your choice (1-7): ", 1, 7);
            switch (choice) {
                case 1:
                    viewAllMembers();
                    break;
                case 2:
                    searchMembers();
                    break;
                case 3:
                    searchMemberById();
                    break;
                case 4:
                    registerNewMember();
                    break;
                case 5:
                    updateMember();
                    break;
                case 6:
                    deleteMember();
                    break;
                case 7:
                    exit = true;
                    System.out.println("Returning to Main Menu...");
                    break;
            }
        }
    }

    private void viewAllMembers() {
        try {
            List<Member> members = memberService.getAllMembers();
            printMemberTable(members);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve members: " + e.getMessage());
        }
    }

    private void searchMembers() {
        String keyword = InputUtil.readString("Enter search keyword (Name, Email, or Phone): ");
        try {
            List<Member> members = memberService.searchMembers(keyword);
            printMemberTable(members);
        } catch (SQLException e) {
            System.err.println("[ERROR] Member search failed: " + e.getMessage());
        }
    }

    private void searchMemberById() {
        int id = InputUtil.readInt("Enter Member ID: ");
        try {
            Member member = memberService.getMemberById(id);
            if (member != null) {
                System.out.println("\n--- Member Details ---");
                System.out.println("Member ID:         " + member.getMemberId());
                System.out.println("Name:              " + member.getName());
                System.out.println("Email:             " + member.getEmail());
                System.out.println("Phone:             " + member.getPhone());
                System.out.println("Registration Date: " + member.getRegistrationDate());
            } else {
                System.out.println("[!] No member found with ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private void registerNewMember() {
        System.out.println("\n--- Register New Member ---");
        String name = InputUtil.readString("Enter Name: ");
        String email = InputUtil.readString("Enter Email: ");
        String phone = InputUtil.readString("Enter Phone: ");

        Member newMember = new Member(name, email, phone, LocalDate.now());
        try {
            boolean success = memberService.registerMember(newMember);
            if (success) {
                System.out.println("[SUCCESS] Member registered successfully!");
            } else {
                System.out.println("[!] Failed to register member.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] " + e.getMessage());
        }
    }

    private void updateMember() {
        System.out.println("\n--- Update Member ---");
        int id = InputUtil.readInt("Enter Member ID to update: ");
        try {
            Member existing = memberService.getMemberById(id);
            if (existing == null) {
                System.out.println("[!] Member with ID " + id + " does not exist.");
                return;
            }

            System.out.println("Leave blank and press ENTER to keep current value:");

            System.out.print("Name [" + existing.getName() + "]: ");
            String name = InputUtil.readOptionalString("");
            if (!name.isEmpty()) existing.setName(name);

            System.out.print("Email [" + existing.getEmail() + "]: ");
            String email = InputUtil.readOptionalString("");
            if (!email.isEmpty()) existing.setEmail(email);

            System.out.print("Phone [" + existing.getPhone() + "]: ");
            String phone = InputUtil.readOptionalString("");
            if (!phone.isEmpty()) existing.setPhone(phone);

            boolean success = memberService.updateMember(existing);
            if (success) {
                System.out.println("[SUCCESS] Member updated successfully!");
            } else {
                System.out.println("[!] Update failed.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] " + e.getMessage());
        }
    }

    private void deleteMember() {
        System.out.println("\n--- Delete Member ---");
        int id = InputUtil.readInt("Enter Member ID to delete: ");
        try {
            Member member = memberService.getMemberById(id);
            if (member == null) {
                System.out.println("[!] No member found with ID: " + id);
                return;
            }

            String confirm = InputUtil.readString("Are you sure you want to delete '" + member.getName() + "'? (yes/no): ");
            if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
                boolean success = memberService.deleteMember(id);
                if (success) {
                    System.out.println("[SUCCESS] Member deleted successfully!");
                } else {
                    System.out.println("[!] Failed to delete member.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (IllegalStateException e) {
            System.out.println("[RESTRICTION ERROR] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[INPUT ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] " + e.getMessage());
        }
    }

    private void printMemberTable(List<Member> members) {
        if (members == null || members.isEmpty()) {
            System.out.println("[!] No members found.");
            return;
        }

        System.out.println("\n" + TABLE_SEPARATOR);
        System.out.printf("%-5s | %-25s | %-30s | %-15s | %-12s%n",
                "ID", "Name", "Email", "Phone", "Reg Date");
        System.out.println(TABLE_SEPARATOR);

        for (Member m : members) {
            System.out.printf("%-5d | %-25s | %-30s | %-15s | %-12s%n",
                    m.getMemberId(),
                    truncate(m.getName(), 25),
                    truncate(m.getEmail(), 30),
                    truncate(m.getPhone(), 15),
                    m.getRegistrationDate());
        }
        System.out.println(TABLE_SEPARATOR);
        System.out.println("Total records: " + members.size());
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
