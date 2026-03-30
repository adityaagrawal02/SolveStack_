package models;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : models.User.java
 *  Package: models
 *  Role   : Abstract base class for all user roles in the system.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *  ✔ Encapsulation  – All fields are private; accessed via
 *                     public getters and protected setters.
 *  ✔ Abstraction    – Abstract methods force every subclass
 *                     to define role-specific behaviour.
 *  ✔ Inheritance    – models.Company, models.Developer, models.Evaluator, models.Admin
 *                     all extend this class.
 *  ✔ Polymorphism   – viewDashboard() and getRole() are
 *                     overridden differently in each subclass.
 * ============================================================
 */
public abstract class User {

    // ─────────────────────────────────────────────────────────
    //  ENCAPSULATION: private fields – no direct external access
    // ─────────────────────────────────────────────────────────

    private final String userId;      // Immutable unique identifier
    private String       username;
    private String       email;
    private String       passwordHash; // Never stored as plain text
    private String       profileBio;
    private String       contactNumber;
    private boolean      isLoggedIn;
    private boolean      isVerified;   // Set by models.Admin after verification
    private boolean      isBanned;     // Set by models.Admin for policy violations

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a new models.User with core identity fields.
     *
     * @param userId   Unique identifier (e.g., UUID or DB primary key).
     * @param username Display name chosen by the user.
     * @param email    Email address used for authentication.
     * @param password Plain-text password; hashed before storage.
     */
    public User(String userId, String username, String email, String password) {
        if (userId   == null || userId.isBlank())   throw new IllegalArgumentException("userId cannot be blank.");
        if (username == null || username.isBlank())  throw new IllegalArgumentException("username cannot be blank.");
        if (email    == null || email.isBlank())     throw new IllegalArgumentException("email cannot be blank.");
        if (password == null || password.isBlank())  throw new IllegalArgumentException("password cannot be blank.");

        this.userId       = userId;
        this.username     = username;
        this.email        = email;
        this.passwordHash = hashPassword(password); // Hashed immediately
        this.profileBio   = "";
        this.contactNumber = "";
        this.isLoggedIn   = false;
        this.isVerified   = false;
        this.isBanned     = false;
    }

    // ─────────────────────────────────────────────────────────
    //  CORE METHODS (concrete – shared logic for all roles)
    // ─────────────────────────────────────────────────────────

    /**
     * Authenticates the user by comparing a hashed version of the
     * supplied password against the stored hash.
     *
     * @param inputPassword The plain-text password entered at login.
     * @return true if credentials match and the account is active.
     */
    public boolean login(String inputPassword) {
        if (isBanned) {
            System.out.println("[LOGIN DENIED] Account is banned. Contact admin.");
            return false;
        }
        if (isLoggedIn) {
            System.out.println("[LOGIN] " + username + " is already logged in.");
            return false;
        }
        if (hashPassword(inputPassword).equals(this.passwordHash)) {
            this.isLoggedIn = true;
            System.out.println("[LOGIN SUCCESS] Welcome, " + username
                    + "! Role: " + getRole());
            return true;
        }
        System.out.println("[LOGIN FAILED] Incorrect password for: " + username);
        return false;
    }

    /**
     * Ends the current session for this user.
     */
    public void logout() {
        if (!isLoggedIn) {
            System.out.println("[LOGOUT] " + username + " is not currently logged in.");
            return;
        }
        this.isLoggedIn = false;
        System.out.println("[LOGOUT] " + username + " has been logged out successfully.");
    }

    /**
     * Displays the role-specific dashboard.
     *
     * This is the hook for POLYMORPHISM: each subclass overrides
     * this to show its own dashboard content while the guard
     * logic here is shared.
     */
    public void viewDashboard() {
        if (!isLoggedIn) {
            System.out.println("[ACCESS DENIED] Please log in to view your dashboard.");
            return;
        }
        System.out.println("============================================");
        System.out.println("  SolveStack Dashboard – " + getRole());
        System.out.println("  models.User  : " + username);
        System.out.println("  Email : " + email);
        System.out.println("  Status: " + (isVerified ? "Verified ✔" : "Pending Verification"));
        System.out.println("============================================");
        displayRoleDashboard(); // Delegated to subclass
    }

    /**
     * Allows the user to update their own profile information.
     *
     * Pass null for any field you do NOT want to change.
     *
     * @param newUsername    Replacement username (null = keep current).
     * @param newBio         Replacement bio     (null = keep current).
     * @param newContact     Replacement contact  (null = keep current).
     * @param newPassword    Replacement password (null = keep current).
     */
    public void updateProfile(String newUsername,
                              String newBio,
                              String newContact,
                              String newPassword) {
        if (!isLoggedIn) {
            System.out.println("[ERROR] You must be logged in to update your profile.");
            return;
        }

        boolean updated = false;

        if (newUsername != null && !newUsername.isBlank()) {
            this.username = newUsername;
            System.out.println("[PROFILE] Username updated to: " + newUsername);
            updated = true;
        }
        if (newBio != null) {
            this.profileBio = newBio;
            System.out.println("[PROFILE] Bio updated.");
            updated = true;
        }
        if (newContact != null && !newContact.isBlank()) {
            this.contactNumber = newContact;
            System.out.println("[PROFILE] Contact number updated.");
            updated = true;
        }
        if (newPassword != null && !newPassword.isBlank()) {
            this.passwordHash = hashPassword(newPassword);
            System.out.println("[PROFILE] Password changed successfully.");
            updated = true;
        }

        if (!updated) {
            System.out.println("[PROFILE] No changes were made.");
        } else {
            System.out.println("[PROFILE] Profile updated successfully for: " + username);
        }
    }

    // ─────────────────────────────────────────────────────────
    //  ABSTRACT METHODS (ABSTRACTION + POLYMORPHISM hooks)
    //  Every subclass MUST implement these.
    // ─────────────────────────────────────────────────────────

    /**
     * Returns the role label for this user type.
     * Examples: "models.Company", "models.Developer", "models.Evaluator", "models.Admin"
     *
     * @return Role string.
     */
    public abstract String getRole();

    /**
     * Renders the role-specific section of the dashboard.
     * Called internally by {@link #viewDashboard()} after the
     * common header is printed.
     */
    protected abstract void displayRoleDashboard();

    /**
     * Performs any cleanup or role-specific teardown when the
     * user account is removed from the system.
     */
    public abstract void onAccountRemoved();

    // ─────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    /**
     * Simulated password hashing.
     *
     * In a real system this would use BCrypt or PBKDF2.
     * Here we use a deterministic simulation for the prototype.
     *
     * @param password Plain-text password.
     * @return Hashed string.
     */
    private String hashPassword(String password) {
        // Prototype-grade hash: In Phase 2 replace with BCrypt.
        int hash = 0;
        for (char c : password.toCharArray()) {
            hash = 31 * hash + c;
        }
        return "HASH_" + Math.abs(hash);
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS (public – read access for all)
    // ─────────────────────────────────────────────────────────

    public String  getUserId()       { return userId;        }
    public String  getUsername()     { return username;      }
    public String  getEmail()        { return email;         }
    public String  getProfileBio()   { return profileBio;    }
    public String  getContactNumber(){ return contactNumber; }
    public boolean isLoggedIn()      { return isLoggedIn;    }
    public boolean isVerified()      { return isVerified;    }
    public boolean isBanned()        { return isBanned;      }

    // ─────────────────────────────────────────────────────────
    //  PROTECTED SETTERS (only subclasses + models.Admin can mutate)
    // ─────────────────────────────────────────────────────────

    /** Called by models.Admin to verify a user after review. */
    protected void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    /** Called by models.Admin to ban / unban a user. */
    protected void setBanned(boolean banned) {
        if (banned && this.isLoggedIn) {
            logout(); // Force logout on ban
        }
        this.isBanned = banned;
    }

    /** Allows email to be updated (e.g., during profile change flow). */
    protected void setEmail(String email) {
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
    }

    // ─────────────────────────────────────────────────────────
    //  OVERRIDDEN toString()
    // ─────────────────────────────────────────────────────────

    /**
     * Returns a concise, human-readable summary of this user.
     * Useful for logging and admin reports.
     */
    @Override
    public String toString() {
        return String.format(
                "models.User{id='%s', username='%s', email='%s', role='%s', verified=%b, banned=%b, loggedIn=%b}",
                userId, username, email, getRole(), isVerified, isBanned, isLoggedIn
        );
    }

    // ─────────────────────────────────────────────────────────
    //  OVERRIDDEN equals() and hashCode()
    //  Two users are equal if and only if their userId matches.
    // ─────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return this.userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
