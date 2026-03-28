package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : Company.java
 *  Package: models
 *  Role   : Represents a Company user who can post and manage
 *           challenges, and review developer submissions.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *  ✔ Inheritance    – Extends User, inherits login/logout/
 *                     updateProfile/viewDashboard behaviour.
 *  ✔ Encapsulation  – Company-specific fields are private;
 *                     accessed via getters only.
 *  ✔ Polymorphism   – Overrides getRole() and
 *                     displayRoleDashboard() from User.
 *  ✔ Abstraction    – Fulfils all abstract contracts defined
 *                     in the parent User class.
 * ============================================================
 */
public class Company extends User {

    // ─────────────────────────────────────────────────────────
    //  ENCAPSULATION: Company-specific private fields
    // ─────────────────────────────────────────────────────────

    private String       companyName;
    private String       industry;
    private String       website;
    private String       registrationNumber;   // Unique business ID
    private final List<Challenge>   postedChallenges;  // Challenges created by this company
    private int          totalSubmissionsReceived;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a new Company user account.
     *
     * @param userId             Unique identifier.
     * @param username           Login username.
     * @param email              Official company email.
     * @param password           Plain-text password (hashed in User).
     * @param companyName        Registered company name.
     * @param industry           Industry sector (e.g., "FinTech", "HealthTech").
     * @param registrationNumber Official business registration number.
     */
    public Company(String userId,
                   String username,
                   String email,
                   String password,
                   String companyName,
                   String industry,
                   String registrationNumber) {

        super(userId, username, email, password); // Calls User constructor

        if (companyName        == null || companyName.isBlank())
            throw new IllegalArgumentException("Company name cannot be blank.");
        if (industry           == null || industry.isBlank())
            throw new IllegalArgumentException("Industry cannot be blank.");
        if (registrationNumber == null || registrationNumber.isBlank())
            throw new IllegalArgumentException("Registration number cannot be blank.");

        this.companyName           = companyName;
        this.industry              = industry;
        this.registrationNumber    = registrationNumber;
        this.website               = "";
        this.postedChallenges      = new ArrayList<>();
        this.totalSubmissionsReceived = 0;
    }

    // ─────────────────────────────────────────────────────────
    //  POLYMORPHISM: Overriding abstract methods from User
    // ─────────────────────────────────────────────────────────

    /**
     * Returns the role label for this user type.
     * POLYMORPHISM: Each subclass returns its own role string.
     */
    @Override
    public String getRole() {
        return "Company";
    }

    /**
     * Renders the company-specific section of the dashboard.
     * Called by viewDashboard() in the parent User class.
     * POLYMORPHISM: Different dashboard content per role.
     */
    @Override
    protected void displayRoleDashboard() {
        System.out.println("  Company Name : " + companyName);
        System.out.println("  Industry     : " + industry);
        System.out.println("  Website      : " + (website.isBlank() ? "Not set" : website));
        System.out.println("  Challenges Posted    : " + postedChallenges.size());
        System.out.println("  Total Submissions    : " + totalSubmissionsReceived);
        System.out.println("--------------------------------------------");

        if (postedChallenges.isEmpty()) {
            System.out.println("  No challenges posted yet. Use createChallenge() to get started.");
        } else {
            System.out.println("  Your Challenges:");
            for (Challenge c : postedChallenges) {
                System.out.println("    → [" + c.getStatus() + "] " + c.getTitle()
                        + " | Submissions: " + c.getSubmissionCount());
            }
        }
        System.out.println("============================================");
    }

    /**
     * Cleanup logic when the company account is removed from the platform.
     * ABSTRACTION: Fulfils the abstract contract defined in User.
     */
    @Override
    public void onAccountRemoved() {
        System.out.println("[ACCOUNT REMOVED] Company account '" + companyName
                + "' has been removed. Closing all active challenges...");
        for (Challenge c : postedChallenges) {
            if (c.getStatus().equalsIgnoreCase("OPEN")) {
                c.closeChallenge();
            }
        }
        postedChallenges.clear();
        System.out.println("[ACCOUNT REMOVED] All challenges closed and data cleared.");
    }

    // ─────────────────────────────────────────────────────────
    //  COMPANY-SPECIFIC METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a new challenge and adds it to this company's list.
     * The challenge is created with PENDING status until Admin approves it.
     *
     * @param challengeId  Unique ID for the challenge.
     * @param title        Short title of the challenge.
     * @param description  Detailed problem description.
     * @param deadline     Submission deadline (e.g., "2025-12-31").
     * @param prizeAmount  Reward offered (0 if unpaid/academic).
     * @return The newly created Challenge object, or null on failure.
     */
    public Challenge createChallenge(String challengeId,
                                     String title,
                                     String description,
                                     String deadline,
                                     double prizeAmount) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] You must be logged in to post a challenge.");
            return null;
        }
        if (!isVerified()) {
            System.out.println("[ERROR] Your company account must be verified by Admin before posting challenges.");
            return null;
        }
        if (title == null || title.isBlank()) {
            System.out.println("[ERROR] Challenge title cannot be blank.");
            return null;
        }

        Challenge challenge = new Challenge(
                challengeId, title, description,
                deadline, prizeAmount, this.getUserId()
        );
        postedChallenges.add(challenge);
        System.out.println("[CHALLENGE CREATED] '" + title + "' posted by " + companyName
                + ". Awaiting admin approval.");
        return challenge;
    }

    /**
     * Edits the title and/or description of an existing challenge.
     * Only OPEN or PENDING challenges can be edited.
     *
     * @param challengeId  ID of the challenge to edit.
     * @param newTitle     New title (null = keep current).
     * @param newDesc      New description (null = keep current).
     */
    public void editChallenge(String challengeId, String newTitle, String newDesc) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Login required to edit a challenge.");
            return;
        }
        Challenge target = findChallengeById(challengeId);
        if (target == null) {
            System.out.println("[ERROR] Challenge ID '" + challengeId + "' not found.");
            return;
        }
        if (target.getStatus().equalsIgnoreCase("CLOSED")) {
            System.out.println("[ERROR] Cannot edit a CLOSED challenge.");
            return;
        }

        boolean changed = false;
        if (newTitle != null && !newTitle.isBlank()) {
            target.setTitle(newTitle);
            System.out.println("[EDIT] Title updated to: " + newTitle);
            changed = true;
        }
        if (newDesc != null && !newDesc.isBlank()) {
            target.setDescription(newDesc);
            System.out.println("[EDIT] Description updated.");
            changed = true;
        }
        if (!changed) System.out.println("[EDIT] No changes applied.");
    }

    /**
     * Closes an active challenge, stopping further submissions.
     *
     * @param challengeId ID of the challenge to close.
     */
    public void closeChallenge(String challengeId) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Login required to close a challenge.");
            return;
        }
        Challenge target = findChallengeById(challengeId);
        if (target == null) {
            System.out.println("[ERROR] Challenge ID '" + challengeId + "' not found.");
            return;
        }
        if (target.getStatus().equalsIgnoreCase("CLOSED")) {
            System.out.println("[INFO] Challenge '" + target.getTitle() + "' is already closed.");
            return;
        }
        target.closeChallenge();
        System.out.println("[CLOSED] Challenge '" + target.getTitle()
                + "' has been closed by " + companyName + ".");
    }

    /**
     * Displays all submissions received for a specific challenge.
     *
     * @param challengeId ID of the challenge to view submissions for.
     */
    public void viewSubmissions(String challengeId) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Login required to view submissions.");
            return;
        }
        Challenge target = findChallengeById(challengeId);
        if (target == null) {
            System.out.println("[ERROR] Challenge ID '" + challengeId + "' not found.");
            return;
        }

        List<Submission> submissions = target.getSubmissions();
        System.out.println("============================================");
        System.out.println("  Submissions for: " + target.getTitle());
        System.out.println("  Total: " + submissions.size());
        System.out.println("--------------------------------------------");

        if (submissions.isEmpty()) {
            System.out.println("  No submissions received yet.");
        } else {
            for (Submission s : submissions) {
                System.out.println("  [" + s.getStatus() + "] ID: " + s.getSubmissionId()
                        + " | Developer: " + s.getDeveloperId()
                        + " | Score: " + s.getScore());
            }
        }
        System.out.println("============================================");
    }

    /**
     * Updates the deadline of an existing open challenge.
     *
     * @param challengeId  ID of the challenge.
     * @param newDeadline  New deadline string (e.g., "2026-03-31").
     */
    public void updateChallengeDeadline(String challengeId, String newDeadline) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Login required.");
            return;
        }
        if (newDeadline == null || newDeadline.isBlank()) {
            System.out.println("[ERROR] Deadline cannot be blank.");
            return;
        }
        Challenge target = findChallengeById(challengeId);
        if (target == null) {
            System.out.println("[ERROR] Challenge not found.");
            return;
        }
        target.updateDeadline(newDeadline);
        System.out.println("[UPDATED] Deadline for '" + target.getTitle()
                + "' changed to: " + newDeadline);
    }

    /**
     * Increments the total submission count for reporting.
     * Called externally by SubmissionService when a new submission arrives.
     */
    public void incrementSubmissionCount() {
        this.totalSubmissionsReceived++;
    }

    // ─────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    /**
     * Searches the company's challenge list by ID.
     *
     * @param challengeId The ID to search for.
     * @return Matching Challenge, or null if not found.
     */
    private Challenge findChallengeById(String challengeId) {
        for (Challenge c : postedChallenges) {
            if (c.getChallengeId().equals(challengeId)) {
                return c;
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────

    public String  getCompanyName()           { return companyName;              }
    public String  getIndustry()              { return industry;                 }
    public String  getWebsite()               { return website;                  }
    public String  getRegistrationNumber()    { return registrationNumber;       }
    public int     getTotalSubmissionsReceived() { return totalSubmissionsReceived; }

    /** Returns an unmodifiable view – prevents external mutation of the list. */
    public List<Challenge> getPostedChallenges() {
        return Collections.unmodifiableList(postedChallenges);
    }

    // ─────────────────────────────────────────────────────────
    //  SETTERS (protected where appropriate)
    // ─────────────────────────────────────────────────────────

    public void setWebsite(String website)         { this.website     = website;     }
    public void setIndustry(String industry)       { this.industry    = industry;    }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    // ─────────────────────────────────────────────────────────
    //  toString()
    // ─────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "Company{id='%s', name='%s', industry='%s', challenges=%d, submissions=%d}",
            getUserId(), companyName, industry,
            postedChallenges.size(), totalSubmissionsReceived
        );
    }
}
