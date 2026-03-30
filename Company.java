package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : Company.java
 *  Package: models
 *  Role   : Represents a corporate entity that posts challenges
 *           and reviews developer submissions.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *  ✔ Inheritance    – Extends User; inherits login, logout,
 *                     updateProfile, and all getters.
 *  ✔ Encapsulation  – Company-specific fields are private;
 *                     accessed through controlled methods.
 *  ✔ Polymorphism   – Overrides getRole() and
 *                     displayRoleDashboard() from User.
 *  ✔ Abstraction    – Implements all abstract methods from User.
 * ============================================================
 */
public class Company extends User {

    // ─────────────────────────────────────────────────────────
    //  COMPANY-SPECIFIC PRIVATE FIELDS
    // ─────────────────────────────────────────────────────────

    private String       companyName;
    private String       industry;
    private String       website;
    private String       registrationNumber;     // Verified by Admin
    private final List<Challenge> postedChallenges;   // Challenges this company created

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a Company user with identity and corporate details.
     *
     * @param userId             Unique platform identifier.
     * @param username           Display / login name.
     * @param email              Corporate email address.
     * @param password           Plain-text password (hashed in User).
     * @param companyName        Official registered company name.
     * @param industry           Industry sector (e.g., "FinTech", "HealthTech").
     * @param registrationNumber Corporate registration / CIN number.
     */
    public Company(String userId,
                   String username,
                   String email,
                   String password,
                   String companyName,
                   String industry,
                   String registrationNumber) {
        super(userId, username, email, password);

        if (companyName        == null || companyName.isBlank())
            throw new IllegalArgumentException("companyName cannot be blank.");
        if (industry           == null || industry.isBlank())
            throw new IllegalArgumentException("industry cannot be blank.");
        if (registrationNumber == null || registrationNumber.isBlank())
            throw new IllegalArgumentException("registrationNumber cannot be blank.");

        this.companyName        = companyName;
        this.industry           = industry;
        this.registrationNumber = registrationNumber;
        this.website            = "";
        this.postedChallenges   = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────
    //  ABSTRACT METHOD IMPLEMENTATIONS (required by User)
    // ─────────────────────────────────────────────────────────

    /**
     * Returns the role label for this user type.
     *
     * @return "Company"
     */
    @Override
    public String getRole() {
        return "Company";
    }

    /**
     * Renders the company-specific portion of the dashboard,
     * showing challenge statistics and quick-action prompts.
     */
    @Override
    protected void displayRoleDashboard() {
        long open   = postedChallenges.stream()
                .filter(c -> c.getStatus() == Challenge.Status.OPEN).count();
        long closed = postedChallenges.stream()
                .filter(c -> c.getStatus() == Challenge.Status.CLOSED).count();

        System.out.println("  Company         : " + companyName);
        System.out.println("  Industry        : " + industry);
        System.out.println("  Website         : " + (website.isBlank() ? "N/A" : website));
        System.out.println("--------------------------------------------");
        System.out.println("  Total Challenges: " + postedChallenges.size());
        System.out.println("  Open            : " + open);
        System.out.println("  Closed          : " + closed);
        System.out.println("--------------------------------------------");
        System.out.println("  Quick Actions:");
        System.out.println("    1) createChallenge(title, desc, prize, days)");
        System.out.println("    2) viewSubmissions(challengeId)");
        System.out.println("    3) closeChallenge(challengeId)");
        System.out.println("============================================");
    }

    /**
     * Cleanup when the company account is removed from the platform.
     * Closes all open challenges before removal.
     */
    @Override
    public void onAccountRemoved() {
        System.out.println("[ACCOUNT REMOVED] Company account '" + companyName
                + "' is being removed. Closing all open challenges...");
        for (Challenge challenge : postedChallenges) {
            if (challenge.getStatus() == Challenge.Status.OPEN) {
                challenge.closeChallenge();
            }
        }
        System.out.println("[ACCOUNT REMOVED] All challenges closed for: " + companyName);
    }

    // ─────────────────────────────────────────────────────────
    //  CHALLENGE MANAGEMENT METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a new challenge and posts it to the platform.
     *
     * The company must be logged in and verified by an admin before
     * posting challenges.
     *
     * @param title           Short, descriptive challenge title.
     * @param description     Full problem statement.
     * @param prizeAmount     Monetary reward for the winning submission.
     * @param durationDays    Number of days the challenge remains open.
     * @return The newly created Challenge, or null if creation fails.
     */
    public Challenge createChallenge(String title,
                                     String description,
                                     double prizeAmount,
                                     int    durationDays) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] You must be logged in to post a challenge.");
            return null;
        }
        if (!isVerified()) {
            System.out.println("[ERROR] Your company account must be verified by an Admin before posting challenges.");
            return null;
        }
        if (title == null || title.isBlank()) {
            System.out.println("[ERROR] Challenge title cannot be blank.");
            return null;
        }
        if (description == null || description.isBlank()) {
            System.out.println("[ERROR] Challenge description cannot be blank.");
            return null;
        }
        if (prizeAmount <= 0) {
            System.out.println("[ERROR] Prize amount must be greater than zero.");
            return null;
        }
        if (durationDays <= 0) {
            System.out.println("[ERROR] Duration must be at least 1 day.");
            return null;
        }

        String challengeId = "CH-" + getUserId() + "-" + (postedChallenges.size() + 1);
        Challenge challenge = new Challenge(challengeId, title, description,
                this, prizeAmount, durationDays);
        postedChallenges.add(challenge);

        System.out.println("[CHALLENGE CREATED] '" + title + "' posted by " + companyName);
        System.out.println("  Challenge ID  : " + challengeId);
        System.out.println("  Prize         : $" + prizeAmount);
        System.out.println("  Duration      : " + durationDays + " day(s)");
        System.out.println("  Status        : " + challenge.getStatus());

        return challenge;
    }

    /**
     * Edits an existing challenge that this company owns.
     *
     * Only open challenges can be edited. Pass null for fields
     * you do NOT want to change.
     *
     * @param challengeId     ID of the challenge to edit.
     * @param newTitle        Replacement title        (null = keep current).
     * @param newDescription  Replacement description  (null = keep current).
     * @param newPrize        New prize amount (≤ 0   = keep current).
     */
    public void editChallenge(String challengeId,
                              String newTitle,
                              String newDescription,
                              double newPrize) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] You must be logged in to edit a challenge.");
            return;
        }

        Challenge challenge = findChallenge(challengeId);
        if (challenge == null) return;

        if (challenge.getStatus() != Challenge.Status.OPEN) {
            System.out.println("[ERROR] Only OPEN challenges can be edited. Current status: "
                    + challenge.getStatus());
            return;
        }

        boolean changed = false;

        if (newTitle != null && !newTitle.isBlank()) {
            challenge.setTitle(newTitle);
            System.out.println("[CHALLENGE EDIT] Title updated to: " + newTitle);
            changed = true;
        }
        if (newDescription != null && !newDescription.isBlank()) {
            challenge.setDescription(newDescription);
            System.out.println("[CHALLENGE EDIT] Description updated.");
            changed = true;
        }
        if (newPrize > 0) {
            challenge.setPrizeAmount(newPrize);
            System.out.println("[CHALLENGE EDIT] Prize updated to: $" + newPrize);
            changed = true;
        }

        if (!changed) {
            System.out.println("[CHALLENGE EDIT] No changes were made to challenge: " + challengeId);
        } else {
            System.out.println("[CHALLENGE EDIT] Challenge '" + challengeId + "' updated successfully.");
        }
    }

    /**
     * Closes an open challenge, preventing further submissions.
     *
     * @param challengeId ID of the challenge to close.
     */
    public void closeChallenge(String challengeId) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] You must be logged in to close a challenge.");
            return;
        }

        Challenge challenge = findChallenge(challengeId);
        if (challenge == null) return;

        if (challenge.getStatus() == Challenge.Status.CLOSED) {
            System.out.println("[INFO] Challenge '" + challengeId + "' is already closed.");
            return;
        }

        challenge.closeChallenge();
        System.out.println("[CHALLENGE CLOSED] '" + challenge.getTitle()
                + "' has been closed. No further submissions accepted.");
    }

    /**
     * Displays all submissions for a specific challenge owned by this company.
     *
     * @param challengeId ID of the challenge to inspect.
     */
    public void viewSubmissions(String challengeId) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] You must be logged in to view submissions.");
            return;
        }

        Challenge challenge = findChallenge(challengeId);
        if (challenge == null) return;

        List<Submission> submissions = challenge.getSubmissions();
        System.out.println("============================================");
        System.out.println("  Submissions for: " + challenge.getTitle());
        System.out.println("  Challenge ID   : " + challengeId);
        System.out.println("  Total          : " + submissions.size());
        System.out.println("--------------------------------------------");

        if (submissions.isEmpty()) {
            System.out.println("  No submissions yet.");
        } else {
            for (int i = 0; i < submissions.size(); i++) {
                Submission s = submissions.get(i);
                System.out.printf("  %d. [%s] %s | Developer: %s | Score: %.1f%n",
                        i + 1,
                        s.getStatus(),
                        s.getSubmissionId(),
                        s.getDeveloperUsername(),
                        s.getScore());
            }
        }
        System.out.println("============================================");
    }

    /**
     * Lists all challenges posted by this company.
     */
    public void viewAllChallenges() {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] You must be logged in.");
            return;
        }
        System.out.println("============================================");
        System.out.println("  All Challenges by: " + companyName);
        System.out.println("--------------------------------------------");
        if (postedChallenges.isEmpty()) {
            System.out.println("  No challenges posted yet.");
        } else {
            for (Challenge c : postedChallenges) {
                System.out.printf("  [%s] %s | Prize: $%.2f | Submissions: %d%n",
                        c.getStatus(), c.getChallengeId(),
                        c.getPrizeAmount(), c.getSubmissions().size());
                System.out.println("       Title: " + c.getTitle());
            }
        }
        System.out.println("============================================");
    }

    // ─────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    /**
     * Searches for a challenge by ID within this company's posted list.
     *
     * @param challengeId The ID to search for.
     * @return The matching Challenge, or null if not found.
     */
    private Challenge findChallenge(String challengeId) {
        for (Challenge c : postedChallenges) {
            if (c.getChallengeId().equals(challengeId)) {
                return c;
            }
        }
        System.out.println("[ERROR] Challenge not found or not owned by " + companyName
                + ": " + challengeId);
        return null;
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────

    public String          getCompanyName()        { return companyName;        }
    public String          getIndustry()           { return industry;           }
    public String          getWebsite()            { return website;            }
    public String          getRegistrationNumber() { return registrationNumber; }
    public List<Challenge> getPostedChallenges()   {
        return Collections.unmodifiableList(postedChallenges);
    }

    // ─────────────────────────────────────────────────────────
    //  SETTERS (protected – changeable only within package or subclass)
    // ─────────────────────────────────────────────────────────

    public void setWebsite(String website) {
        if (website != null && !website.isBlank()) {
            this.website = website;
            System.out.println("[PROFILE] Website updated to: " + website);
        }
    }

    public void setIndustry(String industry) {
        if (industry != null && !industry.isBlank()) {
            this.industry = industry;
        }
    }

    // ─────────────────────────────────────────────────────────
    //  OVERRIDDEN toString()
    // ─────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
                "Company{id='%s', username='%s', company='%s', industry='%s', challenges=%d, verified=%b}",
                getUserId(), getUsername(), companyName, industry,
                postedChallenges.size(), isVerified()
        );
    }
}