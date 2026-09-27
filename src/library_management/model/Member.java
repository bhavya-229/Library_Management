package library_management.model;

import java.time.LocalDate;

/**
 * Member - Model / Domain Entity
 * 
 * Demonstrates OOP:
 * - Encapsulation: Private member variables with public accessors and mutators.
 * - Modern Java Date API: Uses java.time.LocalDate for registrationDate.
 */
public class Member {

    private int memberId;
    private Integer userId; // Optional link to users table (nullable)
    private String name;
    private String email;
    private String phone;
    private LocalDate registrationDate;

    public Member() {
    }

    // Constructor used when registering a NEW member
    public Member(String name, String email, String phone, LocalDate registrationDate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
    }

    // Constructor used when loading an EXISTING member from the database
    public Member(int memberId, Integer userId, String name, String email, String phone, LocalDate registrationDate) {
        this.memberId = memberId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
    }

    // --- Getters and Setters ---

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return String.format("Member [ID=%d, Name='%s', Email='%s', Phone='%s', RegDate=%s]",
                memberId, name, email, phone, registrationDate);
    }
}
