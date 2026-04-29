package ui;

import dao.SubmissionDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 UPDATED LeaderboardUI
 SAME UI / SAME CSS
 REAL JDBC DATA
 Ranks by submission score
*/

public class LeaderboardUI extends FxModalWindow {

    private final SubmissionDAO submissionDAO =
            new SubmissionDAO();

    public LeaderboardUI(Object parent) {
        super(
                "SolveStack - Leaderboard",
                1120,
                760,
                parent
        );
    }

    @Override
    protected Parent buildContent() {

        BorderPane root =
                new BorderPane();

        root.getStyleClass()
                .addAll(
                        "modal-root",
                        "app-root"
                );

        root.setTop(topBar());

        VBox content =
                new VBox(14);

        content.setPadding(
                new Insets(24)
        );

        Label title =
                new Label("Leaderboard");

        title.getStyleClass()
                .add("page-title");

        Label subtitle =
                new Label(
                        "Top-performing submissions ranked by final evaluator score."
                );

        subtitle.getStyleClass()
                .add("muted");

        VBox board =
                new VBox(10);

        List<Entry> rows =
                entries();

        if (rows.isEmpty()) {

            Label empty =
                    new Label(
                            "No scored submissions available yet."
                    );

            empty.getStyleClass()
                    .add("muted");

            board.getChildren()
                    .add(empty);

        } else {

            for (Entry entry : rows) {
                board.getChildren()
                        .add(
                                entryCard(entry)
                        );
            }
        }

        content.getChildren()
                .addAll(
                        title,
                        subtitle,
                        board
                );

        ScrollPane scroller =
                new ScrollPane(content);

        scroller.setFitToWidth(true);

        scroller.getStyleClass()
                .add("page-scroll");

        root.setCenter(scroller);

        return root;
    }

    /* ======================================
       CARD
       ====================================== */

    private VBox entryCard(
            Entry entry
    ) {

        Label rank =
                new Label(
                        "#" + entry.rank()
                );

        rank.getStyleClass()
                .addAll(
                        "rank-pill",
                        entry.rankClass()
                );

        Label name =
                new Label(
                        entry.name()
                                + "   "
                                + entry.solution()
                );

        name.getStyleClass()
                .add("table-title");

        Label challenge =
                new Label(
                        entry.challenge()
                );

        challenge.getStyleClass()
                .add("muted");

        Label score =
                new Label(
                        entry.score()
                                + " / 100"
                );

        score.getStyleClass()
                .add("score-highlight");

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox top =
                new HBox(
                        10,
                        rank,
                        name,
                        spacer,
                        score
                );

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label status =
                new Label(
                        entry.status()
                );

        status.getStyleClass()
                .addAll(
                        "status-pill",
                        entry.statusClass()
                );

        return FxComponents.card(
                top,
                challenge,
                status
        );
    }

    /* ======================================
       LOAD REAL DATA
       ====================================== */

    private List<Entry> entries() {

        List<Entry> list =
                new ArrayList<>();

        List<String[]> rows =
                submissionDAO
                        .getAllSubmissions();

        /*
         DAO row:
         0 submission_id
         1 challenge_id
         2 developer_id
         3 status
         4 score
         5 submitted_at
        */

        rows.sort((a, b) -> {

            double s1 =
                    parseScore(a[4]);

            double s2 =
                    parseScore(b[4]);

            return Double.compare(
                    s2,
                    s1
            );
        });

        int rank = 1;

        for (String[] row : rows) {

            double score =
                    parseScore(row[4]);

            if (score <= 0) {
                continue;
            }

            String rankClass =
                    switch (rank) {
                        case 1 -> "rank-gold";
                        case 2 -> "rank-silver";
                        case 3 -> "rank-bronze";
                        default -> "rank-default";
                    };

            String statusClass =
                    switch (
                            row[3]
                                    .toUpperCase()
                            ) {
                        case "ACCEPTED" ->
                                "chip-success";
                        case "REJECTED" ->
                                "chip-danger";
                        case "UNDER_REVIEW" ->
                                "chip-warning";
                        default ->
                                "chip-info";
                    };

            list.add(
                    new Entry(
                            rank,
                            row[2],                // developer
                            row[0],                // submission
                            row[1],                // challenge
                            row[3],                // status
                            statusClass,
                            (int) score,
                            rankClass
                    )
            );

            rank++;
        }

        return list;
    }

    private double parseScore(
            String value
    ) {

        try {
            return Double.parseDouble(
                    value
            );
        } catch (Exception e) {
            return 0;
        }
    }

    /* ======================================
       TOP BAR
       ====================================== */

    private HBox topBar() {

        Label title =
                new Label(
                        "Global Leaderboard"
                );

        title.getStyleClass()
                .add("section-title");

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button close =
                FxComponents.smallBtn(
                        "Close",
                        this::dispose
                );

        HBox top =
                new HBox(
                        12,
                        new LogoPanel(true),
                        title,
                        spacer,
                        close
                );

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        top.getStyleClass()
                .add("top-nav");

        return top;
    }

    /* ======================================
       RECORD
       ====================================== */

    private record Entry(
            int rank,
            String name,
            String solution,
            String challenge,
            String status,
            String statusClass,
            int score,
            String rankClass
    ) {
    }
}