package ui;

public class DeveloperDashboardUI {

    public void setVisible(boolean visible) {
        if (!visible) {
            return;
        }

        FxComponents.runFx(() -> {
            if (!isAuthorized()) {
                new LoginUI().setVisible(true);
                return;
            }
            DashboardRouter.openDashboard(FxSolveStackApp.getPrimaryStage(), "Developer");
        });
    }

    private boolean isAuthorized() {
        UserSession session = UserSession.getInstance();
        return session.isLoggedIn() && session.hasRole("Developer");
    }
}

