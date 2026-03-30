package models;

import java.util.List;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : Admin.java
 *  Package: models
 *  Role   : Platform administrator with elevated privileges.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *   Inheritance    – Extends User.
 *   Encapsulation  – Admin-only operations are gated behind
 *                     login checks and protected setters.
 *   Polymorphism   – Overrides getRole() and dashboard.
 *   Abstraction    – Implements all abstract User methods.
 * ============================================================
 */
public class Admin extends User {

    // ─────────────────────────────────────────────────────────
    //  ADMIN-SPECIFIC PRIVATE FIELDS
    // ─────────────────────────────────────────────────────────

    private final String adminLevel;   // e.g., "SUPER", "REGIONAL"
    private       int    actionsPerformed;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates an Admin user. Admin accounts are auto-verified.
     *
     * @param userId     Unique platform identifier.
     * @param username   Admin login name.
     * @param email      Admin email.
     * @param password   Plain-text password.
     * @param adminLevel Privilege level (e.g., "SUPER", "REGIONAL").
     */
    public Admin(String userId,
                 String username,
                 String email,
                 String password,
                 String adminLevel) {
        super(userId, username, email, password);
        this.adminLevel       = (adminLevel != null && !adminLevel.isBlank()) ? adminLevel : "STANDARD";
        this.actionsPerformed = 0;
        // Admins are always verified
        setVerified(true);
    }

    // ─────────────────────────────────────────────────────────
    //  ABSTRACT METHOD IMPLEMENTATIONS
    // ─────────────────────────────────────────────────────────

    @Override
    public String getRole() { return "Admin"; }

    @Override
    protected void displayRoleDashboard() {
        System.out.println("  Admin Level       : " + adminLevel);
        System.out.println("  Actions Performed : " + actionsPerformed);
        System.out.println("--------------------------------------------");
        System.out.println("  Quick Actions:");
        System.out.println("    1) verifyUser(user)");
        System.out.println("    2) banUser(user)");
        System.out.println("    3) approveChallenge(challenge)");
        System.out.println("    4) viewReports(users, challenges)");
        System.out.println("    5) assignEvaluator(challenge, evaluator)");
        System.out.println("============================================");
    }

    @Override
    public void onAccountRemoved() {
        System.out.println("[ADMIN REMOVED] Admin account '" + getUsername()
                + "' has been removed from the system.");
    }

    // ─────────────────────────────────────────────────────────
    //  ADMIN OPERATIONS
    // ─────────────────────────────────────────────────────────

    /**
     * Verifies a user account after manual review.
     *
     * @param user The user to verify.
     */
    public void verifyUser(User user) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Admin must be logged in.");
            return;
        }
        if (user == null) {
            System.out.println("[ERROR] User cannot be null.");
            return;
        }
        if (user.isVerified()) {
            System.out.println("[INFO] " + user.getUsername() + " is already verified.");
            return;
        }
        user.setVerified(true);
        actionsPerformed++;
        System.out.println("[ADMIN] User '" + user.getUsername()
                + "' (" + user.getRole() + ") has been VERIFIED by " + getUsername());
    }

    /**
     * Bans a user for policy violations (forced logout if online).
     *
     * @param user   The user to ban.
     * @param reason Reason for the ban (for logging).
     */
    public void banUser(User user, String reason) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Admin must be logged in.");
            return;
        }
        if (user == null) {
            System.out.println("[ERROR] User cannot be null.");
            return;
        }
        if (user instanceof Admin) {
            System.out.println("[DENIED] Admins cannot ban other Admins.");
            return;
        }
        if (user.isBanned()) {
            System.out.println("[INFO] " + user.getUsername() + " is already banned.");
            return;
        }
        user.setBanned(true);
        actionsPerformed++;
        System.out.println("[ADMIN] User '" + user.getUsername() + "' BANNED by "
                + getUsername() + ". Reason: " + (reason != null ? reason : "Not specified"));
    }

    /**
     * Lifts a ban from a user.
     *
     * @param user The user to unban.
     */
    public void unbanUser(User user) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Admin must be logged in.");
            return;
        }
        if (user == null || !user.isBanned()) {
            System.out.println("[INFO] User is not banned or is null.");
            return;
        }
        user.setBanned(false);
        actionsPerformed++;
        System.out.println("[ADMIN] User '" + user.getUsername() + "' has been UNBANNED by "
                + getUsername());
    }

    /**
     * Approves a challenge so it becomes publicly visible.
     * (In this prototype, OPEN = approved; this marks it explicitly.)
     *
     * @param challenge The challenge to approve.
     */
    public void approveChallenge(Challenge challenge) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Admin must be logged in.");
            return;
        }
        if (challenge == null) {
            System.out.println("[ERROR] Challenge cannot be null.");
            return;
        }
        if (challenge.getStatus() != Challenge.Status.OPEN) {
            System.out.println("[INFO] Challenge '" + challenge.getChallengeId()
                    + "' is not in OPEN state. Current: " + challenge.getStatus());
            return;
        }
        actionsPerformed++;
        System.out.println("[ADMIN] Challenge '" + challenge.getTitle()
                + "' APPROVED by " + getUsername() + ". Now publicly visible.");
    }

    /**
     * Assigns an Evaluator to a specific Challenge.
     *
     * @param challenge The challenge needing evaluation.
     * @param evaluator The evaluator to assign.
     */
    public void assignEvaluator(Challenge challenge, Evaluator evaluator) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Admin must be logged in.");
            return;
        }
        if (challenge == null || evaluator == null) {
            System.out.println("[ERROR] Challenge and Evaluator cannot be null.");
            return;
        }
        if (!evaluator.isVerified()) {
            System.out.println("[ERROR] Evaluator must be verified before assignment.");
            return;
        }
        challenge.setAssignedEvaluatorId(evaluator.getUserId());
        evaluator.assignToChallenge(challenge.getChallengeId());
        challenge.markUnderReview();
        actionsPerformed++;
        System.out.println("[ADMIN] Evaluator '" + evaluator.getUsername()
                + "' assigned to challenge '" + challenge.getTitle() + "'");
    }

    /**
     * Prints a platform-wide summary report.
     *
     * @param allUsers       Full list of platform users.
     * @param allChallenges  Full list of platform challenges.
     */
    public void viewReports(List<User> allUsers, List<Challenge> allChallenges) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Admin must be logged in.");
            return;
        }

        long companies  = allUsers.stream().filter(u -> u instanceof Company).count();
        long developers = allUsers.stream().filter(u -> u instanceof Developer).count();
        long evaluators = allUsers.stream().filter(u -> u instanceof Evaluator).count();
        long banned     = allUsers.stream().filter(User::isBanned).count();
        long verified   = allUsers.stream().filter(User::isVerified).count();
        long openCh     = allChallenges.stream().filter(c -> c.getStatus() == Challenge.Status.OPEN).count();
        long closedCh   = allChallenges.stream().filter(c -> c.getStatus() == Challenge.Status.CLOSED).count();
        long totalSubs  = allChallenges.stream().mapToLong(c -> c.getSubmissions().size()).sum();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         SOLVESTACK PLATFORM REPORT       ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf ("║  Total Users     : %-22d║%n", allUsers.size());
        System.out.printf ("║    Companies     : %-22d║%n", companies);
        System.out.printf ("║    Developers    : %-22d║%n", developers);
        System.out.printf ("║    Evaluators    : %-22d║%n", evaluators);
        System.out.printf ("║    Verified      : %-22d║%n", verified);
        System.out.printf ("║    Banned        : %-22d║%n", banned);
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf ("║  Total Challenges: %-22d║%n", allChallenges.size());
        System.out.printf ("║    Open          : %-22d║%n", openCh);
        System.out.printf ("║    Closed        : %-22d║%n", closedCh);
        System.out.printf ("║  Total Submissions: %-21d║%n", totalSubs);
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf ("║  Admin Actions   : %-22d║%n", actionsPerformed);
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────

    public String getAdminLevel()       { return adminLevel;       }
    public int    getActionsPerformed() { return actionsPerformed; }

    @Override
    public String toString() {
        return String.format(
                "Admin{id='%s', username='%s', level='%s', actions=%d}",
                getUserId(), getUsername(), adminLevel, actionsPerformed
        );
    }
}