package ui;

import java.util.HashMap;
import java.util.Map;
import models.*;

/**
 * User repository that stores users with credentials for authentication.
 * In a real application, this would connect to a database.
 */
public class UserRepository {
    private static UserRepository instance;
    private final Map<String, UserCredential> users;
    private int userIdCounter = 100;

    private static class UserCredential {
        User user;
        String password;
        String securityQuestion;
        String securityAnswer;

        UserCredential(User user, String password, String securityQuestion, String securityAnswer) {
            this.user = user;
            this.password = password;
            this.securityQuestion = securityQuestion;
            this.securityAnswer = securityAnswer;
        }
    }

    private UserRepository() {
        users = new HashMap<>();
        initializeSampleUsers();
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private void initializeSampleUsers() {
        // Sample Developer
        Developer dev1 = new Developer("dev_001", "alex_kumar", "alex@example.com", "password123", "Java, Python, ML");
        users.put("alex_kumar", new UserCredential(dev1, "password123", "What is your pet's name?", "Fluffy"));

        Developer dev2 = new Developer("dev_002", "priya_sharma", "priya@example.com", "secure456",
                "React, Node.js, AWS");
        users.put("priya_sharma", new UserCredential(dev2, "secure456", "What is your pet's name?", "Fluffy"));

        // Sample Company
        Company comp1 = new Company("comp_001", "acme_corp", "contact@acme.com", "company123", "Acme Corp Solutions",
                "Technology", "ACM-2023-001");
        users.put("acme_corp", new UserCredential(comp1, "company123", "What is your pet's name?", "Fluffy"));

        Company comp2 = new Company("comp_002", "greentech", "info@greentech.com", "green789", "GreenTech Industries",
                "Sustainability", "GRT-2023-002");
        users.put("greentech", new UserCredential(comp2, "green789", "What is your pet's name?", "Fluffy"));

        // Sample Evaluator
        Evaluator eval1 = new Evaluator("eval_001", "ravi_evaluator", "ravi@evaluate.com", "eval123", "expert");
        users.put("ravi_evaluator", new UserCredential(eval1, "eval123", "What is your pet's name?", "Fluffy"));

        Evaluator eval2 = new Evaluator("eval_002", "maya_judge", "maya@evaluate.com", "judge456", "professional");
        users.put("maya_judge", new UserCredential(eval2, "judge456", "What is your pet's name?", "Fluffy"));

        // Sample Admin
        Admin admin = new Admin("admin_001", "admin_user", "admin@solvestack.com", "admin999", "SUPER_ADMIN");
        users.put("admin_user", new UserCredential(admin, "admin999", "What is your pet's name?", "Fluffy"));
    }

    /**
     * Authenticates a user with username and password.
     * Returns the user if credentials are valid, null otherwise.
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return null;
        }

        UserCredential credential = users.get(username);
        if (credential != null && credential.password.equals(password)) {
            return credential.user;
        }
        return null;
    }

    /**
     * Gets the role of a user by their username
     */
    public String getUserRole(String username) {
        UserCredential credential = users.get(username);
        if (credential != null) {
            return credential.user.getRole();
        }
        return null;
    }

    /**
     * Registers a new user account
     * 
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(String username, String email, String password, String role, String dynamicValue,
            String securityQuestion, String securityAnswer) {
        // Check if username already exists
        if (users.containsKey(username)) {
            return false;
        }

        String userId = role.toLowerCase() + "_" + (userIdCounter++);
        User newUser = null;

        try {
            switch (role) {
                case "Developer":
                    newUser = new Developer(userId, username, email, password, dynamicValue);
                    break;
                case "Evaluator":
                    newUser = new Evaluator(userId, username, email, password, dynamicValue);
                    break;
                case "Admin":
                    newUser = new Admin(userId, username, email, password, dynamicValue);
                    break;
            }

            if (newUser != null) {
                users.put(username, new UserCredential(newUser, password, securityQuestion, securityAnswer));
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }

        return false;
    }

    /**
     * Registers a new company account with company-specific fields
     * 
     * @return true if registration successful, false if username already exists
     */
    public boolean registerCompany(String username, String email, String password, String companyName, String industry,
            String securityQuestion, String securityAnswer) {
        // Check if username already exists
        if (users.containsKey(username)) {
            return false;
        }

        try {
            String userId = "comp_" + (userIdCounter++);
            String regNumber = "REG-" + System.currentTimeMillis();
            Company newCompany = new Company(userId, username, email, password, companyName, industry, regNumber);
            users.put(username, new UserCredential(newCompany, password, securityQuestion, securityAnswer));
            return true;
        } catch (Exception e) {
            System.err.println("Error registering company: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        UserCredential credential = users.get(username);
        if (credential != null && newPassword != null && !newPassword.isBlank()) {
            credential.password = newPassword;
            credential.user.updateProfile(null, null, null, newPassword);
            return true;
        }
        return false;
    }

    public boolean updateUserProfile(String oldUsername, String newUsername, String newEmail, String newBio) {
        UserCredential credential = users.get(oldUsername);
        if (credential != null) {
            if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(oldUsername)) {
                if (users.containsKey(newUsername)) {
                    return false; // username already taken
                }
                users.remove(oldUsername);
                users.put(newUsername, credential);
            }

            String updatedUsername = (newUsername != null && !newUsername.isBlank()) ? newUsername : oldUsername;
            credential.user.updateProfile(updatedUsername, newBio, null, null);
            credential.user.setEmail(newEmail);
            return true;
        }
        return false;
    }

    public String getSecurityQuestion(String username) {
        UserCredential credential = users.get(username);
        if (credential != null) {
            return credential.securityQuestion;
        }
        return null;
    }

    public boolean verifySecurityAnswer(String username, String answer) {
        UserCredential credential = users.get(username);
        if (credential != null && credential.securityAnswer != null) {
            return credential.securityAnswer.equalsIgnoreCase(answer);
        }
        return false;
    }
}
