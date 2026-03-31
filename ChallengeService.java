package services;

import models.Challenge;
import models.Company;
import models.Evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : ChallengeService.java
 *  Package: services
 *  Role   : Service layer that manages all platform-wide
 *           challenge operations. Acts as the bridge between
 *           the model layer (Challenge, Company) and the
 *           main application / future REST controllers.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *  ✔ Encapsulation  – Master challenge registry is private;
 *                     all access goes through methods.
 *  ✔ Abstraction    – Callers don't need to know how
 *                     challenges are stored or searched.
 *  ✔ Single Responsibility – This class only manages
 *                     challenges; evaluation and submissions
 *                     are handled by their own services.
 * ============================================================
 */
public class ChallengeService {

    // ─────────────────────────────────────────────────────────
    //  ENCAPSULATION: private master registry of all challenges
    // ─────────────────────────────────────────────────────────

    private final List<Challenge> allChallenges;   // All challenges across all companies
    private static int            challengeCounter; // Auto-incrementing ID generator

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Initialises the service with an empty challenge registry.
     */
    public ChallengeService() {
        this.allChallenges  = new ArrayList<>();
        challengeCounter    = 1000; // IDs start from CHG-1001
    }

    // ─────────────────────────────────────────────────────────
    //  CORE SERVICE METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a new challenge on behalf of a verified Company and
     * registers it in the platform-wide challenge list.
     *
     * The Company must be logged in and verified; these checks are
     * enforced inside Company.createChallenge() — this service
     * layer adds the created challenge to the global registry.
     *
     * @param company     The Company posting the challenge.
     * @param title       Short descriptive title.
     * @param description Full problem statement.
     * @param deadline    Submission deadline (e.g., "2026-06-30").
     * @param prizeAmount Reward amount (0 for academic/unpaid).
     * @return The newly created Challenge, or null if creation failed.
     */
    public Challenge createChallenge(Company company,
                                     String title,
                                     String description,
                                     String deadline,
                                     double prizeAmount) {

        if (company == null) {
            System.out.println("[ChallengeService] ERROR: Company reference is null.");
            return null;
        }

        String newId = generateChallengeId();

        // Delegates to Company — enforces login + verification guard
        Challenge created = company.createChallenge(
                newId, title, description, deadline, prizeAmount
        );

        if (created != null) {
            allChallenges.add(created);
            System.out.println("[ChallengeService] Challenge '" + title
                    + "' registered in platform registry. ID: " + newId);
        }

        return created;
    }

    /**
     * Permanently deletes a challenge from the platform registry.
     * Only challenges in PENDING or CLOSED status can be deleted.
     * OPEN challenges must be closed first.
     *
     * @param challengeId ID of the challenge to delete.
     * @param requesterId userId of the person requesting deletion
     *                    (must be the owning company or an Admin).
     * @return true if deletion was successful.
     */
    public boolean deleteChallenge(String challengeId, String requesterId) {
        Challenge target = findById(challengeId);

        if (target == null) {
            System.out.println("[ChallengeService] ERROR: Challenge '" + challengeId + "' not found.");
            return false;
        }
        if (target.getStatus().equalsIgnoreCase("OPEN")) {
            System.out.println("[ChallengeService] ERROR: Cannot delete an OPEN challenge. Close it first.");
            return false;
        }
        if (!target.getPostedByCompanyId().equals(requesterId)) {
            System.out.println("[ChallengeService] UNAUTHORIZED: Only the owning company or Admin can delete this challenge.");
            return false;
        }

        allChallenges.remove(target);
        System.out.println("[ChallengeService] Challenge '" + target.getTitle()
                + "' permanently deleted from the platform.");
        return true;
    }

    /**
     * Returns an unmodifiable view of all challenges on the platform.
     * Callers can read the list but cannot mutate it.
     *
     * @return Read-only list of all challenges.
     */
    public List<Challenge> getAllChallenges() {
        return Collections.unmodifiableList(allChallenges);
    }

    /**
     * Returns only challenges with status "OPEN".
     * Used by Developers to browse available challenges.
     *
     * @return List of currently open challenges.
     */
    public List<Challenge> getOpenChallenges() {
        List<Challenge> open = new ArrayList<>();
        for (Challenge c : allChallenges) {
            if (c.getStatus().equalsIgnoreCase("OPEN")) {
                open.add(c);
            }
        }
        if (open.isEmpty()) {
            System.out.println("[ChallengeService] No open challenges available at this time.");
        }
        return open;
    }

    /**
     * Searches challenges by a keyword found in the title or description.
     * Case-insensitive. Returns all matches.
     *
     * @param keyword Search term entered by the user.
     * @return List of matching Challenge objects.
     */
    public List<Challenge> searchChallenges(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            System.out.println("[ChallengeService] Search keyword cannot be blank.");
            return Collections.emptyList();
        }

        String lowerKeyword = keyword.toLowerCase();
        List<Challenge> results = new ArrayList<>();

        for (Challenge c : allChallenges) {
            if (c.getTitle().toLowerCase().contains(lowerKeyword)
                    || c.getDescription().toLowerCase().contains(lowerKeyword)) {
                results.add(c);
            }
        }

        System.out.println("[ChallengeService] Search for '" + keyword
                + "' returned " + results.size() + " result(s).");
        return results;
    }

    /**
     * Filters challenges by their current status.
     *
     * @param status One of: "OPEN", "CLOSED", "PENDING", "COMPLETED"
     * @return List of challenges matching the given status.
     */
    public List<Challenge> filterByStatus(String status) {
        if (status == null || status.isBlank()) {
            System.out.println("[ChallengeService] Status filter cannot be blank.");
            return Collections.emptyList();
        }

        List<Challenge> filtered = new ArrayList<>();
        for (Challenge c : allChallenges) {
            if (c.getStatus().equalsIgnoreCase(status)) {
                filtered.add(c);
            }
        }

        System.out.println("[ChallengeService] Found " + filtered.size()
                + " challenge(s) with status: " + status.toUpperCase());
        return filtered;
    }

    /**
     * Assigns an Evaluator to a specific challenge.
     * Once assigned, the Evaluator is responsible for scoring
     * all submissions received for that challenge.
     *
     * @param challengeId ID of the challenge.
     * @param evaluator   The Evaluator object to assign.
     * @return true if assignment was successful.
     */
    public boolean assignEvaluator(String challengeId, Evaluator evaluator) {
        if (evaluator == null) {
            System.out.println("[ChallengeService] ERROR: Evaluator reference is null.");
            return false;
        }
        if (!evaluator.isLoggedIn()) {
            System.out.println("[ChallengeService] ERROR: Evaluator must be logged in to be assigned.");
            return false;
        }

        Challenge target = findById(challengeId);
        if (target == null) {
            System.out.println("[ChallengeService] ERROR: Challenge '" + challengeId + "' not found.");
            return false;
        }
        if (!target.getStatus().equalsIgnoreCase("OPEN")) {
            System.out.println("[ChallengeService] ERROR: Evaluators can only be assigned to OPEN challenges.");
            return false;
        }

        target.setAssignedEvaluatorId(evaluator.getUserId());
        System.out.println("[ChallengeService] Evaluator '" + evaluator.getUsername()
                + "' assigned to challenge: " + target.getTitle());
        return true;
    }

    /**
     * Approves a PENDING challenge, changing its status to OPEN.
     * This action is typically triggered by an Admin through AdminService.
     *
     * @param challengeId ID of the challenge to approve.
     * @return true if the challenge was successfully approved.
     */
    public boolean approveChallenge(String challengeId) {
        Challenge target = findById(challengeId);

        if (target == null) {
            System.out.println("[ChallengeService] ERROR: Challenge '" + challengeId + "' not found.");
            return false;
        }
        if (!target.getStatus().equalsIgnoreCase("PENDING")) {
            System.out.println("[ChallengeService] ERROR: Only PENDING challenges can be approved. "
                    + "Current status: " + target.getStatus());
            return false;
        }

        target.openChallenge();
        System.out.println("[ChallengeService] Challenge '" + target.getTitle()
                + "' approved and is now OPEN for submissions.");
        return true;
    }

    /**
     * Retrieves a single challenge by its unique ID.
     *
     * @param challengeId The ID to look up.
     * @return The matching Challenge, or null if not found.
     */
    public Challenge getChallengeById(String challengeId) {
        Challenge result = findById(challengeId);
        if (result == null) {
            System.out.println("[ChallengeService] No challenge found with ID: " + challengeId);
        }
        return result;
    }

    /**
     * Prints a formatted summary of all challenges in the registry.
     * Useful for Admin reports and platform monitoring.
     */
    public void printAllChallenges() {
        System.out.println("============================================");
        System.out.println("  SolveStack – All Challenges (" + allChallenges.size() + " total)");
        System.out.println("--------------------------------------------");

        if (allChallenges.isEmpty()) {
            System.out.println("  No challenges have been posted yet.");
        } else {
            for (Challenge c : allChallenges) {
                System.out.println("  ID      : " + c.getChallengeId());
                System.out.println("  Title   : " + c.getTitle());
                System.out.println("  Status  : " + c.getStatus());
                System.out.println("  Deadline: " + c.getDeadline());
                System.out.println("  Prize   : $" + c.getPrizeAmount());
                System.out.println("  Submissions: " + c.getSubmissionCount());
                System.out.println("  ----------------------------------------");
            }
        }
        System.out.println("============================================");
    }

    /**
     * Returns the total number of challenges registered on the platform.
     *
     * @return Count of all challenges (all statuses).
     */
    public int getTotalChallengeCount() {
        return allChallenges.size();
    }

    // ─────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    /**
     * Internal lookup by challenge ID.
     * Used by all public methods to locate a challenge.
     *
     * @param challengeId The ID to search for.
     * @return Matching Challenge or null.
     */
    private Challenge findById(String challengeId) {
        if (challengeId == null || challengeId.isBlank()) return null;
        for (Challenge c : allChallenges) {
            if (c.getChallengeId().equals(challengeId)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Generates a unique, auto-incrementing challenge ID.
     * Format: CHG-1001, CHG-1002, ...
     *
     * @return New unique challenge ID string.
     */
    private static String generateChallengeId() {
        return "CHG-" + (++challengeCounter);
    }
}
