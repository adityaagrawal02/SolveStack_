package models;
public interface Evaluable {

    /**
     * Evaluates a single submission and assigns a score.
     *
     * @param submission The submission to evaluate.
     * @param score      Score between 0.0 and 100.0.
     * @param feedback   Written feedback for the developer.
     */
    void evaluateSubmission(Submission submission, double score, String feedback);

    /**
     * Provides detailed written feedback without changing the score.
     *
     * @param submission The target submission.
     * @param feedback   Textual feedback.
     */
    void giveFeedback(Submission submission, String feedback);

    /**
     * Assigns a numeric score to a submission.
     *
     * @param submission The target submission.
     * @param score      Numeric score (0–100).
     */
    void assignScore(Submission submission, double score);
}