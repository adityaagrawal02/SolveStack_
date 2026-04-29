package ui;

import dao.ChallengeDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Challenge;
import models.Company;

import java.util.ArrayList;
import java.util.List;

/*
 UPDATED ChallengeUI
 SAME UI / SAME CSS
 REAL JDBC DATA
*/

public class ChallengeUI extends FxModalWindow {

    private final ChallengeDAO challengeDAO =
            new ChallengeDAO();

    public ChallengeUI() {
        this(null);
    }

    public ChallengeUI(Object parent) {
        super(
                "SolveStack - Challenges",
                1180,
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

        root.setTop(
                topBar(
                        "Challenge Arena",
                        "Discover active innovation tracks"
                )
        );

        VBox list =
                new VBox(12);

        list.setPadding(
                new Insets(24)
        );

        Label title =
                new Label("Open Challenges");

        title.getStyleClass()
                .add("page-title");

        Label subtitle =
                new Label(
                        "Pick a challenge, submit your idea, and climb the leaderboard."
                );

        subtitle.getStyleClass()
                .add("muted");

        list.getChildren().addAll(
                title,
                subtitle
        );

        List<Challenge> challenges =
                loadChallengesFromDB();

        for (Challenge c : challenges) {
            list.getChildren()
                    .add(
                            challengeCard(c)
                    );
        }

        if (challenges.isEmpty()) {

            Label empty =
                    new Label(
                            "No active challenges found."
                    );

            empty.getStyleClass()
                    .add("muted");

            list.getChildren()
                    .add(empty);
        }

        ScrollPane scroller =
                new ScrollPane(list);

        scroller.setFitToWidth(true);

        scroller.getStyleClass()
                .add("page-scroll");

        root.setCenter(scroller);

        return root;
    }

    /* ======================================
       LOAD REAL CHALLENGES
       ====================================== */

    private List<Challenge> loadChallengesFromDB() {

        List<Challenge> list =
                new ArrayList<>();

        List<String[]> rows =
                challengeDAO
                        .getAllOpenChallenges();

        for (String[] row : rows) {

            try {

                /*
                 row:
                 0 challenge_id
                 1 title
                 2 company_id
                 3 prize
                 4 deadline
                */

                Company company =
                        new Company(
                                row[2],
                                "Company",
                                "company@solvestack.com",
                                "hidden",
                                "Company",
                                "Tech",
                                "REG001"
                        );

                Challenge c =
                        new Challenge(
                                row[0],
                                row[1],
                                "Live challenge loaded from database.",
                                company,
                                Double.parseDouble(row[3]),
                                30
                        );

                list.add(c);

            } catch (Exception ignored) {
            }
        }

        return list;
    }

    /* ======================================
       CARD
       ====================================== */

    private VBox challengeCard(
            Challenge c
    ) {

        Label name =
                new Label(
                        c.getTitle()
                );

        name.getStyleClass()
                .add("table-title");

        Label meta =
                new Label(
                        c.getPostedBy()
                                .getUsername()
                                + "   Prize: INR "
                                + c.getPrizeAmount()
                                + "   Deadline: "
                                + c.getDeadline()
                );

        meta.getStyleClass()
                .add("muted");

        Label status =
                new Label("Open");

        status.getStyleClass()
                .addAll(
                        "status-pill",
                        "chip-success"
                );

        Button apply =
                FxComponents.primaryBtn(
                        "Apply",
                        () -> new SubmitSolutionUI(
                                getStage(),
                                c
                        ).setVisible(true)
                );

        apply.getStyleClass()
                .add("compact-btn");

        models.User current =
                UserSession.getInstance()
                        .getCurrentUser();

        if (!(current instanceof models.Developer)) {

            apply.setVisible(false);
            apply.setManaged(false);
        }

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox bottom =
                new HBox(
                        10,
                        status,
                        spacer,
                        apply
                );

        bottom.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox card =
                FxComponents.card(
                        name,
                        meta,
                        bottom
                );

        card.getStyleClass()
                .add("challenge-card");

        return card;
    }

    /* ======================================
       TOP BAR
       ====================================== */

    private HBox topBar(
            String heading,
            String subtext
    ) {

        Label title =
                new Label(heading);

        title.getStyleClass()
                .add("section-title");

        Label sub =
                new Label(subtext);

        sub.getStyleClass()
                .add("muted");

        VBox text =
                new VBox(
                        2,
                        title,
                        sub
                );

        Button close =
                FxComponents.smallBtn(
                        "Close",
                        this::dispose
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox top =
                new HBox(
                        12,
                        new LogoPanel(true),
                        text,
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
}