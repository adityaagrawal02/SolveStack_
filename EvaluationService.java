package models;
public class EvaluationService {

    /**
     * Evaluates a submission and updates developer points.
     */
    public void evaluateSubmission(Evaluator evaluator,
                                   Submission submission,
                                   Developer developer,
                                   double score,
                                   String feedback) {

        // Basic validation
        if (evaluator == null || submission == null || developer == null) {
            System.out.println("[EvaluationService] ERROR: Null values provided.");
            return;
        }

        if (!evaluator.isLoggedIn()) {
            System.out.println("[EvaluationService] ERROR: Evaluator must be logged in.");
            return;
        }

        if (score < 0 || score > 100) {
            System.out.println("[EvaluationService] ERROR: Score must be between 0 and 100.");
            return;
        }

        // Step 1: Evaluate submission
        evaluator.evaluateSubmission(submission, score, feedback);

        // Step 2: Award points if accepted
        if (score >= 50) {
            int points = (int) score; // simple logic: score = points
            developer.addPoints(points);

            System.out.println("[EvaluationService] Points awarded to "
                    + developer.getUsername() + ": " + points);
        } else {
            System.out.println("[EvaluationService] Submission rejected. No points awarded.");
        }
    }
}