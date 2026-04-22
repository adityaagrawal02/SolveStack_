package utils;

/**
 * ============================================================
 *  Constants.java — SolveStack Global Constants
 * ============================================================
 *
 *  PURPOSE:
 *  This file acts as a single source of truth for all
 *  hard-coded values used across the SolveStack application.
 *
 *  WHY THIS EXISTS:
 *  Instead of scattering magic numbers/strings across UI files,
 *  service files, and models, we define them once here.
 *  If a value needs to change (e.g., max submission size),
 *  you only update it in ONE place.
 *
 *  USAGE:
 *  import utils.Constants;
 *  int max = Constants.MAX_SUBMISSIONS_PER_CHALLENGE;
 *
 *  CATEGORIES COVERED:
 *  - Application metadata
 *  - UI dimensions & layout
 *  - Business rules & limits
 *  - Demo/test credentials
 *  - Error & status messages
 *  - Date formatting
 * ============================================================
 */
public final class Constants {

    // Prevent instantiation — this is a utility constants class
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated.");
    }

    // =========================================================
    // APPLICATION METADATA
    // =========================================================

    /** Full display name of the application */
    public static final String APP_NAME = "SolveStack";

    /** Current version of the application */
    public static final String APP_VERSION = "1.0.0 (Academic)";

    /** Tagline shown on login/splash screen */
    public static final String APP_TAGLINE = "Open Innovation Collaboration Platform";

    /** Organization or institution name */
    public static final String APP_AUTHOR = "SolveStack Academic Team";


    // =========================================================
    // UI — WINDOW DIMENSIONS
    // =========================================================

    /** Default width for the main application window */
    public static final int WINDOW_WIDTH = 1200;

    /** Default height for the main application window */
    public static final int WINDOW_HEIGHT = 750;

    /** Width for login/signup dialog windows */
    public static final int LOGIN_WINDOW_WIDTH = 480;

    /** Height for login/signup dialog windows */
    public static final int LOGIN_WINDOW_HEIGHT = 560;

    /** Width for popup/modal dialogs */
    public static final int DIALOG_WIDTH = 500;

    /** Height for popup/modal dialogs */
    public static final int DIALOG_HEIGHT = 400;


    // =========================================================
    // UI — COMPONENT SIZING
    // =========================================================

    /** Standard padding applied inside panels and cards */
    public static final int PADDING_STANDARD = 20;

    /** Large padding for section headers or hero areas */
    public static final int PADDING_LARGE = 40;

    /** Small padding for compact components */
    public static final int PADDING_SMALL = 8;

    /** Corner arc radius for rounded buttons and cards */
    public static final int BORDER_RADIUS = 12;

    /** Standard height for all action buttons */
    public static final int BUTTON_HEIGHT = 42;

    /** Standard height for all text input fields */
    public static final int INPUT_FIELD_HEIGHT = 38;


    // =========================================================
    // BUSINESS RULES — CHALLENGES
    // =========================================================

    /** Maximum number of submissions allowed per challenge */
    public static final int MAX_SUBMISSIONS_PER_CHALLENGE = 50;

    /** Minimum prize amount (in USD) a company must offer */
    public static final double MIN_PRIZE_AMOUNT = 100.0;

    /** Maximum prize amount (in USD) allowed on the platform */
    public static final double MAX_PRIZE_AMOUNT = 100000.0;

    /** Minimum number of days a challenge must stay open */
    public static final int MIN_CHALLENGE_DURATION_DAYS = 3;

    /** Maximum character limit for a challenge description */
    public static final int MAX_CHALLENGE_DESCRIPTION_LENGTH = 2000;

    /** Maximum character limit for a challenge title */
    public static final int MAX_CHALLENGE_TITLE_LENGTH = 100;


    // =========================================================
    // BUSINESS RULES — SUBMISSIONS
    // =========================================================

    /** Maximum character length for a submission note/description */
    public static final int MAX_SUBMISSION_NOTE_LENGTH = 1000;

    /** Maximum allowed score an evaluator can give */
    public static final int MAX_EVALUATION_SCORE = 100;

    /** Minimum allowed score an evaluator can give */
    public static final int MIN_EVALUATION_SCORE = 0;

    /** Score threshold to be considered a top/winning submission */
    public static final int WINNING_SCORE_THRESHOLD = 85;


    // =========================================================
    // BUSINESS RULES — USER ACCOUNTS
    // =========================================================

    /** Minimum allowed length for any user password */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /** Maximum allowed length for any user password */
    public static final int MAX_PASSWORD_LENGTH = 32;

    /** Minimum allowed length for a username */
    public static final int MIN_USERNAME_LENGTH = 3;

    /** Maximum allowed length for a username */
    public static final int MAX_USERNAME_LENGTH = 20;


    // =========================================================
    // USER ROLES
    // These must match the return values of getRole() in models
    // =========================================================

    public static final String ROLE_ADMIN     = "ADMIN";
    public static final String ROLE_COMPANY   = "COMPANY";
    public static final String ROLE_DEVELOPER = "DEVELOPER";
    public static final String ROLE_EVALUATOR = "EVALUATOR";


    // =========================================================
    // CHALLENGE STATUS LABELS
    // Mirror the Challenge.Status enum for UI display
    // =========================================================

    public static final String STATUS_OPEN         = "OPEN";
    public static final String STATUS_UNDER_REVIEW  = "UNDER_REVIEW";
    public static final String STATUS_CLOSED        = "CLOSED";


    // =========================================================
    // DEMO CREDENTIALS
    // These match the hardcoded accounts in UserRepository.java
    // =========================================================

    public static final String DEMO_DEVELOPER_USERNAME = "alex_kumar";
    public static final String DEMO_DEVELOPER_PASSWORD = "password123";

    public static final String DEMO_COMPANY_USERNAME   = "acme_corp";
    public static final String DEMO_COMPANY_PASSWORD   = "company123";

    public static final String DEMO_ADMIN_USERNAME     = "admin_user";
    public static final String DEMO_ADMIN_PASSWORD     = "admin999";


    // =========================================================
    // DATE FORMATTING
    // =========================================================

    /** Standard date format used across all UI components */
    public static final String DATE_FORMAT = "dd MMM yyyy";

    /** Date-time format used in logs and timestamps */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    // =========================================================
    // UI — STATUS & FEEDBACK MESSAGES
    // Reusable strings shown in dialogs and labels
    // =========================================================

    public static final String MSG_LOGIN_SUCCESS       = "Login successful! Welcome to SolveStack.";
    public static final String MSG_LOGIN_FAILED        = "Invalid username or password. Please try again.";
    public static final String MSG_SIGNUP_SUCCESS      = "Account created successfully! Please log in.";
    public static final String MSG_CHALLENGE_SUBMITTED = "Your solution has been submitted successfully!";
    public static final String MSG_CHALLENGE_APPROVED  = "Challenge approved and is now OPEN.";
    public static final String MSG_CHALLENGE_REJECTED  = "Challenge has been rejected and returned for revision.";
    public static final String MSG_UNAUTHORIZED        = "You are not authorized to perform this action.";
    public static final String MSG_SESSION_EXPIRED     = "Your session has expired. Please log in again.";
    public static final String MSG_DEADLINE_PASSED     = "The submission deadline for this challenge has passed.";
    public static final String MSG_EMPTY_FIELDS        = "Please fill in all required fields before submitting.";
}