package models;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardService {

    /**
     * Displays developers ranked by total points.
     */
    public void showTopDevelopers(List<Developer> developers) {

        if (developers == null || developers.isEmpty()) {
            System.out.println("[Leaderboard] No developers found.");
            return;
        }

        // Create a copy to avoid modifying original list
        List<Developer> sortedList = new ArrayList<>(developers);

        // Sort in descending order of points
        sortedList.sort(Comparator.comparingInt(Developer::getTotalPoints).reversed());

        System.out.println("============================================");
        System.out.println("            LEADERBOARD ");
        System.out.println("--------------------------------------------");

        for (int i = 0; i < sortedList.size(); i++) {
            Developer dev = sortedList.get(i);

            System.out.printf("%d. %s | Points: %d%n",
                    i + 1,
                    dev.getUsername(),
                    dev.getTotalPoints());
        }

        System.out.println("============================================");
    }
}