package ui;

import java.util.ArrayList;
import java.util.List;
import models.Challenge;
import models.Company;
import models.Submission;

public class ChallengeRepository {
    private static ChallengeRepository instance;
    private final List<Challenge> allChallenges;

    private ChallengeRepository() {
        allChallenges = new ArrayList<>();
        initializeSampleData();
    }

    public static ChallengeRepository getInstance() {
        if (instance == null) {
            instance = new ChallengeRepository();
        }
        return instance;
    }

    private void initializeSampleData() {
        UserRepository userRepo = UserRepository.getInstance();
        
        Company acme = (Company) userRepo.authenticate("acme_corp", "company123");
        if (acme != null) {
            acme.setVerified(true);
            acme.login("company123");
            Challenge c1 = new Challenge("CHAL-001", "AI-Powered Supply Chain Optimizer", 
                "Develop an end-to-end AI model to optimize supply chain logistics for retail.", 
                acme, 50000.0, 30);
            allChallenges.add(c1);
        }

        Company green = (Company) userRepo.authenticate("greentech", "green789");
        if (green != null) {
            green.setVerified(true);
            green.login("green789");
            Challenge c2 = new Challenge("CHAL-002", "Smart Grid Energy Forecasting", 
                "Create a forecasting tool for renewable energy distribution in smart grids.", 
                green, 75000.0, 45);
            allChallenges.add(c2);
        }
    }

    public List<Challenge> getAllChallenges() {
        return new ArrayList<>(allChallenges);
    }

    public void addChallenge(Challenge challenge) {
        allChallenges.add(challenge);
    }

    public Challenge getChallengeById(String id) {
        return allChallenges.stream()
            .filter(c -> c.getChallengeId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Submission> getAllSubmissions() {
        List<Submission> subs = new ArrayList<>();
        for (Challenge c : allChallenges) {
            subs.addAll(c.getSubmissions());
        }
        return subs;
    }

    public Submission getSubmissionById(String subId) {
        for (Challenge c : allChallenges) {
            for (Submission s : c.getSubmissions()) {
                if (s.getSubmissionId().equals(subId)) return s;
            }
        }
        return null;
    }
}
