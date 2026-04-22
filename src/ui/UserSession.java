package ui;

import models.User;

/**
 * Singleton class to manage the current user session.
 * Tracks which user is logged in and provides session-wide access to user data.
 */
public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private String userRole;

    private UserSession() {
    }

    /**
     * Gets the singleton instance of UserSession
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Sets the current logged-in user
     */
    public void setCurrentUser(User user, String role) {
        this.currentUser = user;
        this.userRole = role;
    }

    /**
     * Gets the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the role of the current user
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Checks if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;
        userRole = null;
    }

    /**
     * Checks if the current user has a specific role
     */
    public boolean hasRole(String role) {
        return isLoggedIn() && userRole.equals(role);
    }
}
